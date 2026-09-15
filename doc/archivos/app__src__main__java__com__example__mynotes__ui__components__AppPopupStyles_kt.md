# AppPopupStyles.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 82 → 38 (53.7% menos)

## Responsabilidad

Define estilos y envoltorios comunes para menús desplegables y diálogos de la aplicación.

## Papel dentro de la arquitectura

Centraliza forma, elevación, borde, contraste y propiedades de popup; esto incluye el comportamiento no focusable usado para conservar el modo inmersivo en versiones antiguas de Android.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.ui.theme.ensureUiContrast`.

**Compose:** `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.ColumnScope`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material3.AlertDialog`, `androidx.compose.material3.DropdownMenu`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.window.DialogProperties`, `androidx.compose.ui.window.PopupProperties`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 18 | composable | `AppDropdownMenu` | `fun AppDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier,` | Envuelve DropdownMenu con forma, borde, elevación y propiedades comunes; el popup no toma foco por defecto para preservar modo inmersivo. |
| 29 | composable | `AppAlertDialog` | `fun AppAlertDialog(onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties(` | Aplica el estilo visual común a los AlertDialog de la aplicación. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
