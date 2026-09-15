# SettingsScreen.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt`  
**Paquete:** `com.example.mynotes.ui`  
**Líneas:** 3081 → 1051 (65.9% menos)

## Responsabilidad

Pantalla Compose completa de Configuración. Ensambla las secciones de apariencia, paletas, fuentes, sonido, vibración, idioma, tarjetas, rendimiento, animaciones, menús y respaldo.

## Papel dentro de la arquitectura

Recibe AppSettings y callbacks de cambio. La persistencia permanece fuera de la pantalla; su responsabilidad es representar y organizar controles.

## Flujo funcional principal

Flujo típico: recibe un `AppSettings` actual -> cada control refleja su valor -> al cambiar emite el callback correspondiente -> SettingsViewModel/Repository persisten -> el nuevo StateFlow vuelve a la pantalla y actualiza el tema o control afectado.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.BackupRestoreSection`, `com.example.mynotes.ui.components.ExtremeCustomizationSection`, `com.example.mynotes.ui.components.OptionsMenuCustomizationSection`, `com.example.mynotes.ui.components.PaletteSelector`, `com.example.mynotes.ui.components.StyledSettingsSlider`, `com.example.mynotes.ui.components.SettingsSectionPanel`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.sound.UiHaptic`, `com.example.mynotes.ui.sound.UiHapticPlayer`, `com.example.mynotes.ui.theme.PaletteCatalog`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`.

**Compose:** `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`….

**Kotlin/Java/corrutinas:** `kotlin.math.roundToInt`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 81 | data class | `SliderStyleOption` | `private data class SliderStyleOption(val key: String, val labelRes: Int)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 91 | composable | `SettingsScreen` | `fun SettingsScreen(settings: AppSettings, onDarkModeChange: (Boolean) -> Unit, onBackgroundColorChange: (String) -> Unit,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 829 | composable | `SettingTitle` | `private fun SettingTitle(text: String, color: Color, fontFamily:` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 839 | composable | `SettingTitleRow` | `private fun SettingTitleRow(title: String, value: String, color: Color, fontFamily:` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 855 | composable | `TextColorSelector` | `private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 898 | composable | `TextColorButton` | `private fun TextColorButton(modifier: Modifier, label: String, sampleColor: Color, selected: Boolean, fontFamily:` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 948 | composable | `SettingDropdown` | `private fun SettingDropdown(title: String, selectedLabel: String, options: List<Pair<String, String>>, textColor: Color,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
