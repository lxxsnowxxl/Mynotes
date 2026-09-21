package com.example.mynotes.widget

object WidgetActions {
    const val ACTION_NEW_NOTE = "com.example.mynotes.widget.NEW_NOTE"
    const val ACTION_OPEN_NOTE = "com.example.mynotes.widget.OPEN_NOTE"
    const val ACTION_OPEN_COLLECTION = "com.example.mynotes.widget.OPEN_COLLECTION"
    const val ACTION_SEARCH = "com.example.mynotes.widget.SEARCH"
    const val ACTION_TOGGLE_FAVORITE = "com.example.mynotes.widget.TOGGLE_FAVORITE"
    const val ACTION_TOGGLE_PIN = "com.example.mynotes.widget.TOGGLE_PIN"

    const val EXTRA_NOTE_ID = "widget_note_id"
    const val EXTRA_COLLECTION = "widget_collection"

    const val COLLECTION_ALL = "all"
    const val COLLECTION_FAVORITES = "favorites"
    const val COLLECTION_PINNED = "pinned"
    const val COLLECTION_PRIORITY = "priority"
    const val COLLECTION_WORK = "work"
    const val COLLECTION_PERSONAL = "personal"
    const val COLLECTION_IMAGES = "images"
    const val COLLECTION_FILES = "files"
}
