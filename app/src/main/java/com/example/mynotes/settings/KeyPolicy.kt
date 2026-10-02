package com.example.mynotes.settings

internal fun String.validOr(validKeys: Set<String>, fallback: String): String = if (this in validKeys) this else fallback

internal fun String.normalizedKeyOr(validKeys: Set<String>, fallback: String): String {
    if (this in validKeys) return this
    val normalized = trim().lowercase()
    return if (normalized in validKeys) normalized else fallback
}
