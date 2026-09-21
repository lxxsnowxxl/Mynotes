# PaletteCatalog.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt`  **SHA-256:** `3eea86771742b0f1be840d1c839d3d93293b586a182b60a42fe2479530610acc`  **Líneas:** 71 · **Bytes:** 6397 · **Imports:** 4 · **Declaraciones detectadas:** 4
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Catálogo central de paletas y tonos disponibles.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.theme`.

### Android / Jetpack / Compose

`androidx.compose.runtime.Immutable`, `androidx.annotation.StringRes`, `androidx.compose.ui.graphics.Color`

### Proyecto MyNotes

`com.example.mynotes.R`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 7 | `class` | `MyNotesPalette` | `` |
| 11 | `object` | `PaletteCatalog` | `object PaletteCatalog {` |
| 12 | `fun` | `p` | `private fun p(key: String, labelRes: Int, c1: Long, c2: Long, c3: Long, c4: Long, accent: Long) = MyNotesPalette(key = key,` |
| 69 | `fun` | `find` | `` |

## 4. Estado, efectos y límites observables

- No aparecen marcadores relevantes de estado/efectos de la lista auditada.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`

## 6. Recursos Android referenciados

- **R.string:** `mock_palette_blue`, `mock_palette_cherry`, `mock_palette_classic`, `mock_palette_coffee`, `mock_palette_coral`, `mock_palette_cream`, `mock_palette_cyan`, `mock_palette_electric_blue`, `mock_palette_electric_cyan`, `mock_palette_emerald`, `mock_palette_forest`, `mock_palette_graphite`, `mock_palette_green`, `mock_palette_ice`, `mock_palette_indigo`, `mock_palette_lavender`, `mock_palette_marple`, `mock_palette_midnight`, `mock_palette_mint`, `mock_palette_monochrome`, `mock_palette_mustard`, `mock_palette_navy`, `mock_palette_neon_green`, `mock_palette_neon_lime`, `mock_palette_neon_pink`, `mock_palette_neon_violet`, `mock_palette_neon_yellow`, `mock_palette_neutral`, `mock_palette_ocean`, `mock_palette_olive`, `mock_palette_orange`, `mock_palette_peach`, `mock_palette_plum`, `mock_palette_rose`, `mock_palette_sage`, `mock_palette_sand`, `mock_palette_sky`, `mock_palette_soft_pink`, `mock_palette_sun`, `mock_palette_sunset`, `mock_palette_terracotta`, `mock_palette_turquoise`, `mock_palette_violet_dusk`, `mock_palette_vivid_orange`, `mock_palette_warm`, `mock_palette_wine`

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
