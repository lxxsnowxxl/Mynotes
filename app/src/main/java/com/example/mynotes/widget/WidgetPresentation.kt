package com.example.mynotes.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.example.mynotes.R
import com.example.mynotes.data.Note
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.ui.theme.PaletteCatalog
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date

object WidgetPresentation {

    private val WidgetEmbeddedLinkMarkerRegex = Regex(
        pattern = """(?m)^\s*\[\[mynotes-link:https?://[^\]]+]]\s*(?:\r?\n)?""",
        option = RegexOption.IGNORE_CASE
    )
    private val WidgetUrlRegex = Regex("""https?://[^\s<>"']+""", RegexOption.IGNORE_CASE)

    private fun visibleNoteContent(raw: String): String = raw
        .replace(WidgetEmbeddedLinkMarkerRegex, "")
        .replace(WidgetUrlRegex, "")
        .replace(Regex("""\n{3,}"""), "\n\n")
        .trim()

    data class WidgetThemeSpec(
        val dark: Boolean,
        val rootBackgroundRes: Int,
        val cardBackgroundRes: Int,
        val noteRowBackgroundRes: Int,
        val detailCardBackgroundRes: Int,
        val metricCardBackgroundRes: Int,
        val actionBackgroundRes: Int,
        val primaryActionBackgroundRes: Int,
        val textPrimary: Int,
        val textSecondary: Int,
        val textMuted: Int,
        val onAction: Int,
        val onPrimaryAction: Int,
        val accent: Int,
        val countBadgeBackgroundRes: Int,
        val countBadgeText: Int,
        val thumbnailBackgroundRes: Int,
        val chipBackgroundRes: Int,
        val rootBackgroundColor: Int,
        val cardBackgroundColor: Int,
        val noteRowBackgroundColor: Int,
        val detailCardBackgroundColor: Int,
        val metricCardBackgroundColor: Int,
        val thumbnailBackgroundColor: Int,
        val chipBackgroundColor: Int
    )

