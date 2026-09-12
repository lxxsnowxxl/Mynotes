package com.example.mynotes.data

import android.net.Uri

data class PendingAttachment(

    val uri: Uri,

    /*
     * image
     * video
     * audio
     * voice
     * file
     */
    val type: String,

    val name: String? = null,

    val mimeType: String? = null
)