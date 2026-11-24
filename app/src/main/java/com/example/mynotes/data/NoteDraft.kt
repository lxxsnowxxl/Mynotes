package com.example.mynotes.data

/** A single editor result keeps media, cover and collection changes in one save operation. */
data class NoteDraft(
    val title: String, val content: String, val color: String,
    val attachments: List<PendingAttachment>, val removedAttachments: List<Attachment>,
    val folderId: String? = null, val coverIcon: String? = null,
    val coverX: Float = 0.5f, val coverY: Float = 0.5f,
    val removeCover: Boolean = false, val formattingEnabled: Boolean = false,
    /** Editor session only; never written to the note table. */
    val workspaceSession: String? = null,
    val workspaceModifiedAt: Long = 0L
)
