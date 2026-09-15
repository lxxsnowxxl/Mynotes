# Attachment.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/Attachment.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 41 → 24 (41.5% menos)

## Responsabilidad

Modelo persistente que representa un archivo adjunto asociado a una nota.

## Papel dentro de la arquitectura

Funciona como contrato de datos entre Room, los DAO, el ViewModel de notas, las vistas previas y el visor de adjuntos.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.runtime.Immutable`.

**Android/Jetpack:** `androidx.room.Entity`, `androidx.room.Index`, `androidx.room.PrimaryKey`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 10 | data class | `Attachment` | `data class Attachment(` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
