# SettingsScreen.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `3098c2e86dc04905b3e2470c439faccd4686a89317060fc9695474973de548c1`  
**Líneas del código real:** 1051

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Pantalla Compose completa de Configuración. Ensambla las secciones de apariencia, paletas, fuentes, sonido, vibración, idioma, tarjetas, rendimiento, animaciones, menús y respaldo.

**Arquitectura.** Recibe AppSettings y callbacks de cambio. La persistencia permanece fuera de la pantalla; su responsabilidad es representar y organizar controles.

**Flujo general.** Flujo típico: recibe un `AppSettings` actual -> cada control refleja su valor -> al cambiar emite el callback correspondiente -> SettingsViewModel/Repository persisten -> el nuevo StateFlow vuelve a la pantalla y actualiza el tema o control afectado.

## 2. Package e imports

El `package` es `com.example.mynotes.ui`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **76 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.lerp`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.BackupRestoreSection`, `com.example.mynotes.ui.components.ExtremeCustomizationSection`, `com.example.mynotes.ui.components.OptionsMenuCustomizationSection`, `com.example.mynotes.ui.components.PaletteSelector`, `com.example.mynotes.ui.components.StyledSettingsSlider`, `com.example.mynotes.ui.components.SettingsSectionPanel`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.sound.UiHaptic`, `com.example.mynotes.ui.sound.UiHapticPlayer`, `com.example.mynotes.ui.theme.PaletteCatalog`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`.

