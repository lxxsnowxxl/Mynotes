# PaletteCatalog.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt`  
**Paquete:** `com.example.mynotes.ui.theme`  
**Líneas:** 96 → 66 (31.2% menos)

## Responsabilidad

Catálogo de paletas disponibles, cada una con su clave, nombre y colección de tonos.

## Papel dentro de la arquitectura

Es la fuente común usada por Configuración y por la resolución del tema; mantener las claves aquí sincronizadas con SettingsRepository evita selecciones inválidas.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`.

**Compose:** `androidx.compose.runtime.Immutable`, `androidx.compose.ui.graphics.Color`.

**Android/Jetpack:** `androidx.annotation.StringRes`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 9 | data class | `MyNotesPalette` | `data class MyNotesPalette(val key: String,` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 11 | object | `PaletteCatalog` | `object PaletteCatalog {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 12 | fun | `p` | `private fun p(key: String, labelRes: Int, c1: Long, c2: Long, c3: Long, c4: Long, accent: Long) = MyNotesPalette(key = key,` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 63 | fun | `find` | `fun find(key: String): MyNotesPalette = palettes.firstOrNull {` | Busca y devuelve el elemento que satisface el criterio implementado. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
