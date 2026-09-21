# AppFonts.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt`  **SHA-256:** `3b9a9b91cd033607fd9ba69a16e099cdc244233374c1471e1d9bed151359c724`  **Líneas:** 51 · **Bytes:** 2518 · **Imports:** 5 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Resolución de familias tipográficas de la app.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.theme`.

### Android / Jetpack / Compose

`androidx.compose.ui.text.font.Font`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontStyle`, `androidx.compose.ui.text.font.FontWeight`

### Proyecto MyNotes

`com.example.mynotes.R`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 36 | `fun` | `appFontFamily` | `` |

## 4. Estado, efectos y límites observables

- No aparecen marcadores relevantes de estado/efectos de la lista auditada.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`

## 6. Recursos Android referenciados

- **R.font:** `google_sans_bold` ×2, `google_sans_bold_italic` ×2, `google_sans_flex`, `google_sans_italic` ×2, `google_sans_medium` ×2, `google_sans_medium_italic` ×2, `google_sans_regular` ×2

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
