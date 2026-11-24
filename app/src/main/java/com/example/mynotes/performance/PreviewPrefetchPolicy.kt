package com.example.mynotes.performance

/** Presupuesto de precarga independiente del tamaño total de la biblioteca. */
internal object PreviewPrefetchPolicy {
    const val MAX_NOTES = 12
    const val NEIGHBOR_DISTANCE = 4
    const val MAX_ATTACHMENTS = 24

    fun noteIndices(visibleIndices: List<Int>, itemCount: Int): List<Int> {
        if (itemCount <= 0) return emptyList()
        val visible = visibleIndices.asSequence().filter { it in 0 until itemCount }
            .distinct().take(MAX_NOTES).toList()
        if (visible.isEmpty()) return emptyList()
        val selected = LinkedHashSet<Int>(MAX_NOTES)
        selected.addAll(visible)
        val first = visible.minOrNull()!!
        val last = visible.maxOrNull()!!
        for (distance in 1..NEIGHBOR_DISTANCE) {
            if (selected.size >= MAX_NOTES) break
            val next = last.toLong() + distance
            if (next < itemCount) selected.add(next.toInt())
            if (selected.size >= MAX_NOTES) break
            val previous = first - distance
            if (previous >= 0) selected.add(previous)
        }
        return selected.toList()
    }
}
