package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import kotlin.math.roundToInt
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Attachment

internal data class NoteWidgetEnvironment(
    val textContext: Context,
    val theme: WidgetPresentation.WidgetThemeSpec,
    val visualMap: Map<Int, List<Attachment>>
)

internal suspend fun loadNoteWidgetEnvironment(
    context: Context,
    database: AppDatabase,
    noteIds: List<Int>
): NoteWidgetEnvironment {
    val appearance = loadWidgetAppearance(context)
    return NoteWidgetEnvironment(
        textContext = appearance.textContext,
        theme = appearance.theme,
        visualMap = if (noteIds.isEmpty()) emptyMap() else WidgetMediaPreview.visualsByNote(
            database.attachmentDao().getAttachmentsForNotesOnce(noteIds)
        )
    )
}

internal fun RemoteViews.setVisible(viewId: Int, visible: Boolean) {
    setViewVisibility(viewId, if (visible) View.VISIBLE else View.GONE)
}

internal data class WidgetPreviewSizeDp(
    val widthDp: Int,
    val heightDp: Int
)

internal data class WidgetSizeInfo(
    val widthDp: Int,
    val heightDp: Int
) {
    val isTall: Boolean get() = heightDp >= 260

    fun focusThumbnailSize(): WidgetPreviewSizeDp = if (isTall) {
        WidgetPreviewSizeDp(
            widthDp = (widthDp - 28).coerceAtLeast(90),
            heightDp = 92
        )
    } else {
        WidgetPreviewSizeDp(
            widthDp = 74,
            heightDp = (heightDp - 68).coerceIn(78, 180)
        )
    }

    fun cardThumbnailSize(): WidgetPreviewSizeDp = WidgetPreviewSizeDp(
        widthDp = ((widthDp - 44) / 2).coerceIn(58, 220),
        heightDp = if (isTall) 72 else 62
    )

    fun focusPreviewLength(): Int = scaledPreviewLength(
        base = when {
            heightDp >= 420 -> 420
            heightDp >= 330 -> 300
            heightDp >= 260 -> 210
            heightDp >= 200 -> 130
            else -> 82
        }
    )

    fun focusPreviewLines(): Int = when {
        heightDp >= 420 -> 16
        heightDp >= 330 -> 11
        heightDp >= 260 -> 8
        heightDp >= 200 -> 5
        else -> 3
    }

    fun cardPreviewLength(): Int = scaledPreviewLength(
        base = when {
            heightDp >= 420 -> 220
            heightDp >= 330 -> 150
            heightDp >= 260 -> 96
            heightDp >= 220 -> 54
            else -> 36
        }
    )

    fun cardPreviewLines(): Int = when {
        heightDp >= 420 -> 10
        heightDp >= 330 -> 7
        heightDp >= 260 -> 5
        heightDp >= 220 -> 2
        else -> 1
    }

    private fun scaledPreviewLength(base: Int): Int {
        val widthFactor = (widthDp / 190f).coerceIn(0.85f, 1.8f)
        return (base * widthFactor).toInt().coerceAtLeast(base)
    }
}

internal fun updateSizedWidget(
    context: Context,
    manager: AppWidgetManager,
    appWidgetId: Int,
    createViews: (WidgetSizeInfo) -> RemoteViews
) {
    val views = createSizedWidgetViews(
        manager.getAppWidgetOptions(appWidgetId),
        context.resources.configuration.orientation,
        createViews
    )
    manager.updateAppWidget(appWidgetId, views)
}

/** Android 12+ elige la vista que corresponde al tamaño real del widget en el launcher. */
internal fun createSizedWidgetViews(
    options: Bundle,
    orientation: Int,
    createViews: (WidgetSizeInfo) -> RemoteViews
): RemoteViews {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Esta sobrecarga también funciona en Android 13 sin depender del lector tipado de API 33.
        @Suppress("DEPRECATION")
        val sizes = options.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
            .orEmpty()
            .filter { it.width > 0f && it.height > 0f }
            .distinct()
        // RemoteViews admite como máximo 16 variantes. Un launcher sin medidas usa el fallback.
        if (sizes.size in 1..16) {
            return RemoteViews(sizes.associateWith { size ->
                createViews(WidgetSizeInfo(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1)))
            })
        }
    }
    return createViews(resolveWidgetSize(options, orientation))
}

private fun resolveWidgetSize(options: Bundle, orientation: Int): WidgetSizeInfo {
    fun positiveOption(key: String, fallback: Int): Int =
        options.getInt(key, fallback).takeIf { it > 0 } ?: fallback

    val minWidth = positiveOption(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 190)
    val maxWidth = positiveOption(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, minWidth)
    val minHeight = positiveOption(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180)
    val maxHeight = positiveOption(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, minHeight)

    val portrait = orientation != Configuration.ORIENTATION_LANDSCAPE
    val width = if (portrait) minWidth else maxWidth
    val height = if (portrait) maxHeight else minHeight
    return WidgetSizeInfo(
        widthDp = width.coerceAtLeast(1),
        heightDp = height.coerceAtLeast(1)
    )
}

internal fun previewPixels(context: Context, size: WidgetPreviewSizeDp): Pair<Int, Int> =
    previewPixels(context, size.widthDp, size.heightDp)

internal fun previewPixels(context: Context, widthDp: Int, heightDp: Int): Pair<Int, Int> {
    val density = context.resources.displayMetrics.density.coerceAtLeast(1f)
    val rawWidth = (widthDp.coerceAtLeast(1) * density).coerceAtLeast(1f)
    val rawHeight = (heightDp.coerceAtLeast(1) * density).coerceAtLeast(1f)
    val downScale = minOf(1f, 420f / rawWidth, 360f / rawHeight)
    val widthPx = (rawWidth * downScale).roundToInt().coerceAtLeast(48)
    val heightPx = (rawHeight * downScale).roundToInt().coerceAtLeast(48)
    return widthPx to heightPx
}
