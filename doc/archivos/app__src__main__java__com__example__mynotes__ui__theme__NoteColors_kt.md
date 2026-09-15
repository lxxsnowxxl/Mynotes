# NoteColors.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt`  
**Paquete:** `com.example.mynotes.ui.theme`  
**Líneas:** 232 → 164 (29.3% menos)

## Responsabilidad

Centraliza los colores específicos de notas y las reglas para resolver fondos/textos asociados.

## Papel dentro de la arquitectura

Permite que editor, detalle y tarjetas interpreten de la misma manera el identificador de color guardado en una nota.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 12 | composable | `noteBackgroundColor` | `fun noteBackgroundColor(color: String): Color {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 43 | fun | `compositeUiColor` | `fun compositeUiColor(foreground: Color, background: Color): Color {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 51 | fun | `uiContrastRatio` | `fun uiContrastRatio(foreground: Color, background: Color): Float {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 64 | fun | `automaticUiTextColor` | `fun automaticUiTextColor(background: Color): Color {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 82 | fun | `resolveUiTextColor` | `fun resolveUiTextColor(value: String, background: Color): Color {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 90 | fun | `mixOpaqueUiColor` | `private fun mixOpaqueUiColor(foreground: Color, background: Color, amount: Float): Color {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 102 | fun | `softenUiColorToContrast` | `fun softenUiColorToContrast(foreground: Color, background: Color, minimumContrast: Float, maximumSoftening: Float = 0.62f): Color {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 127 | fun | `resolveSecondaryUiTextColor` | `fun resolveSecondaryUiTextColor(value: String, background: Color): Color {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 140 | fun | `resolveUiGraphicColor` | `fun resolveUiGraphicColor(value: String, background: Color): Color {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 154 | fun | `ensureUiContrast` | `fun ensureUiContrast(preferred: Color, background: Color, minimumContrast: Float = 3f): Color {` | Comprueba una condición y produce un resultado que cumple los requisitos esperados. |
| 164 | fun | `manualUiTextColor` | `fun manualUiTextColor(value: String): Color = resolveUiTextColor(value = value, background = Color.White)` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