    suspend fun theme(context: Context): WidgetThemeSpec {
        return runCatching {
            val settings = SettingsRepository(context.applicationContext).settings.first()
            val palette = PaletteCatalog.find(settings.backgroundColor)
            val toneIndex = settings.backgroundToneIndex.coerceIn(0, 3)
            val selectedTone = palette.tones.getOrElse(toneIndex) { palette.tones.first() }
            val dark = settings.darkMode || selectedTone.luminance() < 0.48f || toneIndex >= 2
            val key = palette.key.replace(Regex("[^a-z0-9_]"), "_")

            /*
             * Compact widget theming:
             *
             * Only the rounded ROOT background needs a palette/tone-specific
             * drawable. Internal cards/actions use shared translucent shapes,
             * so the palette underneath remains visible without generating
             * thousands of nearly identical XML resources.
             *
             * This keeps RemoteViews compatible with API 28/Samsung launchers
             * and avoids the setBackgroundTintList issue that previously caused
             * partially rendered widgets.
             */
            fun themedRoot(fallback: Int): Int {
                val name = "widget_palette_${key}_${toneIndex}_root"
                val id = context.resources.getIdentifier(name, "drawable", context.packageName)
                return if (id != 0) id else fallback
            }

            val actionColor = if (dark) {
                blend(selectedTone, Color.Black, 0.30f)
            } else {
                blend(selectedTone, Color.White, 0.18f)
            }
            val primaryColor = if (dark) {
                blend(selectedTone, Color.Black, 0.46f)
            } else {
                blend(selectedTone, Color.Black, 0.36f)
            }
            val countColor = if (dark) {
                blend(selectedTone, Color.White, 0.82f)
            } else {
                blend(selectedTone, Color.Black, 0.42f)
            }

            WidgetThemeSpec(
                dark = dark,
                rootBackgroundRes = themedRoot(if (dark) R.drawable.widget_surface_screen_dark else R.drawable.widget_surface_screen_light),
                cardBackgroundRes = if (dark) R.drawable.widget_panel_card_dark else R.drawable.widget_panel_card_light,
                noteRowBackgroundRes = if (dark) R.drawable.widget_note_card_dark else R.drawable.widget_note_card_light,
                detailCardBackgroundRes = if (dark) R.drawable.widget_detail_card_dark else R.drawable.widget_detail_card_light,
                metricCardBackgroundRes = if (dark) R.drawable.widget_metric_card_dark else R.drawable.widget_metric_card_light,
                actionBackgroundRes = if (dark) R.drawable.widget_action_circle_dark else R.drawable.widget_action_circle_light,
                primaryActionBackgroundRes = if (dark) R.drawable.widget_action_primary_dark else R.drawable.widget_action_primary_light,
                textPrimary = if (dark) 0xFFF6F4FF.toInt() else 0xFF0F1115.toInt(),
                textSecondary = if (dark) 0xFFE5E6F5.toInt() else 0xFF62594F.toInt(),
                textMuted = if (dark) 0xFFC7CAE4.toInt() else 0xFF8B8178.toInt(),
                onAction = contrastText(actionColor),
                onPrimaryAction = contrastText(primaryColor),
                accent = palette.accent.toArgb(),
                countBadgeBackgroundRes = if (dark) R.drawable.widget_count_pill_dark else R.drawable.widget_count_pill_light,
                countBadgeText = contrastText(countColor),
                thumbnailBackgroundRes = if (dark) R.drawable.widget_thumb_background_dark else R.drawable.widget_thumb_background_light,
                chipBackgroundRes = if (dark) R.drawable.widget_chip_dark else R.drawable.widget_chip_light,
                rootBackgroundColor = selectedTone.toArgb(),
                cardBackgroundColor = selectedTone.toArgb(),
                noteRowBackgroundColor = selectedTone.toArgb(),
                detailCardBackgroundColor = selectedTone.toArgb(),
                metricCardBackgroundColor = selectedTone.toArgb(),
                thumbnailBackgroundColor = selectedTone.toArgb(),
                chipBackgroundColor = selectedTone.toArgb()
            )
        }.getOrDefault(
            WidgetThemeSpec(
                dark = true,
                rootBackgroundRes = R.drawable.widget_surface_screen_dark,
                cardBackgroundRes = R.drawable.widget_panel_card_dark,
                noteRowBackgroundRes = R.drawable.widget_note_card_dark,
                detailCardBackgroundRes = R.drawable.widget_detail_card_dark,
                metricCardBackgroundRes = R.drawable.widget_metric_card_dark,
                actionBackgroundRes = R.drawable.widget_action_circle_dark,
                primaryActionBackgroundRes = R.drawable.widget_action_primary_dark,
                textPrimary = 0xFFF6F4FF.toInt(),
                textSecondary = 0xFFD3D6FF.toInt(),
                textMuted = 0xFFB2B6E5.toInt(),
                onAction = 0xFFFFFFFF.toInt(),
                onPrimaryAction = 0xFFFFFFFF.toInt(),
                accent = 0xFF666666.toInt(),
                countBadgeBackgroundRes = R.drawable.widget_count_pill_dark,
                countBadgeText = 0xFF2F2A31.toInt(),
                thumbnailBackgroundRes = R.drawable.widget_thumb_background_dark,
                chipBackgroundRes = R.drawable.widget_chip_dark,
                rootBackgroundColor = 0xFF5F5F5F.toInt(),
                cardBackgroundColor = 0xFF767676.toInt(),
                noteRowBackgroundColor = 0xFF858585.toInt(),
                detailCardBackgroundColor = 0xFF8B8B8B.toInt(),
                metricCardBackgroundColor = 0xFF808080.toInt(),
                thumbnailBackgroundColor = 0xFF707070.toInt(),
                chipBackgroundColor = 0xFF555555.toInt()
            )
        )
    }

    suspend fun accentColor(context: Context): Int = theme(context).accent

