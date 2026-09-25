package com.example.mynotes.settings

/** Reglas de menús compartidas por DataStore y las secciones de configuración. */
internal object MenuPreferencePolicy {
    val mainKeys: List<String> = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")
    val mainKeySet: Set<String> = mainKeys.toHashSet()
    val priorityKeys: Set<String> = setOf("none", "low", "medium", "high")
    val colorKeys: Set<String> = setOf(
        "default", "yellow", "orange", "red", "pink", "purple", "blue", "cyan", "teal", "green", "mint",
        "lime", "brown", "gray"
    )
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
        for (part in raw.splitToSequence(',')) {
            val key = part.trim()
            if (key in validKeys) result.add(key)
        }
        return result
    }
}
