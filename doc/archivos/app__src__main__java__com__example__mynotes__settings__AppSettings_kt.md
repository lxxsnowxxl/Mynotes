# AppSettings.kt

**Ruta:** `app/src/main/java/com/example/mynotes/settings/AppSettings.kt`  
**Paquete:** `com.example.mynotes.settings`  
**Líneas:** 227 → 148 (34.8% menos)

## Responsabilidad

Modelo inmutable que reúne los ajustes configurables de la aplicación y sus valores predeterminados.

## Papel dentro de la arquitectura

Es el estado de configuración que observa la UI. SettingsRepository lo produce desde DataStore y SettingsViewModel lo expone a Compose.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.runtime.Immutable`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 6 | data class | `AppSettings` | `data class AppSettings(val darkMode: Boolean = false,` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
