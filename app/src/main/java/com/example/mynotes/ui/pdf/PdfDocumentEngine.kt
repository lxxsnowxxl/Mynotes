package com.example.mynotes.ui.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

object PdfDocumentEngine {
    private const val RENDER_WIDTH = 1200
    private const val MAX_BITMAP_SIDE = 2600

    /**
     * Abre únicamente la estructura del PDF y crea marcadores ligeros para
     * sus páginas. No rasteriza todas las páginas en memoria.
     */
    suspend fun loadPdfLazy(context: Context, uri: Uri): List<PdfPageModel> = withContext(Dispatchers.IO) {
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r")
            ?: error("No se pudo abrir el PDF")
        descriptor.use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                if (renderer.pageCount <= 0) {
                    listOf(PdfPageModel.blank())
                } else {
                    List(renderer.pageCount) { index ->
                        PdfPageModel(sourcePdfPageIndex = index)
                    }
                }
            }
        }
    }

    /** Rasteriza solamente la página solicitada del PDF original. */
    suspend fun renderPdfPage(context: Context, uri: Uri, pageIndex: Int): PdfRenderedPage = withContext(Dispatchers.IO) {
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r")
            ?: error("No se pudo abrir el PDF")
        descriptor.use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                require(pageIndex in 0 until renderer.pageCount) { "Página fuera de rango" }
                renderer.openPage(pageIndex).use { page ->
                    renderPage(page)
                }
            }
        }
    }

    private fun renderPage(page: PdfRenderer.Page): PdfRenderedPage {
        val ratio = page.height.toFloat() / page.width.toFloat().coerceAtLeast(1f)
        val targetWidth = RENDER_WIDTH
        val targetHeight = (targetWidth * ratio).roundToInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        val pageWidth = 595
        val pageHeight = (pageWidth * ratio).roundToInt().coerceAtLeast(1)
        return PdfRenderedPage(pageWidth, pageHeight, bitmap)
    }

    suspend fun loadPdf(context: Context, uri: Uri): List<PdfPageModel> = withContext(Dispatchers.IO) {
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r")
            ?: error("No se pudo abrir el PDF")
        descriptor.use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                buildList {
                    for (index in 0 until renderer.pageCount) {
                        renderer.openPage(index).use { page ->
                            val rendered = renderPage(page)
                            add(
                                PdfPageModel(
                                    widthPt = rendered.widthPt,
                                    heightPt = rendered.heightPt,
                                    background = rendered.bitmap,
                                    sourcePdfPageIndex = index
                                )
                            )
                        }
                    }
                }.ifEmpty { listOf(PdfPageModel.blank()) }
            }
        }
    }

    /**
     * Genera una miniatura de una página editable, incluyendo el PDF base y todos
     * los elementos añadidos en PDF Studio (trazos, textos e imágenes).
     */
    suspend fun renderProjectThumbnail(
        context: Context,
        page: PdfPageModel,
        sourcePdfUri: Uri?,
        targetWidthPx: Int = 260
    ): Bitmap = withContext(Dispatchers.IO) {
        var pageForPreview = page

        if (page.background == null && page.sourcePdfPageIndex != null && sourcePdfUri != null) {
            val descriptor = context.contentResolver.openFileDescriptor(sourcePdfUri, "r")
            descriptor?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    val sourceIndex = page.sourcePdfPageIndex
                    if (sourceIndex in 0 until renderer.pageCount) {
                        renderer.openPage(sourceIndex).use { originalPage ->
                            val rendered = renderPage(originalPage)
                            pageForPreview = page.copy(
                                widthPt = rendered.widthPt,
                                heightPt = rendered.heightPt,
                                background = rendered.bitmap
                            )
                        }
                    }
                }
            }
        }

        val safeWidth = targetWidthPx.coerceIn(96, 720)
        val ratio = pageForPreview.heightPt.toFloat() / pageForPreview.widthPt.toFloat().coerceAtLeast(1f)
        val safeHeight = (safeWidth * ratio).roundToInt().coerceIn(128, 1024)
        val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val font = Typeface.create("sans-serif", Typeface.NORMAL)
        val boldFont = Typeface.create("sans-serif", Typeface.BOLD)

        canvas.save()
        canvas.scale(
            safeWidth / pageForPreview.widthPt.toFloat().coerceAtLeast(1f),
            safeHeight / pageForPreview.heightPt.toFloat().coerceAtLeast(1f)
        )
        drawPage(canvas, pageForPreview, font, boldFont)
        canvas.restore()
        bitmap
    }

    suspend fun loadImage(context: Context, uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("No se pudo abrir la imagen")
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / sample > MAX_BITMAP_SIDE) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            ?: error("Formato de imagen no compatible")
    }

    suspend fun exportPdf(
        context: Context,
        uri: Uri,
        pages: List<PdfPageModel>,
        sourcePdfUri: Uri? = null
    ) = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        val font = Typeface.create("sans-serif", Typeface.NORMAL)
        val boldFont = Typeface.create("sans-serif", Typeface.BOLD)
        var sourceDescriptor: android.os.ParcelFileDescriptor? = null
        var sourceRenderer: PdfRenderer? = null
        try {
            if (sourcePdfUri != null && pages.any { it.sourcePdfPageIndex != null && it.background == null }) {
                sourceDescriptor = context.contentResolver.openFileDescriptor(sourcePdfUri, "r")
                sourceRenderer = sourceDescriptor?.let { PdfRenderer(it) }
            }

            pages.forEachIndexed { index, page ->
                var pageForExport = page
                if (page.background == null && page.sourcePdfPageIndex != null && sourceRenderer != null) {
                    val sourceIndex = page.sourcePdfPageIndex
                    if (sourceIndex in 0 until sourceRenderer.pageCount) {
                        sourceRenderer.openPage(sourceIndex).use { originalPage ->
                            val rendered = renderPage(originalPage)
                            pageForExport = page.copy(
                                widthPt = rendered.widthPt,
                                heightPt = rendered.heightPt,
                                background = rendered.bitmap
                            )
                        }
                    }
                }

                val info = PdfDocument.PageInfo.Builder(
                    pageForExport.widthPt,
                    pageForExport.heightPt,
                    index + 1
                ).create()
                val pdfPage = document.startPage(info)
                drawPage(pdfPage.canvas, pageForExport, font, boldFont)
                document.finishPage(pdfPage)
            }
            context.contentResolver.openOutputStream(uri, "w")?.use { output ->
                document.writeTo(output)
            } ?: error("No se pudo crear el archivo PDF")
        } finally {
            sourceRenderer?.close()
            sourceDescriptor?.close()
            document.close()
        }
    }


    /** Guarda una copia PDF dentro del almacenamiento privado de MyNotes. */
    suspend fun exportPdfToFile(
        context: Context,
        file: File,
        pages: List<PdfPageModel>,
        sourcePdfUri: Uri? = null
    ) = withContext(Dispatchers.IO) {
        file.parentFile?.mkdirs()
        val document = PdfDocument()
        val font = Typeface.create("sans-serif", Typeface.NORMAL)
        val boldFont = Typeface.create("sans-serif", Typeface.BOLD)
        var sourceDescriptor: android.os.ParcelFileDescriptor? = null
        var sourceRenderer: PdfRenderer? = null
        try {
            if (sourcePdfUri != null && pages.any { it.sourcePdfPageIndex != null && it.background == null }) {
                sourceDescriptor = context.contentResolver.openFileDescriptor(sourcePdfUri, "r")
                sourceRenderer = sourceDescriptor?.let { PdfRenderer(it) }
            }

            pages.forEachIndexed { index, page ->
                var pageForExport = page
                if (page.background == null && page.sourcePdfPageIndex != null && sourceRenderer != null) {
                    val sourceIndex = page.sourcePdfPageIndex
                    if (sourceIndex in 0 until sourceRenderer.pageCount) {
                        sourceRenderer.openPage(sourceIndex).use { originalPage ->
                            val rendered = renderPage(originalPage)
                            pageForExport = page.copy(
                                widthPt = rendered.widthPt,
                                heightPt = rendered.heightPt,
                                background = rendered.bitmap
                            )
                        }
                    }
                }

                val info = PdfDocument.PageInfo.Builder(
                    pageForExport.widthPt,
                    pageForExport.heightPt,
                    index + 1
                ).create()
                val pdfPage = document.startPage(info)
                drawPage(pdfPage.canvas, pageForExport, font, boldFont)
                document.finishPage(pdfPage)
            }
            FileOutputStream(file).use { document.writeTo(it) }
        } finally {
            sourceRenderer?.close()
            sourceDescriptor?.close()
            document.close()
        }
    }

    private fun drawPage(canvas: Canvas, page: PdfPageModel, typeface: Typeface, boldTypeface: Typeface) {
        canvas.drawColor(Color.WHITE)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        page.background?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, Rect(0, 0, page.widthPt, page.heightPt), paint)
        }

        page.strokes.forEach { stroke ->
            if (stroke.points.isEmpty()) return@forEach
            val path = Path()
            val first = stroke.points.first()
            path.moveTo(first.x * page.widthPt, first.y * page.heightPt)
            stroke.points.drop(1).forEach { point ->
                path.lineTo(point.x * page.widthPt, point.y * page.heightPt)
            }
            paint.reset()
            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = stroke.widthPt
            paint.color = stroke.colorArgb
            canvas.drawPath(path, paint)
        }

        page.images.forEach { image ->
            val left = image.x * page.widthPt
            val top = image.y * page.heightPt
            val right = (image.x + image.width) * page.widthPt
            val bottom = (image.y + image.height) * page.heightPt
            val rect = RectF(left, top, right, bottom)
            canvas.save()
            canvas.rotate(image.rotationDegrees, rect.centerX(), rect.centerY())
            paint.reset()
            paint.isAntiAlias = true
            paint.isFilterBitmap = true
            canvas.drawBitmap(image.bitmap, null, rect, paint)
            canvas.restore()
        }

        page.texts.forEach { text ->
            paint.reset()
            paint.isAntiAlias = true
            paint.color = text.colorArgb
            paint.textSize = text.sizePt
            paint.typeface = if (text.sizePt >= 20f) boldTypeface else typeface
            val baseX = text.x * page.widthPt
            var baseY = text.y * page.heightPt
            val lineHeight = text.sizePt * 1.24f
            text.text.lines().forEach { line ->
                canvas.drawText(line, baseX, baseY, paint)
                baseY += lineHeight
            }
        }
    }
}
