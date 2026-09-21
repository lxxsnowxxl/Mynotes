package com.example.mynotes.ui.components

import androidx.compose.runtime.Immutable
import com.example.mynotes.settings.AppSettings

@Immutable
data class NoteCardStyle(val cornerRadius: Float, val elevation: Float, val padding: Float, val imageHeight: Float,
    val outlineEnabled: Boolean, val outlineWidth: Float, val titleMaxLines: Int,
    val contentMaxLines: Int, val lineSpacing: Float, val iconSize: Float, val showDate: Boolean, val showCategory: Boolean,
    val showFavorite: Boolean, val animationsEnabled: Boolean, val animationSpeed: Float)
fun AppSettings.toNoteCardStyle() = NoteCardStyle(cornerRadius = noteCardCornerRadius, elevation = noteCardElevation,
        padding = noteCardPadding, imageHeight = noteCardImageHeight, outlineEnabled = noteCardOutlineWidth > 0.01f,
        outlineWidth = noteCardOutlineWidth.coerceIn(0f, 6f), titleMaxLines = noteTitleMaxLines,
        contentMaxLines = noteContentMaxLines, lineSpacing = noteLineSpacing, iconSize = iconSize, showDate = showNoteDate,
        showCategory = showCategoryChip, showFavorite = showFavoriteIcon, animationsEnabled = animationsEnabled,
        animationSpeed = animationSpeed)
