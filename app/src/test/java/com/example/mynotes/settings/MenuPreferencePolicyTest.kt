package com.example.mynotes.settings

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class MenuPreferencePolicyTest {
    @Test
    fun partialOrderPreservesFirstOccurrenceAndAddsMissingActions() {
        assertEquals(
            "pin,edit,delete,favorite,priority,color,move",
            MenuPreferencePolicy.normalizeOrder(" pin,edit,pin,unknown,delete, ")
        )
    }

    @Test
    fun emptyAndInvalidOrdersKeepEveryDefaultAction() {
        val defaultOrder = "edit,favorite,pin,priority,color,move,delete"
        for (raw in listOf("", " , ", "EDIT,unknown", defaultOrder)) {
            assertEquals(defaultOrder, MenuPreferencePolicy.normalizeOrder(raw))
        }
    }

    @Test
    fun hiddenItemsKeepTheirOrderAndTheirOwnMenuDomain() {
        assertEquals(
            "high,none,low",
            MenuPreferencePolicy.normalizeHiddenItems(" high,none,high,low,edit, ", MenuPreferencePolicy.priorityKeys)
        )
        assertEquals(
            "red,blue",
            MenuPreferencePolicy.normalizeHiddenItems("red,blue,red,high,RED", MenuPreferencePolicy.colorKeys)
        )
        assertEquals(emptySet<String>(), MenuPreferencePolicy.hiddenKeys("", MenuPreferencePolicy.mainKeySet))
    }

    @Test
    fun randomizedSavedValuesMatchThePreviousNormalizationRules() {
        // Referencia anterior: verifica compatibilidad de preferencias guardadas.
        val main = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")
        val validMenus = listOf(main, listOf("none", "low", "medium", "high"),
            listOf("default", "yellow", "orange", "red", "pink", "purple", "blue", "cyan", "teal", "green", "mint", "lime", "brown", "gray"))
        val inputs = validMenus.flatten() + listOf("", "unknown", "EDIT", " pin ", "\tred\n")
        val random = Random(161)
        repeat(1_000) {
            val raw = List(random.nextInt(25)) { inputs[random.nextInt(inputs.size)] }.joinToString(",")
            val requested = raw.split(",").map { it.trim() }.filter { it in main }.distinct()
            val expectedOrder = requested + main.filterNot { it in requested }
            assertEquals(expectedOrder, MenuPreferencePolicy.orderedKeys(raw))
            assertEquals(expectedOrder.joinToString(","), MenuPreferencePolicy.normalizeOrder(raw))
            for (valid in validMenus) {
                val expectedHidden = raw.split(",").map { it.trim() }.filter { it in valid }.distinct()
                assertEquals(expectedHidden, MenuPreferencePolicy.hiddenKeys(raw, valid.toSet()).toList())
                assertEquals(expectedHidden.joinToString(","), MenuPreferencePolicy.normalizeHiddenItems(raw, valid.toSet()))
            }
        }
    }
}
