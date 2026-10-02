package com.example.mynotes.settings

import com.example.mynotes.R

/** Reglas de menús compartidas por DataStore y las secciones de configuración. */
internal object MenuPreferencePolicy {
    val mainKeys: List<String> = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")
    val mainKeySet: Set<String> = mainKeys.toHashSet()
    data class PriorityOption(val key: String, val value: Int, val labelRes: Int)
    data class ColorOption(val key: String, val labelRes: Int, val argb: Long? = null)

    val priorityOptions = listOf(
        PriorityOption("none", 0, R.string.mock_priority_none), PriorityOption("low", 1, R.string.mock_priority_low),
        PriorityOption("medium", 2, R.string.mock_priority_medium), PriorityOption("high", 3, R.string.mock_priority_high)
    )
    val colorOptions = listOf(
        ColorOption("default", R.string.mock_color_default), ColorOption("yellow", R.string.mock_color_yellow, 0xFFFFF4C7),
        ColorOption("orange", R.string.mock_color_orange, 0xFFFFE7D1), ColorOption("red", R.string.mock_color_red, 0xFFFFE0E0),
        ColorOption("pink", R.string.mock_color_pink, 0xFFFFE5EC), ColorOption("purple", R.string.mock_color_purple, 0xFFF0E7FA),
        ColorOption("blue", R.string.mock_color_blue, 0xFFE5F1FB), ColorOption("cyan", R.string.mock_color_cyan, 0xFFE0F7FA),
        ColorOption("teal", R.string.mock_color_teal, 0xFFDDF4F0), ColorOption("green", R.string.mock_color_green, 0xFFE4F2E8),
        ColorOption("mint", R.string.mock_color_mint, 0xFFDFF7EA), ColorOption("lime", R.string.mock_color_lime, 0xFFF1F8D7),
        ColorOption("brown", R.string.mock_color_brown, 0xFFEDE2D9), ColorOption("gray", R.string.mock_color_gray, 0xFFE9ECEF)
    )
    val priorityKeyList: List<String> = priorityOptions.map(PriorityOption::key)
    val priorityKeys: Set<String> = priorityKeyList.toHashSet()
    val colorKeyList: List<String> = colorOptions.map(ColorOption::key)
    val colorKeys: Set<String> = colorKeyList.toHashSet()
    val defaultOrder: String = mainKeys.joinToString(",")

    fun orderedKeys(raw: String): List<String> {
        if (raw.isEmpty() || raw == defaultOrder) return mainKeys
        val requested = collectKeys(raw, mainKeySet)
        // LinkedHashSet conserva el primer orden válido y añade los ausentes.
        requested.addAll(mainKeys)
        return requested.toList()
    }

    fun normalizeOrder(raw: String): String =
        if (raw.isEmpty() || raw == defaultOrder) defaultOrder else orderedKeys(raw).joinToString(",")

    fun hiddenKeys(raw: String, validKeys: Set<String>): Set<String> =
        if (raw.isEmpty()) emptySet() else collectKeys(raw, validKeys)

    fun normalizeHiddenItems(raw: String, validKeys: Set<String>): String =
        if (raw.isEmpty()) "" else hiddenKeys(raw, validKeys).joinToString(",")

    private fun collectKeys(raw: String, validKeys: Set<String>): LinkedHashSet<String> {
        val result = LinkedHashSet<String>()
        var segmentStart = 0
        val length = raw.length

        while (segmentStart <= length) {
            var segmentEnd = raw.indexOf(',', segmentStart)
            if (segmentEnd < 0) segmentEnd = length

            var start = segmentStart
            while (start < segmentEnd && raw[start].isWhitespace()) start++
            var end = segmentEnd
            while (end > start && raw[end - 1].isWhitespace()) end--

            if (end > start) {
                val key = raw.substring(start, end)
                if (key in validKeys) result.add(key)
            }

            if (segmentEnd == length) break
            segmentStart = segmentEnd + 1
        }
        return result
    }
}
