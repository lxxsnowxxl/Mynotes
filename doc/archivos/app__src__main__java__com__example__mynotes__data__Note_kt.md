# Note.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/Note.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 83 → 47 (43.4% menos)

## Responsabilidad

Modelo persistente principal de una nota: almacena el contenido y los metadatos usados para ordenar, categorizar, priorizar y representar cada nota.

## Papel dentro de la arquitectura

Es la entidad central del dominio. NoteDao la persiste, NoteViewModel la modifica y las pantallas Compose la representan.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.runtime.Immutable`.

**Android/Jetpack:** `androidx.room.Entity`, `androidx.room.Index`, `androidx.room.PrimaryKey`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 24 | data class | `Note` | `data class Note(` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
