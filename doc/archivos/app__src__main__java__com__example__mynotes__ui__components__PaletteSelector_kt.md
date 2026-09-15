# PaletteSelector.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/PaletteSelector.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 544 → 195 (64.2% menos)

## Responsabilidad

Componente Compose que presenta una paleta y permite seleccionar paleta/tono con indicación visual del color activo.

## Papel dentro de la arquitectura

Consume PaletteCatalog y callbacks de ajustes; mantiene separada la cuadrícula de colores de la pantalla grande de Configuración.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.ui.theme.MyNotesPalette`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Compose:** `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.core.animateDpAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.background`, `androidx.compose.foundation.border`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`….

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 61 | composable | `PaletteSelector` | `fun PaletteSelector(palettes: List<MyNotesPalette>, selectedPaletteKey: String, selectedToneIndex: Int, onPaletteSelected: (String) -> Unit,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 94 | composable | `PaletteCard` | `private fun PaletteCard(modifier: Modifier, palette: MyNotesPalette, selected: Boolean, selectedToneIndex: Int,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 165 | composable | `PaletteToneCircle` | `private fun PaletteToneCircle(modifier: Modifier = Modifier, color: Color, selected: Boolean, onClick: () -> Unit,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
