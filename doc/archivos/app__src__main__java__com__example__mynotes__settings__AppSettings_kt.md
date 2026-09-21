# AppSettings.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/settings/AppSettings.kt`  **SHA-256:** `c0f0b3a5c38f821f6aefbb839a7b6ba20e2469293c9feaf04822bac54ca5eaf4`  **Líneas:** 158 · **Bytes:** 5475 · **Imports:** 1 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Modelo inmutable con todas las preferencias configurables de la app.
## 2. Package e imports

Package declarado: `com.example.mynotes.settings`.

### Android / Jetpack / Compose

`androidx.compose.runtime.Immutable`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 4 | `class` | `AppSettings` | `` |

## 4. Estado, efectos y límites observables

- No aparecen marcadores relevantes de estado/efectos de la lista auditada.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
