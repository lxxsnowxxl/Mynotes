# AppPopupStyles.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt`  **SHA-256:** `6630e22ea68d8169cac1d47b77fbea4c4b89c78cda43a79780d3e3d946b1e0b0`  **Líneas:** 143 · **Bytes:** 5654 · **Imports:** 19 · **Declaraciones detectadas:** 2
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Estilos compartidos para popups/menús Compose.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.ScrollState`, `androidx.compose.foundation.layout.ColumnScope`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material3.AlertDialog`, `androidx.compose.material3.DropdownMenu`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.geometry.CornerRadius`, `androidx.compose.ui.geometry.Offset`, `androidx.compose.ui.geometry.Size`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.draw.drawWithContent`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.window.DialogProperties`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.ui.theme.ensureUiContrast`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 35 | `fun` | `AppDropdownMenu` | `@Composable` |
| 111 | `fun` | `AppAlertDialog` | `` |

## 4. Estado, efectos y límites observables

- **coerce*:** 6 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.ui.theme.ensureUiContrast`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
