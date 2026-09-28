# SettingsScreen.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt`  
**SHA-256:** `f50e0fe495149fb5875e5c4ec05c29db320ce1cfdf6850185bb6eff0595baaa8`  
**Líneas:** 1817  
**Package:** `com.example.mynotes.ui`

## 1. Para qué existe este archivo

Pantalla completa de Configuración. Conecta controles visuales con callbacks del SettingsViewModel y organiza modo básico/avanzado.

## 2. Tipos/clases declarados

- Línea **105** — `private data  class SliderStyleOption`.

## 3. Estado, constantes y valores importantes

- **`PreviewButtonVerticalGap`** (línea 106) inicia con `4.dp`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`PreviewToSliderGap`** (línea 108) inicia con `10.dp`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`SoundHapticPanelBottomPadding`** (línea 109) inicia con `18.dp`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`SliderStyleOptions`** (línea 110) inicia con `listOf(SliderStyleOption("minimal"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`context`** (línea 140) inicia con `LocalContext.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`fontFamily`** (línea 141) inicia con `remember(settings.font`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedPalette`** (línea 218) inicia con `remember(settings.backgroundColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedPaletteTone`** (línea 221) inicia con `selectedPalette.tones[settings.backgroundToneIndex.coerceIn(0`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsPanelColor`** (línea 222) inicia con `lerp(MaterialTheme.colorScheme.surfaceContainerLow`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsTextColor`** (línea 228) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsSecondaryTextColor`** (línea 229) inicia con `resolveSecondaryUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsGraphicColor`** (línea 230) inicia con `resolveUiGraphicColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsScreenTextColor`** (línea 231) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsScreenSecondaryTextColor`** (línea 232) inicia con `resolveSecondaryUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsScreenGraphicColor`** (línea 234) inicia con `resolveUiGraphicColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsTopBarTextColor`** (línea 235) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`menuBackground`** (línea 236) inicia con `when (settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsMenuTextColor`** (línea 240) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsScrollState`** (línea 241) inicia con `rememberScrollState(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`isAdvancedMode`** (línea 242) inicia con `settings.configurationMode == "advanced"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`updateScope`** (línea 253) inicia con `rememberCoroutineScope(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`installedVersion`** (línea 254) inicia con `remember(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`downloadAndOpenInstaller`** (línea 260) inicia con `{ release ->`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`unknownSourcesLauncher`** (línea 280) inicia con `rememberLauncherForActivityResult(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`pendingRelease`** (línea 284) inicia con `pendingReleasePermission`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`requestDownloadAndInstall`** (línea 294) inicia con `{ release ->`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`picker`** (línea 1262) inicia con `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`primaryColor`** (línea 1298) inicia con `MaterialTheme.colorScheme.primary`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`accentTonalButtonBase`** (línea 1299) inicia con `MaterialTheme.colorScheme.primaryContainer`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`secondaryButtonBase`** (línea 1300) inicia con `MaterialTheme.colorScheme.surfaceContainerHigh`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`actionButtonColors`** (línea 1301) inicia con `remember(accentTonalButtonBase`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`actionButtonContainerColor`** (línea 1310) inicia con `actionButtonColors.container`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`actionButtonContentColor`** (línea 1311) inicia con `actionButtonColors.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`actionButtonBorderColor`** (línea 1312) inicia con `remember(actionButtonContentColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`modeSelectedColors`** (línea 1319) inicia con `remember(primaryColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `SettingsScreen` — líneas 119–1247

**Firma:** `fun SettingsScreen(settings: AppSettings, onConfigurationModeChange: (String) -> Unit, onDarkModeChange: (Boolean) -> Unit, onBackgroundColorChange: (String) -> Unit, onBackgroundToneIndexChange: (Int) -> Unit, onBackgroundIntensityChange: (Float) -> Unit, onSettingsPanelToneChange: (Float) -> Unit, onSurfacePanelIntensityChange: (Float) -> Unit, onHeaderIntensityChange: (Float) -> Unit, onTextColorChange: (String) -> Unit, onTextOutlineEnabledChange: (Boolean) -> Unit, onNoteUiTextColorChange: (String) -> Unit, onSliderStyleChange: (String) -> Unit, onFontChange: (String) -> Unit, onFontSizeChange: (Float) -> Unit, onSoundEffectsEnabledChange: (Boolean) -> Unit, onSoundEffectsVolumeChange: (Float) -> Unit, onSoundEffectsThemeChange: (String) -> Unit, onHapticEffectsEnabledChange: (Boolean) -> Unit, onHapticEffectsIntensityChange: (Float) -> Unit, onHapticEffectsStyleChange: (String) -> Unit, onLanguageChange: (String) -> Unit, onGridColumnsChange: (Int) -> Unit, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit, onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit, onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit, onNoteCardImageHeightChange: (Float) -> Unit, onNoteCardOutlineWidthChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit, onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit, onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onOptionMenuOrderChange: (String) -> Unit, onOptionMenuHiddenItemsChange: (String) -> Unit, onOptionMenuShowIconsChange: (Boolean) -> Unit, onOptionMenuTextColorChange: (String) -> Unit, onOptionMenuOpacityChange: (Float) -> Unit, onPriorityMenuHiddenItemsChange: (String) -> Unit, onColorMenuHiddenItemsChange: (String) -> Unit, onResetOptionMenu: () -> Unit, onPerformanceModeChange: (String) -> Unit, onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit, onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit, onOpenDevelopmentInfo: () -> Unit, onBack: () -> Unit)`

Orquesta la pantalla de Configuración. Calcula colores/typography desde AppSettings, muestra el perfil/modo, secciones básicas o avanzadas, backup, desarrollo y actualizaciones, y llama a callbacks sin escribir preferencias directamente.

**Entradas:**
- `settings: AppSettings`
- `onConfigurationModeChange: (String) -> Unit`
- `onDarkModeChange: (Boolean) -> Unit`
- `onBackgroundColorChange: (String) -> Unit`
- `onBackgroundToneIndexChange: (Int) -> Unit`
- `onBackgroundIntensityChange: (Float) -> Unit`
- `onSettingsPanelToneChange: (Float) -> Unit`
- `onSurfacePanelIntensityChange: (Float) -> Unit`
- `onHeaderIntensityChange: (Float) -> Unit`
- `onTextColorChange: (String) -> Unit`
- `onTextOutlineEnabledChange: (Boolean) -> Unit`
- `onNoteUiTextColorChange: (String) -> Unit`
- `onSliderStyleChange: (String) -> Unit`
- `onFontChange: (String) -> Unit`
- `onFontSizeChange: (Float) -> Unit`
- `onSoundEffectsEnabledChange: (Boolean) -> Unit`
- `onSoundEffectsVolumeChange: (Float) -> Unit`
- `onSoundEffectsThemeChange: (String) -> Unit`
- `onHapticEffectsEnabledChange: (Boolean) -> Unit`
- `onHapticEffectsIntensityChange: (Float) -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Participa en estado/efectos de Compose.
- Inicia o prepara navegación/acción mediante Intent.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `appFontFamily`, `mutableFloatStateOf`, `toFloat`, `LaunchedEffect`, `find`, `coerceIn`, `lerp`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `rememberScrollState`, `rememberCoroutineScope`, `currentVersionName`, `downloadApk`, `launchSystemInstaller`, `getString`, `rememberLauncherForActivityResult`, `StartActivityForResult`.

### `ProfileAndModePanel` — líneas 1250–1548

**Firma:** `private fun ProfileAndModePanel( settings: AppSettings, fontFamily: androidx.compose.ui.text.font.FontFamily, isAdvancedMode: Boolean, profileSize: Float, onProfileSizeValueChange: (Float) -> Unit, onConfigurationModeChange: (String) -> Unit, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: () -> Unit, )`

Cabecera de perfil: foto, edición/recorte, tamaño de avatar con preview en vivo y selector Basic/Advanced.

**Entradas:**
- `settings: AppSettings`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`
- `isAdvancedMode: Boolean`
- `profileSize: Float`
- `onProfileSizeValueChange: (Float) -> Unit`
- `onConfigurationModeChange: (String) -> Unit`
- `onProfileImageUriChange: (String) -> Unit`
- `onProfileImageSizeChange: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lee/escribe Uris mediante ContentResolver.
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `rememberLauncherForActivityResult`, `OpenDocument`, `takePersistableUriPermission`, `play`, `ProfileImageEditorDialog`, `playAction`, `onProfileImageUriChange`, `toString`, `SettingsSectionPanel`, `PaddingValues`, `resolveAdaptiveUiButtonColors`, `ensureUiContrast`, `height`, `coerceIn`, `size`, `clickable`, `isNotBlank`, `managedProfileSourceUri`.

### `SettingTitle` — líneas 1551–1558

**Firma:** `private fun SettingTitle(text: String, color: Color, fontFamily: androidx.compose.ui.text.font.FontFamily)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `text: String`
- `color: Color`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`

**Salida:** Unit o inferido por Kotlin.

### `RoundedPreviewButton` — líneas 1562–1599

**Firma:** `private fun RoundedPreviewButton( text: String, panelBackground: Color, panelContentColor: Color, textColorMode: String, fontFamily: androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit )`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `text: String`
- `panelBackground: Color`
- `panelContentColor: Color`
- `textColorMode: String`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`
- `onClick: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Operaciones/funciones que coordina:** `resolveAdaptiveUiButtonColors`, `Button`, `defaultMinSize`, `RoundedCornerShape`, `PaddingValues`, `buttonColors`.

### `SettingTitleRow` — líneas 1602–1615

**Firma:** `private fun SettingTitleRow(title: String, value: String, color: Color, fontFamily: androidx.compose.ui.text.font.FontFamily)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `title: String`
- `value: String`
- `color: Color`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `fillMaxWidth`, `SettingTitle`, `weight`.

### `TextColorSelector` — líneas 1618–1658

**Firma:** `private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `selected: String`
- `onSelected: (String) -> Unit`
- `fontKey: String`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Operaciones/funciones que coordina:** `appFontFamily`, `fillMaxWidth`, `spacedBy`, `TextColorButton`, `weight`, `playAction`, `onSelected`.

### `TextColorButton` — líneas 1661–1708

**Firma:** `private fun TextColorButton(modifier: Modifier, label: String, sampleColor: Color, selected: Boolean, fontFamily: androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `modifier: Modifier`
- `label: String`
- `sampleColor: Color`
- `selected: Boolean`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`
- `onClick: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `resolveUiTextColor`, `ensureUiContrast`, `RoundedCornerShape`, `BorderStroke`, `fillMaxWidth`, `padding`, `size`, `copy`.

### `SettingDropdown` — líneas 1711–1816

**Firma:** `private fun SettingDropdown(title: String, selectedLabel: String, options: List<Pair<String, String>>, textColor: Color, textColorMode: String, menuBackground: Color, menuTextColor: Color, fontFamily: androidx.compose.ui.text.font.FontFamily, playDefaultSelectionFeedback: Boolean = true, onSelected: (String) -> Unit)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `title: String`
- `selectedLabel: String`
- `options: List<Pair<String, String>>`
- `textColor: Color`
- `textColorMode: String`
- `menuBackground: Color`
- `menuTextColor: Color`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`
- `playDefaultSelectionFeedback: Boolean = true`
- `onSelected: (String) -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `resolveUiTextColor`, `resolveUiGraphicColor`, `ensureUiContrast`, `maxOf`, `fillMaxWidth`, `weight`, `width`, `playAction`, `RoundedCornerShape`, `BorderStroke`, `padding`, `size`, `AppDropdownMenu`, `heightIn`, `PopupProperties`, `DropdownMenuItem`, `height`, `PaddingValues`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.
- Usa `com.example.mynotes.update.GitHubUpdateManager`.
- Usa `com.example.mynotes.ui.components.AppDropdownMenu`.
- Usa `com.example.mynotes.settings.AppSettings`.
- Usa `com.example.mynotes.ui.components.BackupRestoreSection`.
- Usa `com.example.mynotes.ui.components.ExtremeCustomizationSection`.
- Usa `com.example.mynotes.ui.components.OptionsMenuCustomizationSection`.
- Usa `com.example.mynotes.ui.components.PaletteSelector`.
- Usa `com.example.mynotes.ui.components.StyledSettingsSlider`.
- Usa `com.example.mynotes.ui.components.SettingsSectionPanel`.
- Usa `com.example.mynotes.ui.components.ScrollPositionCapsule`.
- Usa `com.example.mynotes.ui.components.ProfileImageEditorDialog`.
- Usa `com.example.mynotes.ui.components.clearManagedProfileImages`.
- Usa `com.example.mynotes.ui.components.managedProfileSourceUri`.
- Usa `com.example.mynotes.ui.motion.AnimatedScreenEntry`.
- Usa `com.example.mynotes.ui.sound.UiActionSound`.
- Usa `com.example.mynotes.ui.sound.UiSound`.
- Usa `com.example.mynotes.ui.sound.UiSoundPlayer`.
- Usa `com.example.mynotes.ui.sound.UiHaptic`.
- Usa `com.example.mynotes.ui.sound.UiHapticPlayer`.
- Usa `com.example.mynotes.ui.theme.PaletteCatalog`.
- Usa `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`.
- Usa `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`.
- Usa `com.example.mynotes.ui.theme.appFontFamily`.
- Usa `com.example.mynotes.ui.theme.resolveUiTextColor`.
- Usa `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`.
- Usa `com.example.mynotes.ui.theme.resolveUiGraphicColor`.
- Usa `com.example.mynotes.ui.theme.ensureUiContrast`.

## 6. Recursos Android que utiliza

- `R.string`: `configuration_mode_advanced`, `configuration_mode_basic`, `configuration_mode_settings_description`, `configuration_mode_settings_title`, `development_info_settings_description`, `development_info_settings_title`, `extreme_change_photo`, `extreme_edit_photo`, `extreme_profile`, `extreme_profile_size`, `extreme_remove_photo`, `haptic_effects`, `haptic_effects_description`, `haptic_effects_enabled`, `haptic_effects_intensity`, `haptic_effects_preview`, `haptic_effects_style`, `haptic_style_crisp`, `haptic_style_deep`, `haptic_style_double`, `haptic_style_echo`, `haptic_style_heartbeat`, `haptic_style_heavy`, `haptic_style_mechanical`, `haptic_style_minimal`, `haptic_style_pulse`, `haptic_style_ripple`, `haptic_style_snap`, `haptic_style_soft`, `haptic_style_spring`, `haptic_style_stepped`, `haptic_style_triple`, `haptic_style_wave`, `language_system`, `mock_appearance`, `mock_appearance_description`, `mock_auto`, `mock_back`, `mock_background_intensity`, `mock_black`, `mock_color_palette`, `mock_columns`, `mock_dark_mode`, `mock_dark_mode_description`, `mock_font`, `mock_font_default`, `mock_font_monospace`, `mock_font_serif`, `mock_font_size`, `mock_header_intensity`, `mock_language`, `mock_note_menu_text_color`, `mock_palette_tone`, `mock_settings`, `mock_slider_capsule`, `mock_slider_dots`, `mock_slider_floating`, `mock_slider_glass`, `mock_slider_glow`, `mock_slider_gradient`, `mock_slider_line_pill`, `mock_slider_minimal`, `mock_slider_neumorphic`, `mock_slider_segmented`, `mock_slider_style`, `mock_text_color`, `mock_white`, `settings_panel_tone`, `sound_effects`, `sound_effects_description`, `sound_effects_enabled`, `sound_effects_preview`, `sound_effects_theme`, `sound_effects_volume`, `sound_theme_arcade`, `sound_theme_aurora`, `sound_theme_bubble`, `sound_theme_camera`, `sound_theme_chime`, `sound_theme_classic`, `sound_theme_digital`, `sound_theme_expressive`, `sound_theme_fluid`, `sound_theme_glass`, `sound_theme_material`, `sound_theme_mechanical`, `sound_theme_metal`, `sound_theme_minimal`, `sound_theme_neon`, `sound_theme_paper`, `sound_theme_pixel`, `sound_theme_pop`, `sound_theme_prism`, `sound_theme_pulse`, `sound_theme_retro`, `sound_theme_soft`, `sound_theme_space`, `sound_theme_synth`, `sound_theme_typewriter`, `sound_theme_wood`, `surface_panels_intensity`, `surface_panels_intensity_description`, `text_black_outline`, `text_black_outline_description`, `update_available`, `update_check`, `update_checking`, `update_current_version`, `update_download_error`, `update_download_install`, `update_downloading`, `update_error`, `update_install_error`, `update_no_release`, `update_open_release`, `update_permission_required`, `update_release_without_apk`, `update_settings_description`, `update_settings_title`, `update_up_to_date`

## 7. Tecnologías y efectos relevantes

- Lanza trabajo asíncrono mediante coroutines.
- Lee/escribe Uris mediante ContentResolver.
- Participa en estado/efectos de Compose.
- Inicia o prepara navegación/acción mediante Intent.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `SettingsScreen` — Orquesta la pantalla de Configuración. Calcula colores/typography desde AppSettings, muestra el perfil/modo, secciones básicas o avanzadas, backup, desarrollo y actualizaciones, y llama a callbacks sin escribir preferencias directamente.

## 9. Qué no debe romperse al modificarlo

- No renombrar claves persistentes sin migración; ajustes ya guardados dependen de ellas.
- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.
- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Pantalla completa de Configuración. Conecta controles visuales con callbacks del SettingsViewModel y organiza modo básico/avanzado. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
