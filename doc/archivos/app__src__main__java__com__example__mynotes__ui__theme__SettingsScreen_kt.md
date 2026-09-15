# SettingsScreen.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt`  
**SHA-256 actual del archivo, sin modificar:** `fe1d671f4b47d54ad7134b2f1b61772d9ed05d3bf7477902ee648773771a776c`  
**Líneas del código real:** 1083  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Pantalla principal de configuración. Expone paletas, fuentes, sonido, vibración, columnas, rendimiento, animaciones, personalización y navegación a información del desarrollo.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes desplazan Información del desarrollo al final, aumentan el margen izquierdo de títulos/descripciones y mantienen dropdowns compactos/no focusables.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **79 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material.icons.filled.ChevronRight`, `androidx.compose.material.icons.filled.Code`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.lerp`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.BackupRestoreSection`, `com.example.mynotes.ui.components.ExtremeCustomizationSection`, `com.example.mynotes.ui.components.OptionsMenuCustomizationSection`, `com.example.mynotes.ui.components.PaletteSelector`, `com.example.mynotes.ui.components.StyledSettingsSlider`, `com.example.mynotes.ui.components.SettingsSectionPanel`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.sound.UiHaptic`, `com.example.mynotes.ui.sound.UiHapticPlayer`, `com.example.mynotes.ui.theme.PaletteCatalog`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`

### Java / Kotlin estándar

`kotlin.math.roundToInt`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 2 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Elvis `?:`: 3 aparición/apariciones.** define un fallback explícito cuando el operando izquierdo es nulo.
- **Acotaciones `coerceIn/AtLeast/AtMost`: 4 aparición/apariciones.** imponen límites numéricos para evitar valores fuera del rango aceptado.
- **Límites visuales: 7 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Estado Compose: 17 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `SliderStyleOption` — class, líneas 84–84

```kotlin
private data class SliderStyleOption(val key: String, val labelRes: Int)
```

**Firma/entrada.** `private data class SliderStyleOption(val key: String, val labelRes: Int) private val SliderStyleOptions = listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule", R.string.mock_slider_capsule), SliderStyleOption("glow", R.string.mock_slider_glow), SliderStyleOption("glass", R.string.mock_slider_glass), SliderStyleOption("segmented", R.string.mock_slider_segmented), SliderStyleOption("dots", R.string.mock_slider_dots), SliderStyleOption("gradient", R.string.mock_slider_gradient), SliderStyleOption("neumorphic", R.string.mock_slider_neumorphic), SliderStyleOption("line_pill", R.string.mock_slider_line_pill), SliderStyleOption("floating", R.string.mock_slider_floating)) @OptIn(ExperimentalMaterial3Api::class) @Composable fun SettingsScreen(settings: AppS`

