# SettingsViewModel.kt

**Ruta:** `app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt`  
**Paquete:** `com.example.mynotes.viewmodel`  
**Líneas:** 541 → 280 (48.2% menos)

## Responsabilidad

ViewModel de configuración que expone AppSettings como StateFlow y ofrece métodos de escritura de alto nivel para cada opción visible en Configuración.

## Papel dentro de la arquitectura

Es la capa de adaptación entre Compose y SettingsRepository: los composables invocan setters simples y el ViewModel ejecuta las escrituras dentro de viewModelScope.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.settings.SettingsRepository`.

**Android/Jetpack:** `android.app.Application`, `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.StateFlow`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 13 | class | `SettingsViewModel` | `class SettingsViewModel(application: Application) : AndroidViewModel(application) {` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 20 | fun | `setDarkMode` | `fun setDarkMode(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 25 | fun | `setBackgroundColor` | `fun setBackgroundColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 30 | fun | `setBackgroundToneIndex` | `fun setBackgroundToneIndex(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 35 | fun | `setBackgroundIntensity` | `fun setBackgroundIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 40 | fun | `setSettingsPanelTone` | `fun setSettingsPanelTone(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 45 | fun | `setSurfacePanelIntensity` | `fun setSurfacePanelIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 50 | fun | `setHeaderIntensity` | `fun setHeaderIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 55 | fun | `setTextColor` | `fun setTextColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 60 | fun | `setTextOutlineEnabled` | `fun setTextOutlineEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 65 | fun | `setNoteUiTextColor` | `fun setNoteUiTextColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 70 | fun | `setSliderStyle` | `fun setSliderStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 75 | fun | `setFont` | `fun setFont(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 80 | fun | `setFontSize` | `fun setFontSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 85 | fun | `setSoundEffectsEnabled` | `fun setSoundEffectsEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 90 | fun | `setSoundEffectsVolume` | `fun setSoundEffectsVolume(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 95 | fun | `setSoundEffectsTheme` | `fun setSoundEffectsTheme(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 100 | fun | `setHapticEffectsEnabled` | `fun setHapticEffectsEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 105 | fun | `setHapticEffectsIntensity` | `fun setHapticEffectsIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 110 | fun | `setHapticEffectsStyle` | `fun setHapticEffectsStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 115 | fun | `setLanguage` | `fun setLanguage(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 120 | fun | `setGridColumns` | `fun setGridColumns(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 125 | fun | `setSortOrder` | `fun setSortOrder(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 130 | fun | `setProfileImageUri` | `fun setProfileImageUri(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 135 | fun | `setProfileImageSize` | `fun setProfileImageSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 140 | fun | `setIconStyle` | `fun setIconStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 145 | fun | `setIconSize` | `fun setIconSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 150 | fun | `setAccentColor` | `fun setAccentColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 155 | fun | `setNoteCardCornerRadius` | `fun setNoteCardCornerRadius(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 160 | fun | `setNoteCardElevation` | `fun setNoteCardElevation(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 165 | fun | `setNoteCardPadding` | `fun setNoteCardPadding(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 170 | fun | `setNoteCardImageHeight` | `fun setNoteCardImageHeight(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 175 | fun | `setNoteTitleMaxLines` | `fun setNoteTitleMaxLines(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 180 | fun | `setNoteContentMaxLines` | `fun setNoteContentMaxLines(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 185 | fun | `setNoteLineSpacing` | `fun setNoteLineSpacing(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 190 | fun | `setShowNoteDate` | `fun setShowNoteDate(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 195 | fun | `setShowCategoryChip` | `fun setShowCategoryChip(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 200 | fun | `setShowFavoriteIcon` | `fun setShowFavoriteIcon(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 205 | fun | `setFabSize` | `fun setFabSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 210 | fun | `setOptionMenuOrder` | `fun setOptionMenuOrder(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 215 | fun | `setOptionMenuHiddenItems` | `fun setOptionMenuHiddenItems(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 220 | fun | `setOptionMenuShowIcons` | `fun setOptionMenuShowIcons(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 225 | fun | `setOptionMenuTextColor` | `fun setOptionMenuTextColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 230 | fun | `setOptionMenuOpacity` | `fun setOptionMenuOpacity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 235 | fun | `setPriorityMenuHiddenItems` | `fun setPriorityMenuHiddenItems(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 240 | fun | `setColorMenuHiddenItems` | `fun setColorMenuHiddenItems(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 245 | fun | `resetOptionMenuSettings` | `fun resetOptionMenuSettings() {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 250 | fun | `setPerformanceMode` | `fun setPerformanceMode(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 255 | fun | `setAnimationsEnabled` | `fun setAnimationsEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 260 | fun | `setAnimationSpeed` | `fun setAnimationSpeed(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 265 | fun | `setAnimationStyle` | `fun setAnimationStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 270 | fun | `setAnimationEasing` | `fun setAnimationEasing(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 275 | fun | `setAnimationIntensity` | `fun setAnimationIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
