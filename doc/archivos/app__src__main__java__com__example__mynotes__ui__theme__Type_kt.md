# Type.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/Type.kt`  
**Paquete:** `com.example.mynotes.ui.theme`  
**Líneas:** 34 → 28 (17.6% menos)

## Responsabilidad

Define la configuración tipográfica base del tema Material de la aplicación.

## Papel dentro de la arquitectura

Complementa AppFonts: Type establece la estructura tipográfica y las pantallas pueden aplicar la FontFamily elegida cuando corresponde.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.material3.Typography`, `androidx.compose.ui.text.TextStyle`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.sp`.

## Declaraciones importantes detectadas

Este archivo no declara clases o funciones ejecutables; actúa como configuración, marcador o contenedor de constantes/comentarios.

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