    fun noteAccentColor(note: Note, fallback: Int): Int = when (note.color) {
        "purple" -> 0xFFB388FF.toInt()
        "yellow" -> 0xFFE8B64D.toInt()
        "pink" -> 0xFFFF8FB1.toInt()
        "green" -> 0xFF7DDC9C.toInt()
        "blue" -> 0xFF74B9FF.toInt()
        "orange" -> 0xFFFFAB66.toInt()
        "red" -> 0xFFFF7E7E.toInt()
        "cyan" -> 0xFF5EDAE0.toInt()
        "teal" -> 0xFF5FD2BE.toInt()
        "mint" -> 0xFF7CE6B3.toInt()
        "lime" -> 0xFFC8E86B.toInt()
        "brown" -> 0xFFC89F7A.toInt()
        "gray" -> 0xFFAAB3BD.toInt()
        else -> fallback
    }

    fun title(context: Context, note: Note): String {
        return note.title.trim().takeIf { it.isNotEmpty() }
            ?: visibleNoteContent(note.content).lineSequence().firstOrNull { it.isNotBlank() }?.trim()
            ?: context.getString(R.string.widget_untitled_note)
    }

    fun preview(context: Context, note: Note): String {
        val normalizedTitle = note.title.trim()
        val lines = visibleNoteContent(note.content).lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        val contentLine = lines.firstOrNull().orEmpty()
        if (contentLine.isNotEmpty() && contentLine != normalizedTitle) return contentLine
        return when {
            note.isPinned -> context.getString(R.string.widget_pinned_note)
            note.isFavorite -> context.getString(R.string.widget_favorite_note)
            note.priority > 0 -> priorityLabel(context, note.priority)
            else -> context.getString(R.string.widget_tap_to_open)
        }
    }

    fun contentPreview(note: Note, maxLength: Int = 170): String {
        val merged = visibleNoteContent(note.content).lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
        return when {
            merged.isBlank() -> ""
            merged.length <= maxLength -> merged
            else -> merged.take(maxLength - 1).trimEnd() + "…"
        }
    }

    fun badge(context: Context, note: Note): String = when {
        note.isPinned -> context.getString(R.string.widget_badge_pinned)
        note.isFavorite -> "★"
        note.priority == 3 -> "P3"
        note.priority == 2 -> "P2"
        note.priority == 1 -> "P1"
        note.category == "work" -> context.getString(R.string.widget_badge_work)
        else -> context.getString(R.string.widget_badge_personal)
    }

    fun metadata(context: Context, note: Note): String {
        val parts = buildList {
            if (note.priority > 0) add(priorityLabel(context, note.priority))
            add(
                if (note.category == "work") context.getString(R.string.widget_category_work)
                else context.getString(R.string.widget_category_personal)
            )
            add(shortDate(context, note.createdAt))
        }
        return parts.joinToString("  •  ")
    }

    fun shortDate(context: Context, timestamp: Long): String {
        return runCatching {
            SimpleDateFormat("MMM d, yyyy", WidgetLocale.locale(context)).format(Date(timestamp))
        }.getOrDefault("")
    }

    fun priorityLabel(context: Context, priority: Int): String = when (priority) {
        3 -> context.getString(R.string.mock_priority_high)
        2 -> context.getString(R.string.mock_priority_medium)
        1 -> context.getString(R.string.mock_priority_low)
        else -> context.getString(R.string.mock_priority_none)
    }

    private fun contrastText(background: Color): Int {
        return if (background.luminance() > 0.48f) {
            0xFF111111.toInt()
        } else {
            0xFFFFFFFF.toInt()
        }
    }

    private fun blend(base: Color, overlay: Color, amount: Float): Color {
        val t = amount.coerceIn(0f, 1f)
        return Color(
            red = base.red + (overlay.red - base.red) * t,
            green = base.green + (overlay.green - base.green) * t,
            blue = base.blue + (overlay.blue - base.blue) * t,
            alpha = 1f
        )
    }

    private fun applyIntensity(color: Color, intensity: Float, dark: Boolean): Color {
        val normalized = (intensity.coerceIn(0f, 100f) / 100f)
        val target = if (dark) Color.Black else Color.White
        val amount = if (dark) normalized * 0.12f else normalized * 0.10f
        return blend(color, target, amount)
    }
}
