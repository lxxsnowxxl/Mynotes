package com.example.mynotes.reminders

import org.json.JSONArray
import org.json.JSONObject

internal fun List<Reminder>.toJsonString(): String = JSONArray().apply {
    forEach { put(it.toJson()) }
}.toString()

internal fun remindersFromJson(raw: String): List<Reminder> {
    val array = JSONArray(raw)
    return List(array.length()) { index -> array.getJSONObject(index).toReminder(index) }
}

private fun Reminder.toJson() = JSONObject().apply {
    put("id", id)
    put("title", title)
    put("description", description)
    put("triggerAtMillis", triggerAtMillis)
    put("repeatMode", repeatMode)
    put("priority", priority)
    put("colorKey", colorKey)
    put("enabled", enabled)
    put("createdAt", createdAt)
}

private fun JSONObject.toReminder(index: Int) = Reminder(
    id = optLong("id", System.currentTimeMillis() + index),
    title = optString("title"),
    description = optString("description"),
    triggerAtMillis = optLong("triggerAtMillis"),
    repeatMode = optString("repeatMode", Reminder.REPEAT_NONE),
    priority = optString("priority", Reminder.PRIORITY_NORMAL),
    colorKey = optString("colorKey", "palette"),
    enabled = optBoolean("enabled", true),
    createdAt = optLong("createdAt", System.currentTimeMillis())
)
