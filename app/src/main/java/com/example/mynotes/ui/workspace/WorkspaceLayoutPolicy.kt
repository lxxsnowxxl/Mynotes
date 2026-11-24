package com.example.mynotes.ui.workspace

/** Dimensions use the current window, after insets, rather than the device model. */
internal object WorkspaceLayoutPolicy {
    const val dividerWidth = 48f
    private fun scale(fontScale: Float) = if (fontScale.isFinite()) fontScale.coerceIn(1f, 2f) else 1f
    fun minimumPanelWidth(fontScale: Float) = 240f * scale(fontScale)
    fun useTwoPanes(width: Float, height: Float, fontScale: Float): Boolean =
        width.isFinite() && height.isFinite() && height >= 480f &&
            width >= maxOf(840f, minimumPanelWidth(fontScale) * 2f + dividerWidth)

    fun panelFraction(requested: Float, width: Float, fontScale: Float): Float {
        val available = (width - dividerWidth).coerceAtLeast(1f)
        val minimum = (minimumPanelWidth(fontScale) / available).coerceIn(0f, .5f)
        val safe = if (requested.isFinite()) requested else .64f
        return safe.coerceIn(minimum, 1f - minimum)
    }

    fun compactPrimaryAction(width: Float, fontScale: Float) = width < 360f || scale(fontScale) > 1.4f
}