**Kotlin/corrutinas/Java:** `kotlin.math.roundToInt`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (2 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (3 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (4 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Límite visual (3 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Sin foco de popup (1 aparición/apariciones):** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android.

## 4. Bloques de código, uno por uno

### 4.1 `SettingsScreen` — fun, líneas 81–90

```kotlin
private data class SliderStyleOption(val key: String, val labelRes: Int)

private val SliderStyleOptions = listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule",
            R.string.mock_slider_capsule), SliderStyleOption("glow", R.string.mock_slider_glow), SliderStyleOption("glass",
            R.string.mock_slider_glass), SliderStyleOption("segmented", R.string.mock_slider_segmented), SliderStyleOption("dots",
            R.string.mock_slider_dots), SliderStyleOption("gradient", R.string.mock_slider_gradient), SliderStyleOption("neumorphic",
            R.string.mock_slider_neumorphic), SliderStyleOption("line_pill", R.string.mock_slider_line_pill), SliderStyleOption("floating",
            R.string.mock_slider_floating))
@OptIn(ExperimentalMaterial3Api::class)
@Composable
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `val key: String` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val labelRes: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 83 | `val SliderStyleOptions` | `inferido` | `listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `SliderStyleOption`, `OptIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `SettingsScreen` — fun, líneas 91–826

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
    onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit, onBack: () -> Unit) {
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 10.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 10.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(vertical = 14.dp),
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
                }
            }
        }
        }
    }
    }
}
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 110 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 111 | `val fontFamily` | `inferido` | `remember(settings.font) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 181 | `val selectedPalette` | `inferido` | `remember(settings.backgroundColor) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 184 | `val selectedPaletteTone` | `inferido` | `selectedPalette.tones[settings.backgroundToneIndex.coerceIn(0, 3)]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 185 | `val settingsPanelColor` | `inferido` | `lerp(MaterialTheme.colorScheme.surfaceContainerLow, selectedPaletteTone, (localSettingsPanelTone / 1…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 191 | `val settingsTextColor` | `inferido` | `resolveUiTextColor(value = settings.textColor, background = settingsPanelColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 192 | `val settingsSecondaryTextColor` | `inferido` | `resolveSecondaryUiTextColor(value = settings.textColor, background = settingsPanelColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 193 | `val settingsGraphicColor` | `inferido` | `resolveUiGraphicColor(value = settings.textColor, background = settingsPanelColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `val menuBackground` | `inferido` | `when (settings.textColor) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `val settingsMenuTextColor` | `inferido` | `resolveUiTextColor(value = settings.textColor, background = menuBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 91 | `fun SettingsScreen(settings: AppSettings, onDarkModeChange: (Boolean) -> Unit, onBackgroundColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 92 | `onBackgroundToneIndexChange: (Int) -> Unit, onBackgroundIntensityChange: (Float) -> Unit, onSettingsPanelToneChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 93 | `onSurfacePanelIntensityChange: (Float) -> Unit, onHeaderIntensityChange: (Float) -> Unit, onTextColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 94 | `onTextOutlineEnabledChange: (Boolean) -> Unit, onNoteUiTextColorChange: (String) -> Unit, onSliderStyleChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 95 | `onFontChange: (String) -> Unit, onFontSizeChange: (Float) -> Unit, onSoundEffectsEnabledChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 96 | `onSoundEffectsVolumeChange: (Float) -> Unit, onSoundEffectsThemeChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 97 | `onHapticEffectsEnabledChange: (Boolean) -> Unit, onHapticEffectsIntensityChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 98 | `onHapticEffectsStyleChange: (String) -> Unit, onLanguageChange: (String) -> Unit, onGridColumnsChange: (Int) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 99 | `onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit, onIconStyleChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 100 | `onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit, onNoteCardCornerRadiusChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 101 | `onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit, onNoteCardImageHeightChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 102 | `onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit, onNoteLineSpacingChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 103 | `onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit, onShowFavoriteIconChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 104 | `onFabSizeChange: (Float) -> Unit, onOptionMenuOrderChange: (String) -> Unit, onOptionMenuHiddenItemsChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `onOptionMenuShowIconsChange: (Boolean) -> Unit, onOptionMenuTextColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `onOptionMenuOpacityChange: (Float) -> Unit, onPriorityMenuHiddenItemsChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `onColorMenuHiddenItemsChange: (String) -> Unit, onResetOptionMenu: () -> Unit, onPerformanceModeChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit, onBack: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 146 | `LaunchedEffect(settings.backgroundToneIndex) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 149 | `LaunchedEffect(settings.backgroundIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 152 | `LaunchedEffect(settings.settingsPanelTone) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 155 | `LaunchedEffect(settings.surfacePanelIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 158 | `LaunchedEffect(settings.headerIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 161 | `LaunchedEffect(settings.fontSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 164 | `LaunchedEffect(settings.soundEffectsVolume) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 167 | `LaunchedEffect(settings.hapticEffectsIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 195 | `"white" -> MaterialTheme.colorScheme.inverseSurface` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 196 | `else -> MaterialTheme.colorScheme.surfaceContainerHigh` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 221 | `paddingValues ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 282 | `paletteKey ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 291 | `paletteKey, toneIndex ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 479 | `"google_sans" -> "Google Sans (Auto)"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 480 | `"google_sans_regular" -> "Google Sans Regular"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 481 | `"google_sans_medium" -> "Google Sans Medium"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 482 | `"google_sans_bold" -> "Google Sans Bold"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 483 | `"google_sans_italic" -> "Google Sans Italic"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 484 | `"google_sans_medium_italic" -> "Google Sans Medium Italic"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 485 | `"google_sans_bold_italic" -> "Google Sans Bold Italic"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 486 | `"google_sans_flex" -> "Google Sans Flex"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 487 | `"serif" -> stringResource(R.string.mock_font_serif)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 488 | `"monospace" -> stringResource(R.string.mock_font_monospace)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 489 | `else -> stringResource(R.string.mock_font_default)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 556 | `if (settings.soundEffectsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 560 | `"soft" -> stringResource(R.string.sound_theme_soft)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 561 | `"digital" -> stringResource(R.string.sound_theme_digital)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 562 | `"glass" -> stringResource(R.string.sound_theme_glass)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 563 | `"retro" -> stringResource(R.string.sound_theme_retro)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 564 | `"pop" -> stringResource(R.string.sound_theme_pop)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 565 | `"mechanical" -> stringResource(R.string.sound_theme_mechanical)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 566 | `"bubble" -> stringResource(R.string.sound_theme_bubble)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 567 | `"arcade" -> stringResource(R.string.sound_theme_arcade)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 568 | `"wood" -> stringResource(R.string.sound_theme_wood)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 569 | `"synth" -> stringResource(R.string.sound_theme_synth)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 570 | `"minimal" -> stringResource(R.string.sound_theme_minimal)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 571 | `"camera" -> stringResource(R.string.sound_theme_camera)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 572 | `"typewriter" -> stringResource(R.string.sound_theme_typewriter)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 573 | `"metal" -> stringResource(R.string.sound_theme_metal)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 574 | `"pixel" -> stringResource(R.string.sound_theme_pixel)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 575 | `"space" -> stringResource(R.string.sound_theme_space)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 576 | `"chime" -> stringResource(R.string.sound_theme_chime)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 577 | `"paper" -> stringResource(R.string.sound_theme_paper)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 578 | `"neon" -> stringResource(R.string.sound_theme_neon)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 579 | `else -> stringResource(R.string.sound_theme_classic)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 666 | `if (settings.hapticEffectsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 670 | `"crisp" -> stringResource(R.string.haptic_style_crisp)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 671 | `"deep" -> stringResource(R.string.haptic_style_deep)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 672 | `"double" -> stringResource(R.string.haptic_style_double)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 673 | `"pulse" -> stringResource(R.string.haptic_style_pulse)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 674 | `"stepped" -> stringResource(R.string.haptic_style_stepped)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 675 | `"mechanical" -> stringResource(R.string.haptic_style_mechanical)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 676 | `"minimal" -> stringResource(R.string.haptic_style_minimal)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 677 | `"triple" -> stringResource(R.string.haptic_style_triple)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 678 | `"ripple" -> stringResource(R.string.haptic_style_ripple)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 679 | `"heartbeat" -> stringResource(R.string.haptic_style_heartbeat)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 680 | `"snap" -> stringResource(R.string.haptic_style_snap)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 681 | `"wave" -> stringResource(R.string.haptic_style_wave)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 682 | `"heavy" -> stringResource(R.string.haptic_style_heavy)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 683 | `"spring" -> stringResource(R.string.haptic_style_spring)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 684 | `"echo" -> stringResource(R.string.haptic_style_echo)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

> Se detectaron 86 estructuras; la tabla limita la vista a las primeras 80 para no duplicar de forma inútil el código completo. El fragmento de código anterior conserva todas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 4.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `appFontFamily`, `mutableFloatStateOf`, `settings.backgroundToneIndex.toFloat`, `LaunchedEffect`, `PaletteCatalog.find`, `settings.backgroundToneIndex.coerceIn`, `lerp`, `coerceIn`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `AnimatedScreenEntry`, `Scaffold`, `TopAppBar`, `Text`, `stringResource`, `TextButton`, `UiSoundPlayer.playAction`, `onBack`, `ButtonDefaults.textButtonColors`, `Icon`, `Modifier.size`, `Box`, `Modifier.fillMaxSize`, `padding`, `Column`, `Modifier.widthIn`, `fillMaxWidth`, `verticalScroll`, `rememberScrollState`, `Surface`, `Modifier.fillMaxWidth`, `RoundedCornerShape`, `Modifier.padding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.3 `SettingTitle` — fun, líneas 829–836

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

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `text: String` — `text` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — `fontFamily` recibe un valor de tipo `androidx.compose.ui.text.font.FontFamily`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `SettingTitleRow` — fun, líneas 839–852

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

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — `fontFamily` recibe un valor de tipo `androidx.compose.ui.text.font.FontFamily`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Row`, `Modifier.fillMaxWidth`, `SettingTitle`, `Spacer`, `Modifier.weight`, `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `TextColorSelector` — fun, líneas 855–895

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `selected: String` — `selected` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `onSelected: (String) -> Unit` — `onSelected` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `fontKey: String` — `fontKey` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 856 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 862 | `val selectorFontFamily` | `inferido` | `remember(fontKey) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 855 | `private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `appFontFamily`, `Row`, `Modifier.fillMaxWidth`, `Arrangement.spacedBy`, `TextColorButton`, `Modifier.weight`, `stringResource`, `UiSoundPlayer.playAction`, `onSelected`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.6 `TextColorButton` — fun, líneas 898–945

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modifier: Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable.
- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `sampleColor: Color` — `sampleColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `selected: Boolean` — `selected` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — `fontFamily` recibe un valor de tipo `androidx.compose.ui.text.font.FontFamily`. El contrato no marca este parámetro como anulable.
- `onClick: () -> Unit` — `onClick` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 900 | `val buttonBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerLow` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 901 | `val buttonContentColor` | `inferido` | `resolveUiTextColor(value = "auto", background = buttonBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 902 | `val buttonBorderColor` | `inferido` | `ensureUiContrast(preferred = MaterialTheme.colorScheme.outline, background = buttonBackground,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 899 | `androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 938 | `if (selected) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `resolveUiTextColor`, `ensureUiContrast`, `Surface`, `RoundedCornerShape`, `BorderStroke`, `Row`, `Modifier.fillMaxWidth`, `padding`, `Modifier.size`, `Color.Gray.copy`, `Text`, `Modifier.padding`, `MaterialTheme.typography.labelMedium.copy`, `Icon`, `size`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `SettingDropdown` — fun, líneas 948–1051

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

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `selectedLabel: String` — `selectedLabel` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `options: List<Pair<String, String>>` — `options` recibe un valor de tipo `List<Pair<String, String>>`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `textColorMode: String` — `textColorMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `menuBackground: Color` — `menuBackground` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `menuTextColor: Color` — `menuTextColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — `fontFamily` recibe un valor de tipo `androidx.compose.ui.text.font.FontFamily`. El contrato no marca este parámetro como anulable.
- `onSelected: (String) -> Unit` — `onSelected` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 951 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 956 | `val dropdownButtonBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerLow` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 957 | `val dropdownButtonTextColor` | `inferido` | `resolveUiTextColor(value = textColorMode, background = dropdownButtonBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 958 | `val dropdownButtonGraphicColor` | `inferido` | `com.example.mynotes.ui.theme.resolveUiGraphicColor(value = textColorMode,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 960 | `val dropdownButtonBorderColor` | `inferido` | `ensureUiContrast(preferred = MaterialTheme.colorScheme.outline, background = dropdownButtonBackgroun…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 964 | `val dropdownWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 973 | `val longestOptionLength` | `inferido` | `maxOf(selectedLabel.length, options.maxOfOrNull { it.second.length } ?: 0)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 974 | `val compactMenuWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 950 | `androidx.compose.ui.text.font.FontFamily, onSelected: (String) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 965 | `selectedLabel.length <= 3 -> 112.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 966 | `selectedLabel.length <= 7 -> 132.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 967 | `selectedLabel.length <= 11 -> 154.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 968 | `selectedLabel.length <= 16 -> 188.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 969 | `else -> 228.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 975 | `longestOptionLength <= 6 -> 112.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 976 | `longestOptionLength <= 10 -> 132.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 977 | `longestOptionLength <= 14 -> 150.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 978 | `longestOptionLength <= 18 -> 170.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 979 | `else -> 194.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1029 | `option ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 2.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `mutableStateOf`, `resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `ensureUiContrast`, `maxOf`, `Row`, `Modifier.fillMaxWidth`, `Text`, `Modifier.weight`, `Box`, `Surface`, `Modifier.width`, `UiSoundPlayer.playAction`, `RoundedCornerShape`, `BorderStroke`, `padding`, `Icon`, `Modifier.size`, `AppDropdownMenu`, `Modifier.heightIn`, `width`, `PopupProperties`, `DropdownMenuItem`, `Modifier.height`, `PaddingValues`, `onSelected`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 83 | `SliderStyleOptions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 110 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 111 | `fontFamily` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 181 | `selectedPalette` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 184 | `selectedPaletteTone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 185 | `settingsPanelColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 191 | `settingsTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 192 | `settingsSecondaryTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 193 | `settingsGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `menuBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `settingsMenuTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 856 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 862 | `selectorFontFamily` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 900 | `buttonBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 901 | `buttonContentColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 902 | `buttonBorderColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 951 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 956 | `dropdownButtonBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 957 | `dropdownButtonTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 958 | `dropdownButtonGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 960 | `dropdownButtonBorderColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 964 | `dropdownWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 973 | `longestOptionLength` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 974 | `compactMenuWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 109–826 | 0 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit, onBack: () -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 111–113 | 1 | `val fontFamily = remember(settings.font)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 115–117 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 119–121 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 123–125 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 127–129 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 131–133 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 135–137 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 139–141 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 143–145 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 146–148 | 1 | `LaunchedEffect(settings.backgroundToneIndex)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 149–151 | 1 | `LaunchedEffect(settings.backgroundIntensity)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 152–154 | 1 | `LaunchedEffect(settings.settingsPanelTone)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 155–157 | 1 | `LaunchedEffect(settings.surfacePanelIntensity)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 158–160 | 1 | `LaunchedEffect(settings.headerIntensity)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 161–163 | 1 | `LaunchedEffect(settings.fontSize)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 164–166 | 1 | `LaunchedEffect(settings.soundEffectsVolume)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 167–169 | 1 | `LaunchedEffect(settings.hapticEffectsIntensity)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 181–183 | 1 | `val selectedPalette = remember(settings.backgroundColor)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 194–197 | 1 | `val menuBackground = when (settings.textColor)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 200–825 | 1 | `animationSpeed = settings.animationSpeed)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 202–220 | 2 | `topBar =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 203–208 | 3 | `TopAppBar(title =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 209–219 | 3 | `navigationIcon =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 210–213 | 4 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 214–218 | 4 | `colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface))` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 220–824 | 2 | `})` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 222–823 | 3 | `Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 224–822 | 4 | `end = 14.dp, bottom = 32.dp))` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 228–821 | 5 | `tonalElevation = 1.dp)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 229–820 | 6 | `Column(modifier = Modifier.fillMaxWidth().padding(16.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 271–323 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 281–284 | 8 | `onPaletteSelected =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 290–295 | 8 | `onToneSelected =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 311–313 | 8 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 314–316 | 8 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 334–350 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 339–341 | 8 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 342–344 | 8 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 353–370 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 359–361 | 8 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 362–364 | 8 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 373–389 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 378–380 | 8 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 381–383 | 8 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 401–423 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 407–409 | 8 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 410–412 | 8 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 431–437 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 440–460 | 7 | `horizontalOutset = 0.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 442–459 | 8 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 443–454 | 9 | `Column(modifier = Modifier.weight(1f))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 456–458 | 9 | `onCheckedChange =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 463–469 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 477–526 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 478–490 | 8 | `selectedLabel = when (settings.font)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 515–517 | 8 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 518–520 | 8 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 534–635 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 546–555 | 8 | `horizontalArrangement = Arrangement.SpaceBetween)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 552–554 | 9 | `onCheckedChange =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 556–634 | 8 | `if (settings.soundEffectsEnabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 559–580 | 9 | `selectedLabel = when (settings.soundEffectsTheme)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 606–609 | 9 | `onSelected =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 610–613 | 9 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 613–615 | 9 | `}, colors = ButtonDefaults.textButtonColors(contentColor = panelColors.text))` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 622–624 | 9 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 625–627 | 9 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 643–739 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 655–665 | 8 | `horizontalArrangement = Arrangement.SpaceBetween)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 661–664 | 9 | `onCheckedChange =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 666–738 | 8 | `if (settings.hapticEffectsEnabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 669–686 | 9 | `selectedLabel = when (settings.hapticEffectsStyle)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 708–711 | 9 | `onSelected =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 712–715 | 9 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 715–717 | 9 | `}, colors = ButtonDefaults.textButtonColors(contentColor = panelColors.text))` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 724–726 | 9 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 727–731 | 9 | `onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 747–765 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 748–753 | 8 | `selectedLabel = when (settings.language)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 768–780 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 776–779 | 8 | `onSelected =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 783–797 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 784–786 | 8 | `selectedLabel = stringResource(SliderStyleOptions.firstOrNull` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 787–790 | 8 | `options = SliderStyleOptions.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 800–819 | 7 | `horizontalOutset = 8.dp)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 801–818 | 8 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 802–813 | 9 | `Column(modifier = Modifier.weight(1f))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 815–817 | 9 | `onCheckedChange =` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 830–836 | 0 | `androidx.compose.ui.text.font.FontFamily)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 840–852 | 0 | `androidx.compose.ui.text.font.FontFamily)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 842–851 | 1 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 855–895 | 0 | `private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 862–864 | 1 | `val selectorFontFamily = remember(fontKey)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 866–894 | 1 | `horizontalArrangement = Arrangement.spacedBy(10.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 872–875 | 2 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 881–884 | 2 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 890–893 | 2 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 899–945 | 0 | `androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit)` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 909–911 | 1 | `border = BorderStroke(width = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 911–913 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 914–916 | 1 | `color = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 916–918 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 918–944 | 1 | `}))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 921–943 | 2 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 925–926 | 3 | `border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.45f)))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 930–932 | 3 | `fontWeight = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 932–934 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 938–942 | 3 | `if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 950–1051 | 0 | `androidx.compose.ui.text.font.FontFamily, onSelected: (String) -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 953–955 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 964–970 | 1 | `val dropdownWidth = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 973–973 | 1 | `val longestOptionLength = maxOf(selectedLabel.length, options.maxOfOrNull` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 974–980 | 1 | `val compactMenuWidth = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 982–1050 | 1 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 990–1049 | 2 | `contentAlignment = Alignment.CenterEnd)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 992–995 | 3 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 1000–1014 | 3 | `color = dropdownButtonBorderColor))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1002–1013 | 4 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1017–1019 | 3 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 1027–1048 | 3 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 1028–1047 | 4 | `options.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1032–1041 | 5 | `text =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1042–1046 | 5 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
