package com.example.mynotes.ui.notes

import android.content.Context

data class IdleMotionOptions(
    val enabled: Boolean = true,
    val intensityLevel: Int = 3,
    val frequency: Int = 2,
    val styleId: String = IdleNoteMotionStyles.ANGULAR
)

/** Independent of AppSettings to avoid Room/backup migrations. */
internal object IdleMotionPreferences {
    private const val NAME = "idle_card_motion_v319"
    fun get(context: Context): IdleMotionOptions = context.applicationContext
        .getSharedPreferences(NAME, Context.MODE_PRIVATE).let { pref ->
            IdleMotionOptions(pref.getBoolean("enabled", true),
                pref.getInt("intensity", 3).coerceIn(1, 5), pref.getInt("frequency", 2).coerceIn(1, 3),
                IdleNoteMotionStyles.normalize(pref.getString("style_id", IdleNoteMotionStyles.ANGULAR)))
        }

    fun save(context: Context, options: IdleMotionOptions) {
        context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            .putBoolean("enabled", options.enabled)
            .putInt("intensity", options.intensityLevel.coerceIn(1, 5))
            .putInt("frequency", options.frequency.coerceIn(1, 3))
            .putString("style_id", IdleNoteMotionStyles.normalize(options.styleId)).apply()
    }
}
