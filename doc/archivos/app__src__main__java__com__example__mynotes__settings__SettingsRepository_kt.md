# SettingsRepository.kt

**Ruta:** `app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt`  
**Paquete:** `com.example.mynotes.settings`  
**Líneas:** 1718 → 598 (65.2% menos)

## Responsabilidad

Fuente de verdad de la configuración persistente. Lee/escribe DataStore, normaliza valores, aplica límites y mantiene compatibilidad con las claves y opciones aceptadas por la aplicación.

## Papel dentro de la arquitectura

La UI no escribe preferencias directamente: los cambios pasan por SettingsViewModel y terminan en este repositorio. Por eso aquí se concentran las reglas de persistencia y normalización.

## Flujo funcional principal

Flujo típico: DataStore emite Preferences -> el repositorio las transforma en `AppSettings` -> SettingsViewModel publica StateFlow -> Compose recompone. En sentido inverso, un control llama a un setter del ViewModel -> el repositorio normaliza y guarda la clave correspondiente.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `android.content.Context`, `androidx.datastore.preferences.core.booleanPreferencesKey`, `androidx.datastore.preferences.core.edit`, `androidx.datastore.preferences.core.floatPreferencesKey`, `androidx.datastore.preferences.core.intPreferencesKey`, `androidx.datastore.preferences.core.stringPreferencesKey`, `androidx.datastore.preferences.preferencesDataStore`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.flow.Flow`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.map`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 16 | class | `SettingsRepository` | `class SettingsRepository(private val context: Context) {` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 148 | fun | `setDarkMode` | `suspend fun setDarkMode(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 153 | fun | `setBackgroundColor` | `suspend fun setBackgroundColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 158 | fun | `setBackgroundToneIndex` | `suspend fun setBackgroundToneIndex(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 170 | fun | `setBackgroundIntensity` | `suspend fun setBackgroundIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 175 | fun | `setSettingsPanelTone` | `suspend fun setSettingsPanelTone(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 180 | fun | `setSurfacePanelIntensity` | `suspend fun setSurfacePanelIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 185 | fun | `setHeaderIntensity` | `suspend fun setHeaderIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 190 | fun | `setTextColor` | `suspend fun setTextColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 195 | fun | `setTextOutlineEnabled` | `suspend fun setTextOutlineEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 200 | fun | `setNoteUiTextColor` | `suspend fun setNoteUiTextColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 205 | fun | `setSliderStyle` | `suspend fun setSliderStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 210 | fun | `setFont` | `suspend fun setFont(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 215 | fun | `setFontSize` | `suspend fun setFontSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 220 | fun | `setSoundEffectsEnabled` | `suspend fun setSoundEffectsEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 225 | fun | `setSoundEffectsVolume` | `suspend fun setSoundEffectsVolume(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 230 | fun | `setSoundEffectsTheme` | `suspend fun setSoundEffectsTheme(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 235 | fun | `setHapticEffectsEnabled` | `suspend fun setHapticEffectsEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 240 | fun | `setHapticEffectsIntensity` | `suspend fun setHapticEffectsIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 245 | fun | `setHapticEffectsStyle` | `suspend fun setHapticEffectsStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 250 | fun | `setLanguage` | `suspend fun setLanguage(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 255 | fun | `setGridColumns` | `suspend fun setGridColumns(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 260 | fun | `setSortOrder` | `suspend fun setSortOrder(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 265 | fun | `setProfileImageUri` | `suspend fun setProfileImageUri(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 270 | fun | `setProfileImageSize` | `suspend fun setProfileImageSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 275 | fun | `setIconStyle` | `suspend fun setIconStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 280 | fun | `setIconSize` | `suspend fun setIconSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 285 | fun | `setAccentColor` | `suspend fun setAccentColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 290 | fun | `setNoteCardCornerRadius` | `suspend fun setNoteCardCornerRadius(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 295 | fun | `setNoteCardElevation` | `suspend fun setNoteCardElevation(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 300 | fun | `setNoteCardPadding` | `suspend fun setNoteCardPadding(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 305 | fun | `setNoteCardImageHeight` | `suspend fun setNoteCardImageHeight(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 310 | fun | `setNoteTitleMaxLines` | `suspend fun setNoteTitleMaxLines(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 315 | fun | `setNoteContentMaxLines` | `suspend fun setNoteContentMaxLines(value: Int) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 320 | fun | `setNoteLineSpacing` | `suspend fun setNoteLineSpacing(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 325 | fun | `setShowNoteDate` | `suspend fun setShowNoteDate(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 330 | fun | `setShowCategoryChip` | `suspend fun setShowCategoryChip(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 335 | fun | `setShowFavoriteIcon` | `suspend fun setShowFavoriteIcon(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 340 | fun | `setFabSize` | `suspend fun setFabSize(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 345 | fun | `setOptionMenuOrder` | `suspend fun setOptionMenuOrder(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 350 | fun | `setOptionMenuHiddenItems` | `suspend fun setOptionMenuHiddenItems(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 355 | fun | `setOptionMenuShowIcons` | `suspend fun setOptionMenuShowIcons(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 360 | fun | `setOptionMenuTextColor` | `suspend fun setOptionMenuTextColor(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 365 | fun | `setOptionMenuOpacity` | `suspend fun setOptionMenuOpacity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 370 | fun | `setPriorityMenuHiddenItems` | `suspend fun setPriorityMenuHiddenItems(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 375 | fun | `setColorMenuHiddenItems` | `suspend fun setColorMenuHiddenItems(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 380 | fun | `resetOptionMenuSettings` | `suspend fun resetOptionMenuSettings() {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 391 | fun | `setPerformanceMode` | `suspend fun setPerformanceMode(value: String) {` | Persiste el perfil de rendimiento después de normalizarlo a una de las opciones soportadas. |
| 396 | fun | `setAnimationsEnabled` | `suspend fun setAnimationsEnabled(value: Boolean) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 401 | fun | `setAnimationSpeed` | `suspend fun setAnimationSpeed(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 406 | fun | `setAnimationStyle` | `suspend fun setAnimationStyle(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 411 | fun | `setAnimationEasing` | `suspend fun setAnimationEasing(value: String) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 416 | fun | `setAnimationIntensity` | `suspend fun setAnimationIntensity(value: Float) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 426 | fun | `restoreFromBackup` | `suspend fun restoreFromBackup(value: AppSettings) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 485 | fun | `normalizeOptionMenuOrder` | `private fun normalizeOptionMenuOrder(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 495 | fun | `normalizeHiddenItems` | `private fun normalizeHiddenItems(value: String, validKeys: List<String>): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 502 | fun | `normalizeOptionMenuTextColor` | `private fun normalizeOptionMenuTextColor(value: String): String = when (value) {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 506 | fun | `normalizePerformanceMode` | `private fun normalizePerformanceMode(value: String): String = when (value) {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 510 | fun | `normalizeAnimationStyle` | `private fun normalizeAnimationStyle(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 518 | fun | `normalizeAnimationEasing` | `private fun normalizeAnimationEasing(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 546 | fun | `normalizePaletteKey` | `private fun normalizePaletteKey(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 558 | fun | `isLegacyPalette` | `private fun isLegacyPalette(value: String): Boolean = value in legacyPaletteKeys` | Evalúa una condición y devuelve un resultado booleano. |
| 559 | fun | `normalizeUiTextColor` | `private fun normalizeUiTextColor(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 565 | fun | `normalizeIconStyle` | `private fun normalizeIconStyle(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 571 | fun | `normalizeAccentColor` | `private fun normalizeAccentColor(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 578 | fun | `normalizeSoundEffectsTheme` | `private fun normalizeSoundEffectsTheme(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 585 | fun | `normalizeHapticEffectsStyle` | `private fun normalizeHapticEffectsStyle(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 592 | fun | `normalizeSliderStyle` | `private fun normalizeSliderStyle(value: String): String {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
