# NoteCardStyle.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/NoteCardStyle.kt`  **SHA-256:** `f43d417b631714babd5201a0ce3b50a9a6744bab642d4adeaf1bf5b3ea4be44b`  **Líneas:** 16 · **Bytes:** 1168 · **Imports:** 2 · **Declaraciones detectadas:** 2
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Modelo derivado de AppSettings con parámetros visuales de NoteCard.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`androidx.compose.runtime.Immutable`

### Proyecto MyNotes

`com.example.mynotes.settings.AppSettings`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 5 | `class` | `NoteCardStyle` | `` |
| 11 | `fun` | `AppSettings` | `fun AppSettings.toNoteCardStyle() = NoteCardStyle(cornerRadius = noteCardCornerRadius, elevation = noteCardElevation,` |

## 4. Estado, efectos y límites observables

- **coerce*:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.settings.AppSettings`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