**Parámetros.**
- `val key: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val labelRes: Int) private val SliderStyleOptions = listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule", R.string.mock_slider_capsule), SliderStyleOption("glow", R.string.mock_slider_glow), SliderStyleOption("glass", R.string.mock_slider_glass), SliderStyleOption("segmented", R.string.mock_slider_segmented), SliderStyleOption("dots", R.string.mock_slider_dots), SliderStyleOption("gradient", R.string.mock_slider_gradient), SliderStyleOption("neumorphic", R.string.mock_slider_neumorphic), SliderStyleOption("line_pill", R.string.mock_slider_line_pill), SliderStyleOption("floating", R.string.mock_slider_floating)) @OptIn(ExperimentalMaterial3Api::class) @Composable fun SettingsScreen(settings: AppSettings, onDarkModeChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca. Tiene valor por defecto y puede omitirse en la llamada.
- `onBackgroundColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onBackgroundToneIndexChange: (Int) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onBackgroundIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSettingsPanelToneChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSurfacePanelIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHeaderIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onTextColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onTextOutlineEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteUiTextColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSliderStyleChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onFontChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onFontSizeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSoundEffectsEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSoundEffectsVolumeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSoundEffectsThemeChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHapticEffectsEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHapticEffectsIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHapticEffectsStyleChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onLanguageChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onGridColumnsChange: (Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.2 `SettingsScreen` — fun, líneas 94–858

```kotlin
fun SettingsScreen(settings: AppSettings, onDarkModeChange: (Boolean) -> Unit, onBackgroundColorChange: (String) -> Unit,
    onBackgroundToneIndexChange: (Int) -> Unit, onBackgroundIntensityChange: (Float) -> Unit, onSettingsPanelToneChange: (Float) -> Unit,
    onSurfacePanelIntensityChange: (Float) -> Unit, onHeaderIntensityChange: (Float) -> Unit, onTextColorChange: (String) -> Unit,
    onTextOutlineEnabledChange: (Boolean) -> Unit, onNoteUiTextColorChange: (String) -> Unit, onSliderStyleChange: (String) -> Unit,
    onFontChange: (String) -> Unit, onFontSizeChange: (Float) -> Unit, onSoundEffectsEnabledChange: (Boolean) -> Unit,
    onSoundEffectsVolumeChange: (Float) -> Unit, onSoundEffectsThemeChange: (String) -> Unit,
    onHapticEffectsEnabledChange: (Boolean) -> Unit, onHapticEffectsIntensityChange: (Float) -> Unit,
    onHapticEffectsStyleChange: (String) -> Unit, onLanguageChange: (String) -> Unit, onGridColumnsChange: (Int) -> Unit,
    onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit, onIconStyleChange: (String) -> Unit,
    onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit, onNoteCardCornerRadiusChange: (Float) -> Unit,
    onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit, onNoteCardImageHeightChange: (Float) -> Unit,
    onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit, onNoteLineSpacingChange: (Float) -> Unit,
    onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit, onShowFavoriteIconChange: (Boolean) -> Unit,
    onFabSizeChange: (Float) -> Unit, onOptionMenuOrderChange: (String) -> Unit, onOptionMenuHiddenItemsChange: (String) -> Unit,
    onOptionMenuShowIconsChange: (Boolean) -> Unit, onOptionMenuTextColorChange: (String) -> Unit,
    onOptionMenuOpacityChange: (Float) -> Unit, onPriorityMenuHiddenItemsChange: (String) -> Unit,
    onColorMenuHiddenItemsChange: (String) -> Unit, onResetOptionMenu: () -> Unit, onPerformanceModeChange: (String) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,
    onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit, onOpenDevelopmentInfo: () -> Unit,
    onBack: () -> Unit) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) {
            appFontFamily(settings.font)
        }
    var localTone by
        remember {
            mutableFloatStateOf(settings.backgroundToneIndex.toFloat())
        }
    var localBackgroundIntensity by
        remember {
            mutableFloatStateOf(settings.backgroundIntensity)
        }
    var localSettingsPanelTone by
        remember {
            mutableFloatStateOf(settings.settingsPanelTone)
        }
    var localSurfacePanelIntensity by
        remember {
            mutableFloatStateOf(settings.surfacePanelIntensity)
        }
    var localHeaderIntensity by
        remember {
            mutableFloatStateOf(settings.headerIntensity)
        }
    var localFontSize by
        remember {
            mutableFloatStateOf(settings.fontSize)
        }
    var localSoundEffectsVolume by
        remember {
            mutableFloatStateOf(settings.soundEffectsVolume)
        }
    var localHapticEffectsIntensity by
        remember {
            mutableFloatStateOf(settings.hapticEffectsIntensity)
        }
    LaunchedEffect(settings.backgroundToneIndex) {
        localTone = settings.backgroundToneIndex.toFloat()
    }
    LaunchedEffect(settings.backgroundIntensity) {
        localBackgroundIntensity = settings.backgroundIntensity
    }
    LaunchedEffect(settings.settingsPanelTone) {
        localSettingsPanelTone = settings.settingsPanelTone
    }
    LaunchedEffect(settings.surfacePanelIntensity) {
        localSurfacePanelIntensity = settings.surfacePanelIntensity
    }
    LaunchedEffect(settings.headerIntensity) {
        localHeaderIntensity = settings.headerIntensity
    }
    LaunchedEffect(settings.fontSize) {
        localFontSize = settings.fontSize
    }
    LaunchedEffect(settings.soundEffectsVolume) {
        localSoundEffectsVolume = settings.soundEffectsVolume
    }
    LaunchedEffect(settings.hapticEffectsIntensity) {
        localHapticEffectsIntensity = settings.hapticEffectsIntensity
    }
    /*
     * ==========================================================
     * TONALIDAD DEL PANEL DE CONFIGURACIÓN
     * ==========================================================
     *
     * Este es el gran rectángulo claro que contiene:
     * paletas, sliders, texto, fuente, idioma, columnas, etc.
     *
     * 0% mantiene el surfaceContainerLow de Material.
     * 100% lo lleva al tono exacto seleccionado de la paleta.
     */
    val selectedPalette = remember(settings.backgroundColor) {
            PaletteCatalog.find(settings.backgroundColor)
        }
    val selectedPaletteTone = selectedPalette.tones[settings.backgroundToneIndex.coerceIn(0, 3)]
    val settingsPanelColor = lerp(MaterialTheme.colorScheme.surfaceContainerLow, selectedPaletteTone, (localSettingsPanelTone / 100f)
                .coerceIn(0f, 1f))
    /*
     * En automático el texto se calcula contra el color REAL del panel de
     * Configuración. Negro y blanco siguen siendo anulaciones manuales.
     */
    val settingsTextColor = resolveUiTextColor(value = settings.textColor, background = settingsPanelColor)
    val settingsSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = settingsPanelColor)
    val settingsGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = settingsPanelColor)
    val menuBackground = when (settings.textColor) {
            "white" -> MaterialTheme.colorScheme.inverseSurface
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val settingsMenuTextColor = resolveUiTextColor(value = settings.textColor, background = menuBackground)
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(title = {
                    Text(text = stringResource(R.string.mock_settings),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp)
                },
                navigationIcon = {
                    TextButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                            onBack()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back),
                            modifier = Modifier.size(25.dp))
                    }
                })
        }) {
            paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = 14.dp,
                            end = 14.dp, bottom = 32.dp)) {
            Surface(modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = settingsPanelColor,
                tonalElevation = 1.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = stringResource(R.string.mock_appearance),
                        color = settingsTextColor,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp)
                    Text(text = stringResource(R.string.mock_appearance_description),
                        modifier = Modifier.padding(top = 2.dp),
                        color = settingsSecondaryTextColor,
                        fontFamily = fontFamily,
                        fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    ExtremeCustomizationSection(settings = settings, fontFamily = fontFamily, textColor = settingsTextColor,
                        secondaryTextColor = settingsSecondaryTextColor, graphicColor = settingsGraphicColor,
                        onProfileImageUriChange = onProfileImageUriChange, onProfileImageSizeChange = onProfileImageSizeChange,
                        onIconStyleChange = onIconStyleChange, onIconSizeChange = onIconSizeChange,
                        onAccentColorChange = onAccentColorChange, onNoteCardCornerRadiusChange = onNoteCardCornerRadiusChange,
                        onNoteCardElevationChange = onNoteCardElevationChange, onNoteCardPaddingChange = onNoteCardPaddingChange,
                        onNoteCardImageHeightChange = onNoteCardImageHeightChange, onNoteTitleMaxLinesChange = onNoteTitleMaxLinesChange,
                        onNoteContentMaxLinesChange = onNoteContentMaxLinesChange, onNoteLineSpacingChange = onNoteLineSpacingChange,
                        onShowNoteDateChange = onShowNoteDateChange, onShowCategoryChipChange = onShowCategoryChipChange,
                        onShowFavoriteIconChange = onShowFavoriteIconChange, onFabSizeChange = onFabSizeChange,
                        onPerformanceModeChange = onPerformanceModeChange, onAnimationsEnabledChange = onAnimationsEnabledChange,
                        onAnimationStyleChange = onAnimationStyleChange, onAnimationEasingChange = onAnimationEasingChange,
                        onAnimationSpeedChange = onAnimationSpeedChange, onAnimationIntensityChange = onAnimationIntensityChange)
                    Spacer(modifier = Modifier.height(18.dp))
                    OptionsMenuCustomizationSection(settings = settings, fontFamily = fontFamily, textColor = settingsTextColor,
                        secondaryTextColor = settingsSecondaryTextColor, graphicColor = settingsGraphicColor,
                        onOrderChange = onOptionMenuOrderChange, onHiddenItemsChange = onOptionMenuHiddenItemsChange,
                        onShowIconsChange = onOptionMenuShowIconsChange, onTextColorChange = onOptionMenuTextColorChange,
                        onOpacityChange = onOptionMenuOpacityChange, onPriorityHiddenItemsChange = onPriorityMenuHiddenItemsChange,
                        onColorHiddenItemsChange = onColorMenuHiddenItemsChange, onReset = onResetOptionMenu)
                    Spacer(modifier = Modifier.height(22.dp))
                    BackupRestoreSection(settings = settings, fontFamily = fontFamily, textColor = settingsTextColor,
                        secondaryTextColor = settingsSecondaryTextColor, graphicColor = settingsGraphicColor)
                    Spacer(modifier = Modifier.height(22.dp))
                    /*
                     * -------------------------------------------------
                     * COLOR PALETTE
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitle(text = stringResource(R.string.mock_color_palette), color =
                                panelColors.text, fontFamily = fontFamily)
                        Spacer(modifier = Modifier.height(10.dp))
                        PaletteSelector(palettes = PaletteCatalog.palettes,
                            selectedPaletteKey = settings.backgroundColor,
                            selectedToneIndex = settings.backgroundToneIndex,
                            /*
                             * Pulsar la tarjeta cambia de paleta y mantiene
                             * el tono actual.
                             */
                            onPaletteSelected = {
                                    paletteKey ->
                                onBackgroundColorChange(paletteKey)
                            },
                            /*
                             * Pulsar CUALQUIERA de los cuatro círculos:
                             * 1) selecciona la paleta;
                             * 2) selecciona ese tono exacto.
                             */
                            onToneSelected = {
                                    paletteKey, toneIndex ->
                                localTone = toneIndex.toFloat()
                                onBackgroundColorChange(paletteKey)
                                onBackgroundToneIndexChange(toneIndex)
                            },
                            animationsEnabled = settings.animationsEnabled,
                            animationSpeed = settings.animationSpeed,
                            textColorMode = settings.textColor,
                            fontFamily = fontFamily)
                        Spacer(modifier = Modifier.height(20.dp))
                        /*
                         * Palette tone permanece como control secundario.
                         * La barra ya no utiliza el mismo tono de fondo,
                         * así no desaparece visualmente.
                         */
                        SettingTitleRow(title = stringResource(R.string.mock_palette_tone),
                            value = "${localTone.roundToInt() + 1}/4",
                            color = panelColors.text,
                            fontFamily = fontFamily)
                        StyledSettingsSlider(value = localTone,
                            onValueChange = {
                                localTone = it
                            },
                            onValueChangeFinished = {
                                onBackgroundToneIndexChange(localTone.roundToInt().coerceIn(0, 3))
                            },
                            valueRange = 0f..3f,
                            steps = 2,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localTone.roundToInt() + 1}/4")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    /*
                     * -------------------------------------------------
                     * TONALIDAD DEL PANEL
                     * -------------------------------------------------
                     *
                     * Controla el gran rectángulo que envuelve toda
                     * la pantalla de Configuración.
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitleRow(title = stringResource(R.string.settings_panel_tone),
                            value = "${localSettingsPanelTone.roundToInt()}%",
                            color = panelColors.text,
                            fontFamily = fontFamily)
                        StyledSettingsSlider(value = localSettingsPanelTone,
                            onValueChange = {
                                localSettingsPanelTone = it
                            },
                            onValueChangeFinished = {
                                onSettingsPanelToneChange(localSettingsPanelTone)
                            },
                            valueRange = 0f..100f,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localSettingsPanelTone.roundToInt()}%")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitleRow(title = stringResource(R.string.mock_background_intensity
                                ),
                            value = "${localBackgroundIntensity.roundToInt()}%",
                            color = panelColors.text,
                            fontFamily = fontFamily)
                        StyledSettingsSlider(value = localBackgroundIntensity,
                            onValueChange = {
                                localBackgroundIntensity = it
                            },
                            onValueChangeFinished = {
                                onBackgroundIntensityChange(localBackgroundIntensity)
                            },
                            valueRange = 0f..100f,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localBackgroundIntensity.roundToInt()}%")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitleRow(title = stringResource(R.string.mock_header_intensity),
                            value = "${localHeaderIntensity.roundToInt()}%",
                            color = panelColors.text,
                            fontFamily = fontFamily)
                        StyledSettingsSlider(value = localHeaderIntensity,
                            onValueChange = {
                                localHeaderIntensity = it
                            },
                            onValueChangeFinished = {
                                onHeaderIntensityChange(localHeaderIntensity)
                            },
                            valueRange = 0f..100f,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localHeaderIntensity.roundToInt()}%")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    /*
                     * -------------------------------------------------
                     * RECUADROS / PANELES TRANSLÚCIDOS
                     * -------------------------------------------------
                     *
                     * Controla surfaceVariant y los tres niveles
                     * surfaceContainer usados por buscador, chips,
                     * tarjetas de Configuración, adjuntos, menús, etc.
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitleRow(title = stringResource(R.string.surface_panels_intensity
                                ),
                            value = "${localSurfacePanelIntensity.roundToInt()}%",
                            color = panelColors.text,
                            fontFamily = fontFamily)
                        StyledSettingsSlider(value = localSurfacePanelIntensity,
                            onValueChange = {
                                localSurfacePanelIntensity = it
                            },
                            onValueChangeFinished = {
                                onSurfacePanelIntensityChange(localSurfacePanelIntensity)
                            },
                            valueRange = 0f..100f,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localSurfacePanelIntensity.roundToInt()}%")
                        Text(text = stringResource(R.string.surface_panels_intensity_description),
                            modifier = Modifier.padding(top = 2.dp),
                            color = panelColors.secondaryText,
                            fontFamily = fontFamily,
                            fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    /*
                     * -------------------------------------------------
                     * TEXT COLOR
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitle(text = stringResource(R.string.mock_text_color), color =
                                panelColors.text, fontFamily = fontFamily)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextColorSelector(selected = settings.textColor,
                            onSelected = onTextColorChange,
                            fontKey = settings.font)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 0.dp),
                        horizontalOutset = 0.dp) { panelColors ->
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.text_black_outline),
                                    color = panelColors.text,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp)
                                Text(text = stringResource(R.string.text_black_outline_description),
                                    modifier = Modifier.padding(top = 2.dp),
                                    color = panelColors.secondaryText,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp)
                            }
                            Switch(checked = settings.textOutlineEnabled,
                                onCheckedChange = { checked -> UiSoundPlayer.playToggle(context = context, checked = checked)
                                    onTextOutlineEnabledChange(checked)
                                })
                        }
                    }
                    Spacer(modifier = Modifier.height(17.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingTitle(text = stringResource(R.string.mock_note_menu_text_color),
                            color = panelColors.text, fontFamily = fontFamily)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextColorSelector(selected = settings.noteUiTextColor,
                            onSelected = onNoteUiTextColorChange,
                            fontKey = settings.font)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    /*
                     * -------------------------------------------------
                     * FONT
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingDropdown(title = stringResource(R.string.mock_font),
                            selectedLabel = when (settings.font) {
                                    "google_sans" -> "Google Sans (Auto)"
                                    "google_sans_regular" -> "Google Sans Regular"
                                    "google_sans_medium" -> "Google Sans Medium"
                                    "google_sans_bold" -> "Google Sans Bold"
                                    "google_sans_italic" -> "Google Sans Italic"
                                    "google_sans_medium_italic" -> "Google Sans Medium Italic"
                                    "google_sans_bold_italic" -> "Google Sans Bold Italic"
                                    "google_sans_flex" -> "Google Sans Flex"
                                    "serif" -> stringResource(R.string.mock_font_serif)
                                    "monospace" -> stringResource(R.string.mock_font_monospace)
                                    else -> stringResource(R.string.mock_font_default)
                                },
                            options = listOf("default" to
                                        stringResource(R.string.mock_font_default), "google_sans" to
                                        "Google Sans (Auto)", "google_sans_regular" to
                                        "Google Sans Regular", "google_sans_medium" to
                                        "Google Sans Medium", "google_sans_bold" to
                                        "Google Sans Bold", "google_sans_italic" to
                                        "Google Sans Italic", "google_sans_medium_italic" to
                                        "Google Sans Medium Italic", "google_sans_bold_italic" to
                                        "Google Sans Bold Italic", "google_sans_flex" to
                                        "Google Sans Flex", "serif" to
                                        stringResource(R.string.mock_font_serif), "monospace" to
                                        stringResource(R.string.mock_font_monospace)),
                            textColor = panelColors.text,
                            textColorMode = settings.textColor,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onSelected = onFontChange)
                        Spacer(modifier = Modifier.height(14.dp))
                        SettingTitleRow(title = stringResource(R.string.mock_font_size),
                            value = "${localFontSize.roundToInt()} sp",
                            color = panelColors.text,
                            fontFamily = fontFamily)
                        StyledSettingsSlider(value = localFontSize,
                            onValueChange = {
                                localFontSize = it
                            },
                            onValueChangeFinished = {
                                onFontSizeChange(localFontSize)
                            },
                            valueRange = 12f..28f,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localFontSize.roundToInt()} sp")
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    /*
                     * -------------------------------------------------
                     * SOUND EFFECTS
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 10.dp, end = 0.dp, bottom = 10.dp),
                        horizontalOutset = 8.dp) { panelColors -> Text(text = stringResource(R.string.sound_effects),
                            color = panelColors.text,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp)
                        Text(text = stringResource(R.string.sound_effects_description),
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                            color = panelColors.secondaryText,
                            fontFamily = fontFamily,
                            fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = stringResource(R.string.sound_effects_enabled),
                                color = panelColors.text,
                                fontFamily = fontFamily,
                                fontSize = 14.sp)
                            Switch(checked = settings.soundEffectsEnabled,
                                onCheckedChange = { checked -> UiSoundPlayer.playToggle(context = context, checked = checked, force = true)
                                    onSoundEffectsEnabledChange(checked)
                                })
                        }
                        if (settings.soundEffectsEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            SettingDropdown(title = stringResource(R.string.sound_effects_theme),
                                selectedLabel = when (settings.soundEffectsTheme) {
                                        "soft" -> stringResource(R.string.sound_theme_soft)
                                        "digital" -> stringResource(R.string.sound_theme_digital)
                                        "glass" -> stringResource(R.string.sound_theme_glass)
                                        "retro" -> stringResource(R.string.sound_theme_retro)
                                        "pop" -> stringResource(R.string.sound_theme_pop)
                                        "mechanical" -> stringResource(R.string.sound_theme_mechanical)
                                        "bubble" -> stringResource(R.string.sound_theme_bubble)
                                        "arcade" -> stringResource(R.string.sound_theme_arcade)
                                        "wood" -> stringResource(R.string.sound_theme_wood)
                                        "synth" -> stringResource(R.string.sound_theme_synth)
                                        "minimal" -> stringResource(R.string.sound_theme_minimal)
                                        "camera" -> stringResource(R.string.sound_theme_camera)
                                        "typewriter" -> stringResource(R.string.sound_theme_typewriter)
                                        "metal" -> stringResource(R.string.sound_theme_metal)
                                        "pixel" -> stringResource(R.string.sound_theme_pixel)
                                        "space" -> stringResource(R.string.sound_theme_space)
                                        "chime" -> stringResource(R.string.sound_theme_chime)
                                        "paper" -> stringResource(R.string.sound_theme_paper)
                                        "neon" -> stringResource(R.string.sound_theme_neon)
                                        else -> stringResource(R.string.sound_theme_classic)
                                    },
                                options = listOf("classic" to stringResource(R.string.sound_theme_classic),
                                        "soft" to stringResource(R.string.sound_theme_soft),
                                        "digital" to stringResource(R.string.sound_theme_digital),
                                        "glass" to stringResource(R.string.sound_theme_glass),
                                        "retro" to stringResource(R.string.sound_theme_retro),
                                        "pop" to stringResource(R.string.sound_theme_pop),
                                        "mechanical" to stringResource(R.string.sound_theme_mechanical),
                                        "bubble" to stringResource(R.string.sound_theme_bubble),
                                        "arcade" to stringResource(R.string.sound_theme_arcade),
                                        "wood" to stringResource(R.string.sound_theme_wood),
                                        "synth" to stringResource(R.string.sound_theme_synth),
                                        "minimal" to stringResource(R.string.sound_theme_minimal),
                                        "camera" to stringResource(R.string.sound_theme_camera),
                                        "typewriter" to stringResource(R.string.sound_theme_typewriter),
                                        "metal" to stringResource(R.string.sound_theme_metal),
                                        "pixel" to stringResource(R.string.sound_theme_pixel),
                                        "space" to stringResource(R.string.sound_theme_space),
                                        "chime" to stringResource(R.string.sound_theme_chime),
                                        "paper" to stringResource(R.string.sound_theme_paper),
                                        "neon" to stringResource(R.string.sound_theme_neon)),
                                textColor = panelColors.text,
                                textColorMode = settings.textColor,
                                menuBackground = menuBackground,
                                menuTextColor = settingsMenuTextColor,
                                fontFamily = fontFamily,
                                onSelected = { selectedTheme -> onSoundEffectsThemeChange(selectedTheme)
                                    UiSoundPlayer.previewTheme(context = context, theme = selectedTheme, sound = UiSound.Edit,
                                        volumePercent = localSoundEffectsVolume)
                                })
                            TextButton(onClick = {
                                    UiSoundPlayer.previewTheme(context = context, theme = settings.soundEffectsTheme,
                                        sound = UiSound.Attachment, volumePercent = localSoundEffectsVolume)
                                }, colors = ButtonDefaults.textButtonColors(contentColor = panelColors.text)) {
                                Text(text = stringResource(R.string.sound_effects_preview), fontFamily = fontFamily)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            SettingTitleRow(title = stringResource(R.string.sound_effects_volume),
                                value = "${localSoundEffectsVolume.roundToInt()}%",
                                color = panelColors.text,
                                fontFamily = fontFamily)
                            StyledSettingsSlider(value = localSoundEffectsVolume,
                                onValueChange = {
                                    localSoundEffectsVolume = it
                                },
                                onValueChangeFinished = {
                                    onSoundEffectsVolumeChange(localSoundEffectsVolume)
                                },
                                valueRange = 0f..100f,
                                steps = 19,
                                activeColor = MaterialTheme.colorScheme.primary,
                                inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                style = settings.sliderStyle,
                                valueLabel = "${localSoundEffectsVolume.roundToInt()}%")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    /*
                     * -------------------------------------------------
                     * HAPTIC / VIBRATION EFFECTS
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 10.dp, end = 0.dp, bottom = 10.dp),
                        horizontalOutset = 8.dp) { panelColors -> Text(text = stringResource(R.string.haptic_effects),
                            color = panelColors.text,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp)
                        Text(text = stringResource(R.string.haptic_effects_description),
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                            color = panelColors.secondaryText,
                            fontFamily = fontFamily,
                            fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = stringResource(R.string.haptic_effects_enabled),
                                color = panelColors.text,
                                fontFamily = fontFamily,
                                fontSize = 14.sp)
                            Switch(checked = settings.hapticEffectsEnabled,
                                onCheckedChange = { checked -> UiHapticPlayer.playToggle(context = context, checked = checked, force = true
                                    )
                                    onHapticEffectsEnabledChange(checked)
                                })
                        }
                        if (settings.hapticEffectsEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            SettingDropdown(title = stringResource(R.string.haptic_effects_style),
                                selectedLabel = when (settings.hapticEffectsStyle) {
                                        "crisp" -> stringResource(R.string.haptic_style_crisp)
                                        "deep" -> stringResource(R.string.haptic_style_deep)
                                        "double" -> stringResource(R.string.haptic_style_double)
                                        "pulse" -> stringResource(R.string.haptic_style_pulse)
                                        "stepped" -> stringResource(R.string.haptic_style_stepped)
                                        "mechanical" -> stringResource(R.string.haptic_style_mechanical)
                                        "minimal" -> stringResource(R.string.haptic_style_minimal)
                                        "triple" -> stringResource(R.string.haptic_style_triple)
                                        "ripple" -> stringResource(R.string.haptic_style_ripple)
                                        "heartbeat" -> stringResource(R.string.haptic_style_heartbeat)
                                        "snap" -> stringResource(R.string.haptic_style_snap)
                                        "wave" -> stringResource(R.string.haptic_style_wave)
                                        "heavy" -> stringResource(R.string.haptic_style_heavy)
                                        "spring" -> stringResource(R.string.haptic_style_spring)
                                        "echo" -> stringResource(R.string.haptic_style_echo)
                                        else -> stringResource(R.string.haptic_style_soft)
                                    },
                                options = listOf("soft" to stringResource(R.string.haptic_style_soft),
                                        "crisp" to stringResource(R.string.haptic_style_crisp),
                                        "deep" to stringResource(R.string.haptic_style_deep),
                                        "double" to stringResource(R.string.haptic_style_double),
                                        "pulse" to stringResource(R.string.haptic_style_pulse),
                                        "stepped" to stringResource(R.string.haptic_style_stepped),
                                        "mechanical" to stringResource(R.string.haptic_style_mechanical),
                                        "minimal" to stringResource(R.string.haptic_style_minimal),
                                        "triple" to stringResource(R.string.haptic_style_triple),
                                        "ripple" to stringResource(R.string.haptic_style_ripple),
                                        "heartbeat" to stringResource(R.string.haptic_style_heartbeat),
                                        "snap" to stringResource(R.string.haptic_style_snap),
                                        "wave" to stringResource(R.string.haptic_style_wave),
                                        "heavy" to stringResource(R.string.haptic_style_heavy),
                                        "spring" to stringResource(R.string.haptic_style_spring),
                                        "echo" to stringResource(R.string.haptic_style_echo)),
                                textColor = panelColors.text,
                                textColorMode = settings.textColor,
                                menuBackground = menuBackground,
                                menuTextColor = settingsMenuTextColor,
                                fontFamily = fontFamily,
                                onSelected = { selectedStyle -> onHapticEffectsStyleChange(selectedStyle)
                                    UiHapticPlayer.previewStyle(context = context, style = selectedStyle,
                                        intensityPercent = localHapticEffectsIntensity, haptic = UiHaptic.Confirm)
                                })
                            TextButton(onClick = {
                                    UiHapticPlayer.previewStyle(context = context, style = settings.hapticEffectsStyle,
                                        intensityPercent = localHapticEffectsIntensity, haptic = UiHaptic.Confirm)
                                }, colors = ButtonDefaults.textButtonColors(contentColor = panelColors.text)) {
                                Text(text = stringResource(R.string.haptic_effects_preview), fontFamily = fontFamily)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            SettingTitleRow(title = stringResource(R.string.haptic_effects_intensity),
                                value = "${localHapticEffectsIntensity.roundToInt()}%",
                                color = panelColors.text,
                                fontFamily = fontFamily)
                            StyledSettingsSlider(value = localHapticEffectsIntensity,
                                onValueChange = {
                                    localHapticEffectsIntensity = it
                                },
                                onValueChangeFinished = {
                                    onHapticEffectsIntensityChange(localHapticEffectsIntensity)
                                    UiHapticPlayer.previewStyle(context = context, style = settings.hapticEffectsStyle,
                                        intensityPercent = localHapticEffectsIntensity, haptic = UiHaptic.Tick)
                                },
                                valueRange = 0f..100f,
                                steps = 19,
                                activeColor = MaterialTheme.colorScheme.primary,
                                inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                style = settings.sliderStyle,
                                valueLabel = "${localHapticEffectsIntensity.roundToInt()}%")
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    /*
                     * -------------------------------------------------
                     * LANGUAGE
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingDropdown(title = stringResource(R.string.mock_language),
                            selectedLabel = when (settings.language) {
                                    "en" -> "English"
                                    "fr" -> "Français"
                                    "zh-CN" -> "中文（简体）"
                                    else -> "Español"
                                },
                            options = listOf("es" to
                                        "Español", "en" to
                                        "English", "fr" to
                                        "Français", "zh-CN" to
                                        "中文（简体）"),
                            textColor = panelColors.text,
                            textColorMode = settings.textColor,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onSelected = onLanguageChange)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingDropdown(title = stringResource(R.string.mock_columns),
                            selectedLabel = settings.gridColumns.toString(),
                            options = listOf("1" to "1", "2" to "2", "3" to "3"),
                            textColor = panelColors.text,
                            textColorMode = settings.textColor,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onSelected = {
                                    value ->
                                onGridColumnsChange(value.toIntOrNull()?.coerceIn(1, 3)?: 2)
                            })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> SettingDropdown(title = stringResource(R.string.mock_slider_style),
                            selectedLabel = stringResource(SliderStyleOptions.firstOrNull {
                                            it.key == settings.sliderStyle
                                        }?.labelRes?: R.string.mock_slider_minimal),
                            options = SliderStyleOptions.map {
                                        it.key to
                                            stringResource(it.labelRes)
                                    },
                            textColor = panelColors.text,
                            textColorMode = settings.textColor,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onSelected = onSliderStyleChange)
                    }
                    Spacer(modifier = Modifier.height(17.dp))
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors -> Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.mock_dark_mode),
                                    color = panelColors.text,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp)
                                Text(text = stringResource(R.string.mock_dark_mode_description),
                                    modifier = Modifier.padding(top = 2.dp),
                                    color = panelColors.secondaryText,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp)
                            }
                            Switch(checked = settings.darkMode,
                                onCheckedChange = { checked -> UiSoundPlayer.playToggle(context = context, checked = checked)
                                    onDarkModeChange(checked)
                                })
                        }
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    /*
                     * -------------------------------------------------
                     * INFORMACIÓN DEL DESARROLLO
                     * -------------------------------------------------
                     * Solo navega a una pantalla informativa; no cambia ninguna
                     * preferencia ni estado funcional de las notas.
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(0.dp),
                        horizontalOutset = 8.dp) { panelColors ->
                        Row(modifier = Modifier.fillMaxWidth().clickable {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                onOpenDevelopmentInfo()
                            }.padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = panelColors.graphic,
                                modifier = Modifier.size(23.dp))
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {
                                Text(text = stringResource(R.string.development_info_settings_title), color = panelColors.text,
                                    fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Text(text = stringResource(R.string.development_info_settings_description),
                                    modifier = Modifier.padding(top = 2.dp), color = panelColors.secondaryText,
                                    fontFamily = fontFamily, fontSize = 12.sp)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null,
                                tint = panelColors.graphic, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
        }
    }
    }
}
```

**Firma/entrada.** `fun SettingsScreen(settings: AppSettings, onDarkModeChange: (Boolean) -> Unit, onBackgroundColorChange: (String) -> Unit, onBackgroundToneIndexChange: (Int) -> Unit, onBackgroundIntensityChange: (Float) -> Unit, onSettingsPanelToneChange: (Float) -> Unit, onSurfacePanelIntensityChange: (Float) -> Unit, onHeaderIntensityChange: (Float) -> Unit, onTextColorChange: (String) -> Unit, onTextOutlineEnabledChange: (Boolean) -> Unit, onNoteUiTextColorChange: (String) -> Unit, onSliderStyleChange: (String) -> Unit, onFontChange: (String) -> Unit, onFontSizeChange: (Float) -> Unit, onSoundEffectsEnabledChange: (Boolean) -> Unit, onSoundEffectsVolumeChange: (Float) -> Unit, onSoundEffectsThemeChange: (String) -> Unit, onHapticEffectsEnabledChange: (Boolean) -> Unit, onHapticEffectsIntensityChange: (F`

**Parámetros.**
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onDarkModeChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onBackgroundColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onBackgroundToneIndexChange: (Int) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onBackgroundIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSettingsPanelToneChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSurfacePanelIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHeaderIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onTextColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onTextOutlineEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteUiTextColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSliderStyleChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onFontChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onFontSizeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSoundEffectsEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSoundEffectsVolumeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onSoundEffectsThemeChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHapticEffectsEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHapticEffectsIntensityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onHapticEffectsStyleChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onLanguageChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onGridColumnsChange: (Int) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onProfileImageUriChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onProfileImageSizeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onIconStyleChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onIconSizeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onAccentColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteCardCornerRadiusChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteCardElevationChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteCardPaddingChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteCardImageHeightChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteTitleMaxLinesChange: (Int) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteContentMaxLinesChange: (Int) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onNoteLineSpacingChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onShowNoteDateChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onShowCategoryChipChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onShowFavoriteIconChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onFabSizeChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOptionMenuOrderChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOptionMenuHiddenItemsChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOptionMenuShowIconsChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOptionMenuTextColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOptionMenuOpacityChange: (Float) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onPriorityMenuHiddenItemsChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onColorMenuHiddenItemsChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onResetOptionMenu: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onPerformanceModeChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onAnimationsEnabledChange: (Boolean) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onAnimationStyleChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onAnimationEasingChange: (String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.3 `SettingTitle` — fun, líneas 861–868

```kotlin
private fun SettingTitle(text: String, color: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily) {
    Text(text = text,
        color = color,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp)
}
```

**Firma/entrada.** `private fun SettingTitle(text: String, color: Color, fontFamily: androidx.compose.ui.text.font.FontFamily) {`

**Parámetros.**
- `text: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `color: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.4 `SettingTitleRow` — fun, líneas 871–884

```kotlin
private fun SettingTitleRow(title: String, value: String, color: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        SettingTitle(text = title,
            color = color,
            fontFamily = fontFamily)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value,
            color = color,
            fontFamily = fontFamily,
            fontSize = 12.sp)
    }
}
```

**Firma/entrada.** `private fun SettingTitleRow(title: String, value: String, color: Color, fontFamily: androidx.compose.ui.text.font.FontFamily) {`

**Parámetros.**
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `value: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `color: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.5 `TextColorSelector` — fun, líneas 887–927

```kotlin
private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String) {
    val context = LocalContext.current
    /*
     * Resuelve la fuente desde la preferencia real justo en este selector.
     * Así los tres botones (Automático/Negro/Blanco) no dependen de un
     * TextStyle heredado ni de una referencia de fuente calculada fuera.
     */
    val selectorFontFamily = remember(fontKey) {
            appFontFamily(fontKey)
        }
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TextColorButton(modifier = Modifier.weight(1f),
            label = stringResource(R.string.mock_auto),
            sampleColor = MaterialTheme.colorScheme.onSurface,
            selected = selected == "auto",
            fontFamily = selectorFontFamily,
            onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                onSelected("auto")
            })
        TextColorButton(modifier = Modifier.weight(1f),
            label = stringResource(R.string.mock_black),
            sampleColor = Color.Black,
            selected = selected == "black",
            fontFamily = selectorFontFamily,
            onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                onSelected("black")
            })
        TextColorButton(modifier = Modifier.weight(1f),
            label = stringResource(R.string.mock_white),
            sampleColor = Color.White,
            selected = selected == "white",
            fontFamily = selectorFontFamily,
            onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                onSelected("white")
            })
    }
}
```

**Firma/entrada.** `private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String) {`

**Parámetros.**
- `selected: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onSelected: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `fontKey: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.6 `TextColorButton` — fun, líneas 930–977

```kotlin
private fun TextColorButton(modifier: Modifier, label: String, sampleColor: Color, selected: Boolean, fontFamily:
        androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit) {
    val buttonBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val buttonContentColor = resolveUiTextColor(value = "auto", background = buttonBackground)
    val buttonBorderColor = ensureUiContrast(preferred = MaterialTheme.colorScheme.outline, background = buttonBackground,
            minimumContrast = 3f)
    Surface(modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        color = buttonBackground,
        contentColor = buttonContentColor,
        border = BorderStroke(width = if (selected) {
                        1.8.dp
                    } else {
                        1.dp
                    },
                color = if (selected) {
                        buttonContentColor
                    } else {
                        buttonBorderColor
                    })) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(18.dp),
                shape = CircleShape,
                color = sampleColor,
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.45f))) {
            }
            Text(text = label,
                modifier = Modifier.padding(start = 5.dp),
                style = MaterialTheme.typography.labelMedium.copy(fontFamily = fontFamily,
                            fontWeight = if (selected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                            fontSize = 11.sp),
                maxLines = 1,
                softWrap = false)
            if (selected) {
                Icon(imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 2.dp).size(12.dp))
            }
        }
    }
}
```

**Firma/entrada.** `private fun TextColorButton(modifier: Modifier, label: String, sampleColor: Color, selected: Boolean, fontFamily: androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit) {`

**Parámetros.**
- `modifier: Modifier` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `label: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sampleColor: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `selected: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onClick: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

### 4.7 `SettingDropdown` — fun, líneas 980–1083

```kotlin
private fun SettingDropdown(title: String, selectedLabel: String, options: List<Pair<String, String>>, textColor: Color,
    textColorMode: String, menuBackground: Color, menuTextColor: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily, onSelected: (String) -> Unit) {
    val context = LocalContext.current
    var expanded by
        remember {
            mutableStateOf(false)
        }
    val dropdownButtonBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val dropdownButtonTextColor = resolveUiTextColor(value = textColorMode, background = dropdownButtonBackground)
    val dropdownButtonGraphicColor = com.example.mynotes.ui.theme.resolveUiGraphicColor(value = textColorMode,
            background = dropdownButtonBackground)
    val dropdownButtonBorderColor = ensureUiContrast(preferred = MaterialTheme.colorScheme.outline, background = dropdownButtonBackground,
            minimumContrast = 3f)
    // Ajusta el ancho del selector al texto visible en lugar de forzarlo
    // a ocupar toda la mitad derecha de la fila.
    val dropdownWidth = when {
            selectedLabel.length <= 3 -> 112.dp
            selectedLabel.length <= 7 -> 132.dp
            selectedLabel.length <= 11 -> 154.dp
            selectedLabel.length <= 16 -> 188.dp
            else -> 228.dp
        }
    // El popup usa la opción más larga, no solo la opción seleccionada.
    // Así queda lo más estrecho posible sin cortar innecesariamente el texto.
    val longestOptionLength = maxOf(selectedLabel.length, options.maxOfOrNull { it.second.length } ?: 0)
    val compactMenuWidth = when {
            longestOptionLength <= 6 -> 112.dp
            longestOptionLength <= 10 -> 132.dp
            longestOptionLength <= 14 -> 150.dp
            longestOptionLength <= 18 -> 170.dp
            else -> 194.dp
        }
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = title,
            modifier = Modifier.weight(0.36f),
            color = textColor,
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp)
        Box(modifier = Modifier.weight(0.64f),
            contentAlignment = Alignment.CenterEnd) {
            Surface(modifier = Modifier.width(dropdownWidth),
                onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                    expanded = true
                },
                shape = RoundedCornerShape(12.dp),
                color = dropdownButtonBackground,
                contentColor = dropdownButtonTextColor,
                border = BorderStroke(width = 1.dp,
                        color = dropdownButtonBorderColor)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = selectedLabel,
                        modifier = Modifier.weight(1f),
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    Icon(imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = dropdownButtonGraphicColor,
                        modifier = Modifier.size(19.dp))
                }
            }
            AppDropdownMenu(modifier = Modifier.heightIn(max = 176.dp).width(compactMenuWidth),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                containerColor = menuBackground,
                /*
                 * IMPORTANTE:
                 * El Popup no toma el foco de la ventana. En Android 8/9
                 * un popup focusable puede hacer reaparecer la barra de
                 * navegación del sistema mientras se muestra el menú.
                 */
                properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                options.forEach {
                            option ->
                        DropdownMenuItem(modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            text = {
                                Text(text = option.second,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = menuTextColor,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            },
                            onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                                expanded = false
                                onSelected(option.first)
                            })
                    }
            }
        }
    }
}
```

**Firma/entrada.** `private fun SettingDropdown(title: String, selectedLabel: String, options: List<Pair<String, String>>, textColor: Color, textColorMode: String, menuBackground: Color, menuTextColor: Color, fontFamily: androidx.compose.ui.text.font.FontFamily, onSelected: (String) -> Unit) {`

**Parámetros.**
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `selectedLabel: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `options: List<Pair<String, String>>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `textColor: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `textColorMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `menuBackground: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `menuTextColor: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onSelected: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

## 5. Variables y propiedades, una por una

Se detectaron **33 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 86 | `SliderStyleOptions` | `private val SliderStyleOptions: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val SliderStyleOptions = listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule",` |
| 114 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 115 | `fontFamily` | `local/pública por contexto val fontFamily: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val fontFamily = remember(settings.font) {` |
| 118 | `localTone` | `local/pública por contexto var localTone: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localTone by` |
| 122 | `localBackgroundIntensity` | `local/pública por contexto var localBackgroundIntensity: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localBackgroundIntensity by` |
| 126 | `localSettingsPanelTone` | `local/pública por contexto var localSettingsPanelTone: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `var localSettingsPanelTone by` |
| 130 | `localSurfacePanelIntensity` | `local/pública por contexto var localSurfacePanelIntensity: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localSurfacePanelIntensity by` |
| 134 | `localHeaderIntensity` | `local/pública por contexto var localHeaderIntensity: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localHeaderIntensity by` |
| 138 | `localFontSize` | `local/pública por contexto var localFontSize: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localFontSize by` |
| 142 | `localSoundEffectsVolume` | `local/pública por contexto var localSoundEffectsVolume: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localSoundEffectsVolume by` |
| 146 | `localHapticEffectsIntensity` | `local/pública por contexto var localHapticEffectsIntensity: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var localHapticEffectsIntensity by` |
| 185 | `selectedPalette` | `local/pública por contexto val selectedPalette: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val selectedPalette = remember(settings.backgroundColor) {` |
| 188 | `selectedPaletteTone` | `local/pública por contexto val selectedPaletteTone: inferido` | `val` fija la referencia después de inicializarla; su inicialización está acotada a un intervalo explícito con `coerceIn`; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val selectedPaletteTone = selectedPalette.tones[settings.backgroundToneIndex.coerceIn(0, 3)]` |
| 189 | `settingsPanelColor` | `local/pública por contexto val settingsPanelColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settingsPanelColor = lerp(MaterialTheme.colorScheme.surfaceContainerLow, selectedPaletteTone, (localSettingsPanelTone / 100f)` |
| 195 | `settingsTextColor` | `local/pública por contexto val settingsTextColor: inferido` | `val` fija la referencia después de inicializarla; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settingsTextColor = resolveUiTextColor(value = settings.textColor, background = settingsPanelColor)` |
| 196 | `settingsSecondaryTextColor` | `local/pública por contexto val settingsSecondaryTextColor: inferido` | `val` fija la referencia después de inicializarla; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settingsSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = settingsPanelColor)` |
| 197 | `settingsGraphicColor` | `local/pública por contexto val settingsGraphicColor: inferido` | `val` fija la referencia después de inicializarla; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settingsGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = settingsPanelColor)` |
| 198 | `menuBackground` | `local/pública por contexto val menuBackground: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val menuBackground = when (settings.textColor) {` |
| 202 | `settingsMenuTextColor` | `local/pública por contexto val settingsMenuTextColor: inferido` | `val` fija la referencia después de inicializarla; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settingsMenuTextColor = resolveUiTextColor(value = settings.textColor, background = menuBackground)` |
| 888 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 894 | `selectorFontFamily` | `local/pública por contexto val selectorFontFamily: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val selectorFontFamily = remember(fontKey) {` |
| 932 | `buttonBackground` | `local/pública por contexto val buttonBackground: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val buttonBackground = MaterialTheme.colorScheme.surfaceContainerLow` |
| 933 | `buttonContentColor` | `local/pública por contexto val buttonContentColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val buttonContentColor = resolveUiTextColor(value = "auto", background = buttonBackground)` |
| 934 | `buttonBorderColor` | `local/pública por contexto val buttonBorderColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val buttonBorderColor = ensureUiContrast(preferred = MaterialTheme.colorScheme.outline, background = buttonBackground,` |
| 983 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 984 | `expanded` | `local/pública por contexto var expanded: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var expanded by` |
| 988 | `dropdownButtonBackground` | `local/pública por contexto val dropdownButtonBackground: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val dropdownButtonBackground = MaterialTheme.colorScheme.surfaceContainerLow` |
| 989 | `dropdownButtonTextColor` | `local/pública por contexto val dropdownButtonTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val dropdownButtonTextColor = resolveUiTextColor(value = textColorMode, background = dropdownButtonBackground)` |
| 990 | `dropdownButtonGraphicColor` | `local/pública por contexto val dropdownButtonGraphicColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val dropdownButtonGraphicColor = com.example.mynotes.ui.theme.resolveUiGraphicColor(value = textColorMode,` |
| 992 | `dropdownButtonBorderColor` | `local/pública por contexto val dropdownButtonBorderColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val dropdownButtonBorderColor = ensureUiContrast(preferred = MaterialTheme.colorScheme.outline, background = dropdownButtonBackground,` |
| 996 | `dropdownWidth` | `local/pública por contexto val dropdownWidth: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val dropdownWidth = when {` |
| 1005 | `longestOptionLength` | `local/pública por contexto val longestOptionLength: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val longestOptionLength = maxOf(selectedLabel.length, options.maxOfOrNull { it.second.length } ?: 0)` |
| 1006 | `compactMenuWidth` | `local/pública por contexto val compactMenuWidth: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val compactMenuWidth = when {` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **70 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 113–858 | 0 | ámbito/lambda anónima | `onBack: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 115–117 | 1 | `remember` / memoria de composición | `val fontFamily = remember(settings.font) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 119–121 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 123–125 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 127–129 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 131–133 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 135–137 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 139–141 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 143–145 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 147–149 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 150–152 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.backgroundToneIndex) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 153–155 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.backgroundIntensity) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 156–158 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.settingsPanelTone) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 159–161 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.surfacePanelIntensity) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 162–164 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.headerIntensity) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 165–167 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.fontSize) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 168–170 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.soundEffectsVolume) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 171–173 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.hapticEffectsIntensity) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 185–187 | 1 | `remember` / memoria de composición | `val selectedPalette = remember(settings.backgroundColor) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 198–201 | 1 | selección `when` | `val menuBackground = when (settings.textColor) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 204–857 | 1 | ámbito/lambda anónima | `animationSpeed = settings.animationSpeed) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 206–224 | 2 | ámbito/lambda anónima | `topBar = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 207–212 | 3 | ámbito/lambda anónima | `TopAppBar(title = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 213–223 | 3 | ámbito/lambda anónima | `navigationIcon = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 224–856 | 2 | ámbito/lambda anónima | `}) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 226–855 | 3 | bloque UI Compose | `Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 233–852 | 6 | bloque UI Compose | `Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 447–458 | 9 | bloque UI Compose | `Column(modifier = Modifier.weight(1f)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 482–494 | 8 | selección `when` | `selectedLabel = when (settings.font) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 560–638 | 8 | condición `if` | `if (settings.soundEffectsEnabled) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 563–584 | 9 | selección `when` | `selectedLabel = when (settings.soundEffectsTheme) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 670–742 | 8 | condición `if` | `if (settings.hapticEffectsEnabled) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 673–690 | 9 | selección `when` | `selectedLabel = when (settings.hapticEffectsStyle) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 752–757 | 8 | selección `when` | `selectedLabel = when (settings.language) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 804–823 | 7 | bloque UI Compose | `horizontalOutset = 8.dp) { panelColors -> Row(modifier = Modifier.fillMaxWidth(),` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 806–817 | 9 | bloque UI Compose | `Column(modifier = Modifier.weight(1f)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 834–837 | 8 | bloque UI Compose | `Row(modifier = Modifier.fillMaxWidth().clickable {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 841–847 | 9 | bloque UI Compose | `Column(modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 862–868 | 0 | ámbito/lambda anónima | `androidx.compose.ui.text.font.FontFamily) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 872–884 | 0 | ámbito/lambda anónima | `androidx.compose.ui.text.font.FontFamily) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 874–883 | 1 | ámbito/lambda anónima | `verticalAlignment = Alignment.CenterVertically) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 887–927 | 0 | ámbito/lambda anónima | `private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 894–896 | 1 | `remember` / memoria de composición | `val selectorFontFamily = remember(fontKey) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 898–926 | 1 | ámbito/lambda anónima | `horizontalArrangement = Arrangement.spacedBy(10.dp)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 904–907 | 2 | ámbito/lambda anónima | `onClick = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 913–916 | 2 | ámbito/lambda anónima | `onClick = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 922–925 | 2 | ámbito/lambda anónima | `onClick = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 931–977 | 0 | ámbito/lambda anónima | `androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 941–943 | 1 | condición `if` | `border = BorderStroke(width = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 943–945 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 946–948 | 1 | condición `if` | `color = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 948–950 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 950–976 | 1 | ámbito/lambda anónima | `})) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 953–975 | 2 | ámbito/lambda anónima | `verticalAlignment = Alignment.CenterVertically) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 957–958 | 3 | ámbito/lambda anónima | `border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.45f))) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 962–964 | 3 | condición `if` | `fontWeight = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 964–966 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 970–974 | 3 | condición `if` | `if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 982–1083 | 0 | ámbito/lambda anónima | `androidx.compose.ui.text.font.FontFamily, onSelected: (String) -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 985–987 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 996–1002 | 1 | ámbito/lambda anónima | `val dropdownWidth = when {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1005–1005 | 1 | ámbito/lambda anónima | `val longestOptionLength = maxOf(selectedLabel.length, options.maxOfOrNull { it.second.length } ?: 0)` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1006–1012 | 1 | ámbito/lambda anónima | `val compactMenuWidth = when {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1014–1082 | 1 | ámbito/lambda anónima | `verticalAlignment = Alignment.CenterVertically) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1022–1081 | 2 | ámbito/lambda anónima | `contentAlignment = Alignment.CenterEnd) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1024–1027 | 3 | ámbito/lambda anónima | `onClick = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1032–1046 | 3 | ámbito/lambda anónima | `color = dropdownButtonBorderColor)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1049–1051 | 3 | ámbito/lambda anónima | `onDismissRequest = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1059–1080 | 3 | ámbito/lambda anónima | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 1060–1079 | 4 | iteración funcional | `options.forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |

## 7. Side effects, rendimiento y lifecycle

- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Corrutinas:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Navegación / Intents:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

El desplazamiento de “Información del desarrollo” al final solo cambia el orden de composición. El padding horizontal adicional para títulos/descripciones aumenta la separación del borde interno sin mover innecesariamente controles alineados a la derecha.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
