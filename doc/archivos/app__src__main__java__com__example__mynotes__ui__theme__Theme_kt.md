# Theme.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/Theme.kt`  
**Paquete:** `com.example.mynotes.ui.theme`  
**Líneas:** 875 → 267 (69.5% menos)

## Responsabilidad

Construye el MaterialTheme global de MyNotes a partir de AppSettings: paleta, tono, modo claro/oscuro, contraste y tipografía.

## Papel dentro de la arquitectura

Es el punto donde las preferencias persistidas se convierten en ColorScheme/estilo efectivo que heredan las pantallas Compose.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.foundation.isSystemInDarkTheme`, `androidx.compose.material3.ColorScheme`, `androidx.compose.material3.LocalTextStyle`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Typography as MaterialTypography`, `androidx.compose.material3.darkColorScheme`, `androidx.compose.material3.lightColorScheme`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.CompositionLocalProvider`, `androidx.compose.runtime.remember`, `androidx.compose.ui.geometry.Offset`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.Shadow`, `androidx.compose.ui.text.TextStyle`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 20 | fun | `mixColor` | `private fun mixColor(first: Color, second: Color, amount: Float): Color {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 28 | fun | `intensityAmount` | `private fun intensityAmount(intensity: Float): Float {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 32 | fun | `applySectionIntensity` | `fun applySectionIntensity(color: Color, intensity: Float): Color {` | Aplica una transformación o configuración sobre el objeto/estado recibido. |
| 36 | fun | `readableContentColor` | `private fun readableContentColor(background: Color): Color = automaticUiTextColor(background)` | Lee datos desde la fuente indicada y los transforma al formato usado internamente. |
| 38 | fun | `resolvedTextColor` | `private fun resolvedTextColor(textColor: String, background: Color): Color = resolveUiTextColor(value = textColor, background = background)` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 45 | fun | `resolvedPaletteTextColor` | `private fun resolvedPaletteTextColor(textColor: String, toneIndex: Int, background: Color): Color = when (textColor) {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 50 | fun | `resolvedPaletteSecondaryTextColor` | `private fun resolvedPaletteSecondaryTextColor(textColor: String, toneIndex: Int, background: Color): Color {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 59 | fun | `resolveAccentColor` | `private fun resolveAccentColor(value: String, palette: MyNotesPalette): Color {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 94 | fun | `typographyWithBlackOutline` | `private fun typographyWithBlackOutline(base: MaterialTypography, enabled: Boolean): MaterialTypography {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 108 | fun | `lightScheme` | `private fun lightScheme(palette:` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 153 | fun | `darkScheme` | `private fun darkScheme(palette:` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 207 | composable | `MyNotesTheme` | `fun MyNotesTheme(darkTheme: Boolean = isSystemInDarkTheme(),` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
