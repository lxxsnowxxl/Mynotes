package com.example.mynotes.ui.preview

import android.graphics.Bitmap
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.mynotes.ui.pdf.PdfPageModel
import com.example.mynotes.ui.pdf.PdfProjectRepository

/**
 * Datos suministrados únicamente por las entradas de diseño de src/debug.
 * null en la app: no altera el estado inicial ni las cargas de producción.
 * Las vistas previas comparten los composables; sólo sustituyen datos y servicios.
 */
internal data class LayoutPreviewData(
    val query: String = "",
    val settingsItemIndex: Int = 0,
    val noteMenu: String? = null,
    val menuNoteId: Int = 0,
    val deleteDialog: Boolean = false,
    val quickCreateExpanded: Boolean = false,
    val recording: Boolean = false,
    val expandedDropdownTitle: String? = null,
    val mediaBitmap: Bitmap? = null,
    val documentBitmap: Bitmap? = null,
    val viewerText: String = "",
    val durationMillis: Int = 0,
    val positionMillis: Int = 0,
    val pdfPages: List<PdfPageModel> = emptyList(),
    val pdfProjects: List<PdfProjectRepository.Summary> = emptyList(),
    val reminderEditingId: Long? = null,
    val linkTitle: String = "",
    val linkDescription: String = ""
)

internal val LocalLayoutPreview = staticCompositionLocalOf<LayoutPreviewData?> { null }
