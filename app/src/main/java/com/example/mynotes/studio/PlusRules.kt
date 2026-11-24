package com.example.mynotes.studio

import com.example.mynotes.data.Note
import org.json.JSONObject

internal object PlusRules {
    fun matches(note: Note, meta: JSONObject, rule: JSONObject, media: Boolean): Boolean {
        val clauses = rule.rows("clauses")
        if (clauses.isEmpty()) return true
        fun test(row: JSONObject): Boolean {
            val field = row.optString("field")
            val value = row.optString("value")
            if (field == "after") return value.toLongOrNull()?.let { note.createdAt >= it } == true
            if (field == "before") return value.toLongOrNull()?.let { note.createdAt <= it } == true
            if (field == "media") return value.toBooleanStrictOrNull()?.let { media == it } == true
            val candidates = when (field) {
                "any" -> listOf(note.title, note.content)
                "title" -> listOf(note.title)
                "content" -> listOf(note.content)
                "folder" -> listOf(note.folderId.orEmpty())
                "priority" -> listOf(note.priority.toString())
                "stage" -> listOf(meta.optString("stage", "pending"))
                "tag" -> meta.strings("tags")
                "property" -> listOfNotNull(meta.optJSONObject("properties")?.optString(row.optString("key"))?.takeIf(String::isNotBlank))
                else -> return false
            }
            return when (row.optString("op", "contains")) {
                "equals" -> candidates.any { it.equals(value, true) }
                "not_contains" -> candidates.none { it.contains(value, true) }
                "exists" -> candidates.any(String::isNotBlank)
                else -> candidates.any { it.contains(value, true) }
            }
        }
        return if (rule.optString("relation", "and") == "or") clauses.any(::test) else clauses.all(::test)
    }
}
