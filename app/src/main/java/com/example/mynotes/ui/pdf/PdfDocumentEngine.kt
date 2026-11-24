package com.example.mynotes.ui.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

object PdfDocumentEngine {
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
                    require(renderer.pageCount <= 20000){"PDF contains too many pages"}
                    List(renderer.pageCount){index->renderer.openPage(index).use{page->PdfPageModel(widthPt=595,heightPt=(595f*page.height/page.width.coerceAtLeast(1)).roundToInt().coerceAtLeast(1),sourcePdfPageIndex=index)}}
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

    private fun renderPage(page: PdfRenderer.Page, rasterWidth: Int = PdfBitmapPolicy.RENDER_WIDTH): PdfRenderedPage {
        val ratio = page.height.toFloat() / page.width.toFloat().coerceAtLeast(1f)
        val rasterSize = PdfBitmapPolicy.rasterSize(page.width, page.height, rasterWidth)
        val targetWidth = rasterSize.width
        val targetHeight = rasterSize.height
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        val pageWidth = 595
        val pageHeight = (pageWidth * ratio).roundToInt().coerceAtLeast(1)
        return PdfRenderedPage(pageWidth, pageHeight, bitmap)
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
        val safeWidth = targetWidthPx.coerceIn(96, 720)
        var pageForPreview = page

        if (page.background == null && page.sourcePdfPageIndex != null && sourcePdfUri != null) {
            val descriptor = context.contentResolver.openFileDescriptor(sourcePdfUri, "r")
            descriptor?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    pageForPreview = page.withRenderedBackground(renderer, safeWidth)
                }
            }
        }

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

    suspend fun renderFlatPage(context:Context,page:PdfPageModel,source:Uri?,maxSide:Int=2400):Bitmap=withContext(Dispatchers.IO){
        var temporary:Bitmap?=null;var model=page
        if(page.background==null&&page.sourcePdfPageIndex!=null&&source!=null){val base=renderPdfPage(context,source,page.sourcePdfPageIndex);temporary=base.bitmap;model=page.copy(widthPt=base.widthPt,heightPt=base.heightPt,background=base.bitmap)}
        val scale=minOf(2f,maxSide.toFloat()/maxOf(model.widthPt,model.heightPt).coerceAtLeast(1));val bitmap=Bitmap.createBitmap((model.widthPt*scale).roundToInt().coerceAtLeast(1),(model.heightPt*scale).roundToInt().coerceAtLeast(1),Bitmap.Config.ARGB_8888)
        try{val canvas=Canvas(bitmap);canvas.scale(scale,scale);drawPage(canvas,model,Typeface.create("sans-serif",Typeface.NORMAL),Typeface.create("sans-serif",Typeface.BOLD));bitmap}catch(error:Throwable){bitmap.recycle();throw error}finally{temporary?.recycle()}
    }

    suspend fun loadImage(context: Context, uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val boundsStream = resolver.openInputStream(uri) ?: error("No se pudo abrir la imagen")
        boundsStream.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) error("Formato de imagen no compatible")
        val options = BitmapFactory.Options().apply {
            inSampleSize = PdfBitmapPolicy.imageSampleSize(bounds.outWidth, bounds.outHeight)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
            ?: error("Formato de imagen no compatible")
    }

    suspend fun exportPdf(
        context: Context,
        uri: Uri,
        pages: List<PdfPageModel>,
        sourcePdfUri: Uri? = null
    ) = withContext(Dispatchers.IO) {
        writePdfDocument(context, pages, sourcePdfUri) { document ->
            context.contentResolver.openOutputStream(uri, "w")?.use { document.writeTo(it) }
                ?: error("No se pudo crear el archivo PDF")
        }
    }

    private fun writePdfDocument(
        context: Context,
        pages: List<PdfPageModel>,
        sourcePdfUri: Uri?,
        write: (PdfDocument) -> Unit
    ) {
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
                    pageForExport = page.withRenderedBackground(sourceRenderer)
                }
                val info = PdfDocument.PageInfo.Builder(pageForExport.widthPt, pageForExport.heightPt, index + 1).create()
                val pdfPage = document.startPage(info)
                drawPage(pdfPage.canvas, pageForExport, font, boldFont)
                document.finishPage(pdfPage)
            }
            write(document)
        } finally {
            sourceRenderer?.close()
            sourceDescriptor?.close()
            document.close()
        }
    }

    private fun PdfPageModel.withRenderedBackground(renderer: PdfRenderer, rasterWidth: Int = PdfBitmapPolicy.RENDER_WIDTH): PdfPageModel {
        val index = sourcePdfPageIndex ?: return this
        if (index !in 0 until renderer.pageCount) return this
        return renderer.openPage(index).use { source ->
            val rendered = renderPage(source, rasterWidth)
            copy(widthPt = rendered.widthPt, heightPt = rendered.heightPt, background = rendered.bitmap)
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
            paint.reset(); paint.isAntiAlias = true; paint.strokeCap = Paint.Cap.ROUND; paint.strokeJoin = Paint.Join.ROUND
            paint.color = stroke.colorArgb
            if (stroke.points.size == 1) {
                val point = stroke.points.first(); paint.style = Paint.Style.FILL
                canvas.drawCircle(point.x * page.widthPt, point.y * page.heightPt, stroke.widthPt * point.pressure / 2, paint)
            } else if (stroke.points.all { it.pressure == 1f }) {
                val path = Path(); val first = stroke.points.first(); path.moveTo(first.x * page.widthPt, first.y * page.heightPt)
                stroke.points.drop(1).forEach { path.lineTo(it.x * page.widthPt, it.y * page.heightPt) }
                paint.style = Paint.Style.STROKE; paint.strokeWidth = stroke.widthPt; canvas.drawPath(path, paint)
            } else {
                paint.style = Paint.Style.STROKE
                stroke.points.zipWithNext().forEach { (a, b) ->
                    paint.strokeWidth = stroke.widthPt * (a.pressure + b.pressure) / 2
                    canvas.drawLine(a.x * page.widthPt, a.y * page.heightPt, b.x * page.widthPt, b.y * page.heightPt, paint)
                }
            }
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
