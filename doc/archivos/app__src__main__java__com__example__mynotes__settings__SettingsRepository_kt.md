# SettingsRepository.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt`  **SHA-256:** `a1b5c28d763ae7840ec5fa917ca901177858f89d32810b1e784eaeb615446de3`  **Líneas:** 640 · **Bytes:** 32680 · **Imports:** 10 · **Declaraciones detectadas:** 16
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Persistencia DataStore de AppSettings y setters de configuración.
## 2. Package e imports

Package declarado: `com.example.mynotes.settings`.

### Android / Jetpack / Compose

`android.content.Context`, `androidx.datastore.preferences.core.booleanPreferencesKey`, `androidx.datastore.preferences.core.edit`, `androidx.datastore.preferences.core.floatPreferencesKey`, `androidx.datastore.preferences.core.intPreferencesKey`, `androidx.datastore.preferences.core.stringPreferencesKey`, `androidx.datastore.preferences.preferencesDataStore`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.flow.Flow`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.map`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 16 | `class` | `SettingsRepository` | `class SettingsRepository(private val context: Context) {` |
| 521 | `fun` | `normalizeOptionMenuOrder` | `private fun normalizeOptionMenuOrder(value: String): String {` |
| 531 | `fun` | `normalizeHiddenItems` | `private fun normalizeHiddenItems(value: String, validKeys: List<String>): String {` |
| 538 | `fun` | `normalizeOptionMenuTextColor` | `private fun normalizeOptionMenuTextColor(value: String): String = when (value) {` |
| 542 | `fun` | `normalizeConfigurationMode` | `private fun normalizeConfigurationMode(value: String): String = when (value) {` |
| 546 | `fun` | `normalizePerformanceMode` | `private fun normalizePerformanceMode(value: String): String = when (value) {` |
| 550 | `fun` | `normalizeAnimationStyle` | `private fun normalizeAnimationStyle(value: String): String {` |
| 558 | `fun` | `normalizeAnimationEasing` | `private fun normalizeAnimationEasing(value: String): String {` |
| 586 | `fun` | `normalizePaletteKey` | `private fun normalizePaletteKey(value: String): String {` |
| 598 | `fun` | `isLegacyPalette` | `private fun isLegacyPalette(value: String): Boolean = value in legacyPaletteKeys` |
| 599 | `fun` | `normalizeUiTextColor` | `private fun normalizeUiTextColor(value: String): String {` |
| 605 | `fun` | `normalizeIconStyle` | `private fun normalizeIconStyle(value: String): String {` |
| 611 | `fun` | `normalizeAccentColor` | `private fun normalizeAccentColor(value: String): String {` |
| 618 | `fun` | `normalizeSoundEffectsTheme` | `private fun normalizeSoundEffectsTheme(value: String): String {` |
| 626 | `fun` | `normalizeHapticEffectsStyle` | `private fun normalizeHapticEffectsStyle(value: String): String {` |
| 634 | `fun` | `normalizeSliderStyle` | `private fun normalizeSliderStyle(value: String): String {` |

## 4. Estado, efectos y límites observables

- **Flow/StateFlow:** 2 aparición/apariciones.
- **coerce*:** 70 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
