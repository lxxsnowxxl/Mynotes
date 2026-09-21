# DisplayPerformanceController.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt`  **SHA-256:** `ce8d178a143a85f44c75e50dfc920c55ea073110dbf6e1bcebb58eab30c0f1fe`  **Líneas:** 129 · **Bytes:** 5920 · **Imports:** 5 · **Declaraciones detectadas:** 8
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Aplicación de perfiles de rendimiento y comportamiento de display/frame pacing.
## 2. Package e imports

Package declarado: `com.example.mynotes.performance`.

### Android / Jetpack / Compose

`android.os.Build`, `android.view.Display`, `android.view.Window`

### Kotlin / Coroutines / Java

`java.util.WeakHashMap`, `kotlin.math.abs`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 25 | `object` | `DisplayPerformanceController` | `object DisplayPerformanceController {` |
| 32 | `fun` | `requestForPerformanceMode` | `fun requestForPerformanceMode(window: Window, performanceMode: String) {` |
| 51 | `fun` | `reapplyLastRequest` | `fun reapplyLastRequest(window: Window) {` |
| 59 | `fun` | `release` | `fun release(window: Window) {` |
| 62 | `fun` | `refreshRateFor` | `private fun refreshRateFor(performanceMode: String): Float = when (performanceMode) {` |
| 67 | `fun` | `normalizePerformanceMode` | `private fun normalizePerformanceMode(value: String): String = when (value.trim().lowercase()) {` |
| 72 | `fun` | `requestRefreshRate` | `private fun requestRefreshRate(window: Window, targetRefreshRate: Float) {` |
| 117 | `fun` | `chooseClosestMode` | `private fun chooseClosestMode(modes: List<Display.Mode>, targetRefreshRate: Float): Display.Mode? = modes.minWithOrNull(` |

## 4. Estado, efectos y límites observables

- **safe calls:** 3 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
