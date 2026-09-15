# AppFonts.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt`  
**Paquete:** `com.example.mynotes.ui.theme`  
**Líneas:** 125 → 51 (59.2% menos)

## Responsabilidad

Catálogo y resolución de las familias tipográficas disponibles en Configuración.

## Papel dentro de la arquitectura

Convierte la clave persistida de una fuente en FontFamily para que todas las pantallas puedan obedecer la misma selección.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`.

**Compose:** `androidx.compose.ui.text.font.Font`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontStyle`, `androidx.compose.ui.text.font.FontWeight`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 37 | fun | `appFontFamily` | `fun appFontFamily(key: String): FontFamily {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
