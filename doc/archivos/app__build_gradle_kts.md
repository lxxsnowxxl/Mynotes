# build.gradle.kts

**Ruta:** `app/build.gradle.kts`  
**Líneas:** 176 → 79 (55.1% menos)

## Responsabilidad

Configuración Gradle del módulo Android: SDK, build types, compatibilidad Java, Compose y dependencias de Room, Coil, Media3, Lifecycle y pruebas.

## Papel dentro de la arquitectura

No contiene lógica de ejecución de MyNotes, pero determina qué APIs y bibliotecas están disponibles durante compilación y empaquetado.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

## Declaraciones importantes detectadas

Este archivo no declara clases o funciones ejecutables; actúa como configuración, marcador o contenedor de constantes/comentarios.

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
