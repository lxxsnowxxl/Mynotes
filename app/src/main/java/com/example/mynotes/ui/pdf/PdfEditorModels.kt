package com.example.mynotes.ui.pdf

import android.graphics.Bitmap

/** Coordenada normalizada dentro de una página PDF (0f..1f). */
data class PdfPoint(val x: Float, val y: Float)

data class PdfStroke(
    /** Identificador estable para seleccionar y editar el trazo después de guardarlo. */
    val id: Long = 0L,
    val points: List<PdfPoint>,
    val colorArgb: Int,
    val widthPt: Float
)

data class PdfTextElement(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val sizePt: Float,
    val colorArgb: Int
)

data class PdfImageElement(
    val id: Long,
    val bitmap: Bitmap,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotationDegrees: Float = 0f
)

data class PdfPageModel(
    val widthPt: Int = 595,
    val heightPt: Int = 842,
    val background: Bitmap? = null,
    /** Índice de la página dentro del PDF original. Null para páginas nuevas. */
    val sourcePdfPageIndex: Int? = null,
    val strokes: List<PdfStroke> = emptyList(),
    val texts: List<PdfTextElement> = emptyList(),
    val images: List<PdfImageElement> = emptyList()
) {
    companion object {
        fun blank(): PdfPageModel = PdfPageModel()
    }
}

/** Página original renderizada bajo demanda. */
data class PdfRenderedPage(
    val widthPt: Int,
    val heightPt: Int,
    val bitmap: Bitmap
)

enum class PdfEditorTool {
    SELECT,
    PEN,
    HIGHLIGHTER,
    LINE,
    RECTANGLE,
    ELLIPSE,
    TEXT,
    ERASER,
    PAGE
}
