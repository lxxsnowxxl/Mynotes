package com.example.mynotes.ui.workspace

/** Dimensions in dp for the horizontally retractable note tools drawer. */
internal object NoteToolsDrawerGeometry {
    // El tirador debe caber en el margen del editor, sin cubrir los caracteres.
    const val tabWidthDp = 48f
    const val tabHeightDp = 64f
    const val tabTopInsetDp = 8f
    const val maxDrawerWidthDp = 480f
    const val contentStartInsetDp = tabWidthDp + 16f
    private const val defaultVerticalOffsetFraction = 0.40f

    fun drawerWidthDp(availableWidthDp: Float): Float =
        (availableWidthDp - 8f).coerceIn(tabWidthDp, maxDrawerWidthDp)

    /** El movimiento vertical se limita a la zona disponible del editor. */
    fun maxVerticalOffsetPx(viewportHeightPx: Int, rowHeightPx: Int): Float =
        (viewportHeightPx - rowHeightPx).coerceAtLeast(0).toFloat()

    fun clampVerticalOffsetPx(offsetPx: Float, viewportHeightPx: Int, rowHeightPx: Int): Float =
        (if (offsetPx.isFinite()) offsetPx else 0f)
            .coerceIn(0f, maxVerticalOffsetPx(viewportHeightPx, rowHeightPx))

    /** Altura inicial: deja visible el editor superior y sitúa la pestaña a media pantalla. */
    fun defaultVerticalOffsetPx(viewportHeightPx: Int, rowHeightPx: Int): Float =
        clampVerticalOffsetPx(viewportHeightPx * defaultVerticalOffsetFraction, viewportHeightPx, rowHeightPx)

    /** Negative offset moves the entire tools panel left, leaving the tab visible. */
    fun closedOffsetDp(drawerWidthDp: Float): Float =
        -(drawerWidthDp - tabWidthDp).coerceAtLeast(0f)
}
