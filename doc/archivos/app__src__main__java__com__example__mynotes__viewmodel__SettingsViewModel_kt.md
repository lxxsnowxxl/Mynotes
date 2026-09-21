# SettingsViewModel.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt`  **SHA-256:** `6890b79306855a674d337a92316af76de3986fc96f44651fe8e54041dfe910a9`  **Líneas:** 303 · **Bytes:** 10972 · **Imports:** 10 · **Declaraciones detectadas:** 55
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Puente entre SettingsRepository y UI; este nombre existe en dos rutas, una es stub de compatibilidad y la clase real vive en viewmodel/.
## 2. Package e imports

Package declarado: `com.example.mynotes.viewmodel`.

### Android / Jetpack / Compose

`android.app.Application`, `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`

### Proyecto MyNotes

`com.example.mynotes.settings.AppSettings`, `com.example.mynotes.settings.SettingsRepository`, `com.example.mynotes.widget.MyNotesWidgetUpdater`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.StateFlow`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 13 | `class` | `SettingsViewModel` | `` |
| 23 | `fun` | `setConfigurationMode` | `fun setConfigurationMode(value: String) {` |
| 28 | `fun` | `setDarkMode` | `fun setDarkMode(value: Boolean) {` |
| 34 | `fun` | `setBackgroundColor` | `fun setBackgroundColor(value: String) {` |
| 40 | `fun` | `setBackgroundToneIndex` | `fun setBackgroundToneIndex(value: Int) {` |
| 46 | `fun` | `setBackgroundIntensity` | `fun setBackgroundIntensity(value: Float) {` |
| 54 | `fun` | `setSettingsPanelTone` | `fun setSettingsPanelTone(value: Float) {` |
| 59 | `fun` | `setSurfacePanelIntensity` | `fun setSurfacePanelIntensity(value: Float) {` |
| 67 | `fun` | `setHeaderIntensity` | `fun setHeaderIntensity(value: Float) {` |
| 72 | `fun` | `setTextColor` | `fun setTextColor(value: String) {` |
| 77 | `fun` | `setTextOutlineEnabled` | `fun setTextOutlineEnabled(value: Boolean) {` |
| 82 | `fun` | `setNoteUiTextColor` | `fun setNoteUiTextColor(value: String) {` |
| 87 | `fun` | `setSliderStyle` | `fun setSliderStyle(value: String) {` |
| 92 | `fun` | `setFont` | `fun setFont(value: String) {` |
| 97 | `fun` | `setFontSize` | `fun setFontSize(value: Float) {` |
| 102 | `fun` | `setSoundEffectsEnabled` | `fun setSoundEffectsEnabled(value: Boolean) {` |
| 107 | `fun` | `setSoundEffectsVolume` | `fun setSoundEffectsVolume(value: Float) {` |
| 112 | `fun` | `setSoundEffectsTheme` | `fun setSoundEffectsTheme(value: String) {` |
| 117 | `fun` | `setHapticEffectsEnabled` | `fun setHapticEffectsEnabled(value: Boolean) {` |
| 122 | `fun` | `setHapticEffectsIntensity` | `fun setHapticEffectsIntensity(value: Float) {` |
| 127 | `fun` | `setHapticEffectsStyle` | `fun setHapticEffectsStyle(value: String) {` |
| 132 | `fun` | `setLanguage` | `fun setLanguage(value: String) {` |
| 138 | `fun` | `setGridColumns` | `fun setGridColumns(value: Int) {` |
| 143 | `fun` | `setSortOrder` | `fun setSortOrder(value: String) {` |
| 148 | `fun` | `setProfileImageUri` | `fun setProfileImageUri(value: String) {` |
| 153 | `fun` | `setProfileImageSize` | `fun setProfileImageSize(value: Float) {` |
| 158 | `fun` | `setIconStyle` | `fun setIconStyle(value: String) {` |
| 163 | `fun` | `setIconSize` | `fun setIconSize(value: Float) {` |
| 168 | `fun` | `setAccentColor` | `fun setAccentColor(value: String) {` |
| 173 | `fun` | `setNoteCardCornerRadius` | `fun setNoteCardCornerRadius(value: Float) {` |
| 178 | `fun` | `setNoteCardElevation` | `fun setNoteCardElevation(value: Float) {` |
| 183 | `fun` | `setNoteCardPadding` | `fun setNoteCardPadding(value: Float) {` |
| 188 | `fun` | `setNoteCardImageHeight` | `fun setNoteCardImageHeight(value: Float) {` |
| 193 | `fun` | `setNoteCardOutlineWidth` | `fun setNoteCardOutlineWidth(value: Float) {` |
| 198 | `fun` | `setNoteTitleMaxLines` | `fun setNoteTitleMaxLines(value: Int) {` |
| 203 | `fun` | `setNoteContentMaxLines` | `fun setNoteContentMaxLines(value: Int) {` |
| 208 | `fun` | `setNoteLineSpacing` | `fun setNoteLineSpacing(value: Float) {` |
| 213 | `fun` | `setShowNoteDate` | `fun setShowNoteDate(value: Boolean) {` |
| 218 | `fun` | `setShowCategoryChip` | `fun setShowCategoryChip(value: Boolean) {` |
| 223 | `fun` | `setShowFavoriteIcon` | `fun setShowFavoriteIcon(value: Boolean) {` |
| 228 | `fun` | `setFabSize` | `fun setFabSize(value: Float) {` |
| 233 | `fun` | `setOptionMenuOrder` | `fun setOptionMenuOrder(value: String) {` |
| 238 | `fun` | `setOptionMenuHiddenItems` | `fun setOptionMenuHiddenItems(value: String) {` |
| 243 | `fun` | `setOptionMenuShowIcons` | `fun setOptionMenuShowIcons(value: Boolean) {` |
| 248 | `fun` | `setOptionMenuTextColor` | `fun setOptionMenuTextColor(value: String) {` |
| 253 | `fun` | `setOptionMenuOpacity` | `fun setOptionMenuOpacity(value: Float) {` |
| 258 | `fun` | `setPriorityMenuHiddenItems` | `fun setPriorityMenuHiddenItems(value: String) {` |
| 263 | `fun` | `setColorMenuHiddenItems` | `fun setColorMenuHiddenItems(value: String) {` |
| 268 | `fun` | `resetOptionMenuSettings` | `fun resetOptionMenuSettings() {` |
| 273 | `fun` | `setPerformanceMode` | `fun setPerformanceMode(value: String) {` |
| 278 | `fun` | `setAnimationsEnabled` | `fun setAnimationsEnabled(value: Boolean) {` |
| 283 | `fun` | `setAnimationSpeed` | `fun setAnimationSpeed(value: Float) {` |
| 288 | `fun` | `setAnimationStyle` | `fun setAnimationStyle(value: String) {` |
| 293 | `fun` | `setAnimationEasing` | `fun setAnimationEasing(value: String) {` |
| 298 | `fun` | `setAnimationIntensity` | `fun setAnimationIntensity(value: Float) {` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 55 aparición/apariciones.
- **Flow/StateFlow:** 4 aparición/apariciones.
- **coerce*:** 18 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.settings.SettingsRepository`
- `com.example.mynotes.widget.MyNotesWidgetUpdater`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
