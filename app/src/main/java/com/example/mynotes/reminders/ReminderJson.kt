package com.example.mynotes.reminders

import org.json.JSONArray
import org.json.JSONObject

internal fun List<Reminder>.toJsonString(): String = JSONArray().apply {
    this@toJsonString.forEach { put(it.toJson()) }
}.toString()

internal fun remindersFromJson(raw: String): List<Reminder> {
    val array = JSONArray(raw)
    return List(array.length()) { index -> array.getJSONObject(index).toReminder(index) }
}

private fun Reminder.toJson() = JSONObject().apply {
    put("id", id);put("libraryId",libraryId)
    put("title", title)
    put("description", description)
    put("triggerAtMillis", triggerAtMillis)
    put("repeatMode", repeatMode)
    put("priority", priority)
    put("colorKey", colorKey)
    put("enabled", enabled)
    put("createdAt", createdAt)
    put("weekDays", JSONArray(weekDays.filter { it in 1..7 }.sorted()))
    put("repeatEndAtMillis", repeatEndAtMillis ?: JSONObject.NULL)
    put("linkedNoteId", linkedNoteId ?: JSONObject.NULL)
    put("snoozedUntil", snoozedUntil ?: JSONObject.NULL)
    put("repeatInterval",repeatInterval);put("monthOrdinal",monthOrdinal);put("monthWeekday",monthWeekday);put("recurrenceAnchorMillis",recurrenceAnchorMillis?:JSONObject.NULL);put("maxOccurrences",maxOccurrences?:JSONObject.NULL);put("occurrencesDone",occurrencesDone);put("alertMinutes",JSONArray(alertMinutes));put("locationOnly",locationOnly);put("excludedOccurrences",JSONArray(excludedOccurrences.sorted()))
}

private fun JSONObject.toReminder(index: Int) = Reminder(
    id = optLong("id", System.currentTimeMillis() + index),
    libraryId=optString("libraryId","default").takeIf{it.matches(Regex("[a-zA-Z0-9-]{1,80}"))}?:"default",
    title = optString("title"),
    description = optString("description"),
    triggerAtMillis = optLong("triggerAtMillis"),
    repeatMode = optString("repeatMode", Reminder.REPEAT_NONE),
    priority = optString("priority", Reminder.PRIORITY_NORMAL),
    colorKey = optString("colorKey", "palette"),
    enabled = optBoolean("enabled", true),
    createdAt = optLong("createdAt", System.currentTimeMillis()),
    weekDays = optJSONArray("weekDays")?.let { days -> (0 until days.length()).map { days.optInt(it) }.filter { it in 1..7 }.toSet() }.orEmpty(),
    repeatEndAtMillis = if (has("repeatEndAtMillis") && !isNull("repeatEndAtMillis")) optLong("repeatEndAtMillis") else null,
    linkedNoteId = if (has("linkedNoteId") && !isNull("linkedNoteId")) optInt("linkedNoteId").takeIf { it > 0 } else null,
    snoozedUntil = if (has("snoozedUntil") && !isNull("snoozedUntil")) optLong("snoozedUntil") else null,
    repeatInterval=optInt("repeatInterval",1).coerceIn(1,365),monthOrdinal=optInt("monthOrdinal").coerceIn(-1,5),monthWeekday=optInt("monthWeekday",2).coerceIn(1,7),
    recurrenceAnchorMillis=if(isNull("recurrenceAnchorMillis"))null else optLong("recurrenceAnchorMillis").takeIf{it>0},
    maxOccurrences=if(isNull("maxOccurrences"))null else optInt("maxOccurrences").takeIf{it in 1..100000},occurrencesDone=optInt("occurrencesDone").coerceAtLeast(0),
    alertMinutes=optJSONArray("alertMinutes")?.let{a->(0 until a.length()).map{a.optInt(it)}.filter{it in 1..10080}.distinct().sorted().take(12)}.orEmpty(),locationOnly=optBoolean("locationOnly"),excludedOccurrences=optJSONArray("excludedOccurrences")?.let{a->(0 until a.length()).map{a.optLong(it)}.filter{it>0}.distinct().take(1000).toSet()}.orEmpty()
)
