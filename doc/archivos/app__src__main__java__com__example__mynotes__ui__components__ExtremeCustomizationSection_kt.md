# ExtremeCustomizationSection.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 1137 → 487 (57.2% menos)

## Responsabilidad

Sección avanzada de Configuración que agrupa controles de personalización y rendimiento con selectores, sliders y opciones de animación.

## Papel dentro de la arquitectura

Descompone SettingsScreen en un componente especializado y reutiliza los estilos de dropdown, sliders, sonido/háptica y opciones de movimiento.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`.

**Compose:** `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.shape.CircleShape`….

**Android/Jetpack:** `android.content.Intent`, `android.net.Uri`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`.

**Bibliotecas externas:** `coil3.compose.AsyncImage`.

**Kotlin/Java/corrutinas:** `kotlin.math.roundToInt`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 68 | data class | `IconStyleOption` | `private data class IconStyleOption(val key: String, val labelRes: Int)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 74 | data class | `AccentOption` | `private data class AccentOption(val key: String, val color: Color)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 77 | data class | `MotionOption` | `private data class MotionOption(val key: String, val labelRes: Int)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 103 | composable | `ExtremeCustomizationSection` | `fun ExtremeCustomizationSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 412 | composable | `MotionOptionPicker` | `private fun MotionOptionPicker(title: String, selectedKey: String, options: List<MotionOption>, onSelected: (String) -> Unit,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 465 | composable | `CustomSlider` | `private fun CustomSlider(title: String, label: String, value: Float, onValueChange: (Float) -> Unit, onFinished: () -> Unit,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 479 | composable | `ToggleRow` | `private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
