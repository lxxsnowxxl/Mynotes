package com.example.mynotes.ui.pdf

internal object PdfPageRange {
    fun parse(input: String, count: Int): List<Int>? {
        if (count <= 0 || input.length > 4000) return null
        if (input.isBlank()) return (0 until count).toList()
        val pages = sortedSetOf<Int>()
        for (part in input.split(',')) {
            val match = Regex("^\\s*(\\d+)\\s*(?:-\\s*(\\d+))?\\s*$").matchEntire(part) ?: return null
            val start = match.groupValues[1].toIntOrNull() ?: return null
            val end = match.groupValues[2].ifBlank { match.groupValues[1] }.toIntOrNull() ?: return null
            if (start !in 1..count || end !in start..count) return null
            for (page in start..end) pages += page - 1
        }
        return pages.toList().takeIf { it.isNotEmpty() }
    }
}
