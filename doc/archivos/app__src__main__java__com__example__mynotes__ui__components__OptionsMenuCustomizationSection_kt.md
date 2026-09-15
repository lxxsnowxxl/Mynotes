# OptionsMenuCustomizationSection.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/OptionsMenuCustomizationSection.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 1227 → 368 (70.0% menos)

## Responsabilidad

Sección de Configuración que permite personalizar el menú contextual de las notas: orden, visibilidad, iconos y apariencia relacionada.

## Papel dentro de la arquitectura

Produce los valores que NoteCard/NoteDetailScreen usan al construir sus menús sin cambiar la lógica funcional de las acciones.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Compose:** `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material.icons.filled.KeyboardArrowDown`, `androidx.compose.material.icons.filled.KeyboardArrowUp`, `androidx.compose.material.icons.filled.Refresh`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`….

**Kotlin/Java/corrutinas:** `kotlin.math.roundToInt`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 52 | data class | `MenuOptionDescriptor` | `private data class MenuOptionDescriptor(val key: String, val labelRes: Int)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 73 | composable | `OptionsMenuCustomizationSection` | `fun OptionsMenuCustomizationSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 247 | composable | `MenuOrderRow` | `private fun MenuOrderRow(label: String, visible: Boolean, canHide: Boolean, canMoveUp: Boolean, canMoveDown: Boolean,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 281 | composable | `CompactToggleGrid` | `private fun CompactToggleGrid(options: List<MenuOptionDescriptor>, hiddenItems: Set<String>, visibleCount: Int,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 312 | composable | `ToggleRow` | `private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 340 | composable | `optionMenuTextColorLabel` | `private fun optionMenuTextColorLabel(value: String): String {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 348 | fun | `normalizedOrder` | `private fun normalizedOrder(raw: String): List<String> {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 361 | fun | `parseKeys` | `private fun parseKeys(raw: String, valid: List<String>): Set<String> {` | Interpreta texto/datos externos y los convierte a una estructura utilizable. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
