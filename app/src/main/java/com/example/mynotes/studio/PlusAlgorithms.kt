package com.example.mynotes.studio

import org.json.JSONArray
import java.util.Locale
import kotlin.math.*

internal object PlusAlgorithms {
    fun tags(raw: String): List<String> = raw.split(',', '\n').map { it.trim().removePrefix("#").take(40) }
        .filter(String::isNotBlank).distinctBy { it.lowercase(Locale.ROOT) }.take(30)

    fun ordered(ids: List<String>, saved: List<String>): List<String> = saved.filter { it in ids }.distinct() + ids.filter { it !in saved }

    fun <T> move(items: List<T>, index: Int, delta: Int): List<T> {
        if (index !in items.indices) return items
        val to = (index + delta).coerceIn(items.indices)
        return items.toMutableList().apply { val value = removeAt(index); add(to, value) }
    }

    data class Replacement(val text: String, val count: Int)
    fun replace(source: String, query: String, replacement: String, ignoreCase: Boolean, all: Boolean, from: Int = 0): Replacement {
        if (query.isEmpty()) return Replacement(source, 0)
        val result = StringBuilder()
        var offset = 0
        var count = 0
        while (offset <= source.length) {
            val at = source.indexOf(query, if (count == 0) maxOf(offset, from.coerceIn(0, source.length)) else offset, ignoreCase)
            if (at < 0 || (!all && count > 0)) break
            result.append(source, offset, at).append(replacement)
            offset = at + query.length
            count++
        }
        return Replacement(result.append(source, offset, source.length).toString(), count)
    }

    fun templateFields(source: String): List<String> = Regex("\\{\\{([\\p{L}\\p{N}_ -]{1,60})}}").findAll(source)
        .map { it.groupValues[1] }.distinct().take(40).toList()

    fun fillTemplate(source: String, fields: Map<String, String>): String = Regex("\\{\\{([\\p{L}\\p{N}_ -]{1,60})}}").replace(source) {
        fields[it.groupValues[1]] ?: it.value
    }

    data class LinkCompletion(val start: Int, val end: Int, val query: String)
    fun completion(text: String, cursor: Int): LinkCompletion? {
        val end = cursor.coerceIn(0, text.length)
        val start = text.lastIndexOf("[[", end - 1)
        if (start < 0 || end - start < 2 || end - start > 82) return null
        val query = text.substring(start + 2, end)
        return LinkCompletion(start, end, query).takeIf { '\n' !in query && ']' !in query }
    }

    data class Paragraph(val start: Int, val end: Int, val text: String)
    fun paragraphs(text: String): List<Paragraph> = Regex("(?s)\\S.*?(?=\\n\\s*\\n|$)").findAll(text)
        .map { Paragraph(it.range.first, it.range.last + 1, it.value) }.toList()

    fun anchorMatches(source: String, start: Int, excerpt: String): Boolean = start >= 0 && start + excerpt.length <= source.length && source.regionMatches(start, excerpt, 0, excerpt.length)

    data class Cue(val start: Double, val end: Double, val text: String)
    fun cues(words: JSONArray, corrected: String? = null, maximum: Int = 12): List<Cue> {
        val original = (0 until words.length()).mapNotNull { words.optJSONObject(it) }.filter {
            it.optDouble("start", -1.0).isFinite() && it.optDouble("end", -1.0).isFinite() && it.optDouble("start", -1.0) >= 0 && it.optDouble("end", -1.0) >= it.optDouble("start")
        }
        if (original.isEmpty()) return emptyList()
        val replacement = corrected?.trim()?.split(Regex("\\s+"))?.filter(String::isNotBlank)
        val result = mutableListOf<Cue>()
        original.chunked(maximum.coerceIn(1, 30)).forEachIndexed { index, chunk ->
            val first = index * maximum.coerceIn(1, 30)
            val startWord = replacement?.let { floor(first.toDouble() * it.size / original.size).toInt() }
            val endWord = replacement?.let { floor((first + chunk.size).toDouble() * it.size / original.size).toInt() }
            val text = if (replacement == null) chunk.joinToString(" ") { it.optString("word") }
                else replacement.subList(startWord!!.coerceIn(0, replacement.size), endWord!!.coerceIn(0, replacement.size)).joinToString(" ")
            if (text.isNotBlank()) result += Cue(chunk.first().getDouble("start"), chunk.last().getDouble("end"), text)
        }
        return result
    }

    fun subtitles(cues: List<Cue>, webVtt: Boolean): String {
        fun time(value: Double): String {
            val ms = (value.coerceAtLeast(0.0) * 1000).roundToLong()
            return String.format(Locale.ROOT, "%02d:%02d:%02d%s%03d", ms / 3600000, ms / 60000 % 60, ms / 1000 % 60, if (webVtt) "." else ",", ms % 1000)
        }
        return (if (webVtt) "WEBVTT\n\n" else "") + cues.mapIndexed { i, cue ->
            "${i + 1}\n${time(cue.start)} --> ${time(maxOf(cue.start + .001, cue.end))}\n${cue.text.replace("-->", "→")}\n"
        }.joinToString("\n")
    }

    fun snap(value: Float, grid: Float): Float = if (grid > 0 && value.isFinite()) (value / grid).roundToInt() * grid else value
    fun angle(a: StudioPoint, pivot: StudioPoint, b: StudioPoint): Double {
        val ax = a.x - pivot.x; val ay = a.y - pivot.y; val bx = b.x - pivot.x; val by = b.y - pivot.y
        val divisor = hypot(ax, ay) * hypot(bx, by)
        return if (divisor <= .00001f) 0.0 else Math.toDegrees(acos(((ax * bx + ay * by) / divisor).coerceIn(-1f, 1f).toDouble()))
    }
}

internal data class PlusSearchFilter(val types: Set<String> = emptySet(), val after: Long = 0, val before: Long = Long.MAX_VALUE) {
    fun accepts(type: String, date: Long) = (types.isEmpty() || type in types) && date in after..before
}
