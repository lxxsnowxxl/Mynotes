# NoteCardStyle.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/NoteCardStyle.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 38 → 14 (63.2% menos)

## Responsabilidad

Funciones/modelos auxiliares para calcular la apariencia de una tarjeta de nota a partir de color, contraste, bordes y configuración.

## Papel dentro de la arquitectura

Mantiene las decisiones visuales fuera de NoteCard para que el composable principal no mezcle todo el cálculo de estilo con el árbol de UI.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.settings.AppSettings`.

**Compose:** `androidx.compose.runtime.Immutable`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 7 | data class | `NoteCardStyle` | `data class NoteCardStyle(val cornerRadius: Float, val elevation: Float, val padding: Float, val imageHeight: Float, val titleMaxLines: Int,` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
