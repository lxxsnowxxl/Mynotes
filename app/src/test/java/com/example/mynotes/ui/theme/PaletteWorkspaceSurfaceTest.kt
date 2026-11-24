package com.example.mynotes.ui.theme

import com.example.mynotes.ui.components.settingsBackgroundColors
import com.example.mynotes.ui.workspace.workspaceToolColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The opaque panels must remain readable over every configured background style. */
class PaletteWorkspaceSurfaceTest {
    private fun readable(label: String, ratio: Float, minimum: Float = 4.5f) {
        assertTrue("$label: contrast=$ratio, minimum=$minimum", ratio >= minimum - 0.01f)
    }

    @Test fun everyPaletteToneHasReadableEditorPanelsAndGradientAnchors() {
        for (palette in PaletteCatalog.palettes) {
            for (tone in palette.tones.indices) {
                for (textMode in listOf("auto", "black", "white")) {
                    for (intensity in listOf(0f, 72f, 100f)) {
                        val app = resolveAppColorScheme(false, palette, tone, intensity, textMode, "palette")
                        val tools = workspaceToolColorScheme(app, palette, intensity)
                        val id = "${palette.key}/$tone/$textMode/$intensity"
                        assertEquals("$id preserves screen tone", app.background, tools.background)
                        for (panel in listOf(tools.surfaceContainerLow, tools.surfaceContainer,
                            tools.surfaceContainerHigh, tools.surfaceContainerHighest)) {
                            readable("$id panel text", uiContrastRatio(tools.onSurface, panel))
                            readable("$id secondary text", uiContrastRatio(tools.onSurfaceVariant, panel))
                            readable("$id control outline", uiContrastRatio(tools.outline, panel), 3f)
                        }
                        readable("$id primary action", uiContrastRatio(tools.onPrimary, tools.primary))
                        readable("$id selected control", uiContrastRatio(tools.onPrimaryContainer, tools.primaryContainer))
                        val background = settingsBackgroundColors(palette, tone, tools.background, tools.onBackground)
                        for (style in listOf("solid", "gradient", "blur", "animated_blur")) {
                            val anchors = if (style == "solid") listOf(background.base)
                                else listOf(background.base, background.first, background.second)
                            for (anchor in anchors) readable("$id $style foreground", uiContrastRatio(tools.onBackground, anchor))
                        }
                    }
                }
            }
        }
    }
}
