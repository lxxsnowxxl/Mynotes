# PendingAttachment.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/PendingAttachment.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 21 → 21 (0.0% menos)

## Responsabilidad

Modelo temporal usado mientras un adjunto todavía está en proceso de agregarse o guardarse en una nota.

## Papel dentro de la arquitectura

Evita tratar un URI recién seleccionado como si ya fuera un Attachment persistente; sirve de puente entre los selectores del sistema y el guardado definitivo.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `android.net.Uri`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 5 | data class | `PendingAttachment` | `data class PendingAttachment(` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
