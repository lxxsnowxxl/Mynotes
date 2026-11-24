package com.example.mynotes.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.components.AppBackgroundAppearance
import com.example.mynotes.ui.components.LocalAppBackground
import com.example.mynotes.ui.workspace.PaletteWorkspaceTheme
import com.example.mynotes.ui.workspace.workspaceToolColorScheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class PaletteWorkspaceThemeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun existingPanelsFollowToneAndStyleChanges() {
        val palette = PaletteCatalog.find("indigo")
        val tone = mutableIntStateOf(0)
        val style = mutableStateOf("solid")
        var background = Color.Unspecified
        var panel = Color.Unspecified
        var foreground = Color.Unspecified
        compose.setContent {
            val app = resolveAppColorScheme(false, palette, tone.intValue, 72f, "auto", "palette")
            CompositionLocalProvider(
                LocalAppBackground provides AppBackgroundAppearance(style.value, palette, tone.intValue)
            ) {
                MaterialTheme(colorScheme = app) {
                    PaletteWorkspaceTheme(72f) {
                        background = MaterialTheme.colorScheme.background
                        panel = MaterialTheme.colorScheme.surfaceContainerLow
                        foreground = MaterialTheme.colorScheme.onSurface
                        Box(Modifier.size(48.dp).background(panel))
                    }
                }
            }
        }
        for (backgroundStyle in listOf("solid", "gradient", "blur", "animated_blur")) {
            for (index in palette.tones.indices) {
                compose.runOnIdle {
                    tone.intValue = index
                    style.value = backgroundStyle
                }
                compose.runOnIdle {
                    val expected = workspaceToolColorScheme(
                        resolveAppColorScheme(false, palette, index, 72f, "auto", "palette"), palette, 72f
                    )
                    assertEquals("$backgroundStyle/$index background", expected.background, background)
                    assertEquals("$backgroundStyle/$index panel", expected.surfaceContainerLow, panel)
                    assertEquals("$backgroundStyle/$index text", expected.onSurface, foreground)
                }
            }
        }
    }
}
