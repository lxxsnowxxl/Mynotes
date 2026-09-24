package com.example.mynotes.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.R
import com.example.mynotes.update.GitHubUpdateManager
import com.example.mynotes.reminders.ReminderFeedbackPreferences
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.DeveloperFeatures
import com.example.mynotes.ui.components.BackupRestoreSection
import com.example.mynotes.ui.components.ExtremeCustomizationSection
import com.example.mynotes.ui.components.OptionsMenuCustomizationSection
import com.example.mynotes.ui.components.PaletteSelector
import com.example.mynotes.ui.components.StyledSettingsSlider
import com.example.mynotes.ui.components.SettingsSectionPanel
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.ProfileImageEditorDialog
import com.example.mynotes.ui.components.clearManagedProfileImages
import com.example.mynotes.ui.components.managedProfileSourceUri
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.sound.UiHaptic
import com.example.mynotes.ui.sound.UiHapticPlayer
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.adaptiveUiButtonContainer
import com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.ensureUiContrast
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil3.compose.AsyncImage

@Immutable
private data class SliderStyleOption(val key: String, val labelRes: Int)

private val PreviewButtonVerticalGap = 4.dp
private val PreviewToSliderGap = 10.dp
private val SoundHapticPanelBottomPadding = 18.dp

private val SliderStyleOptions = listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule",
            R.string.mock_slider_capsule), SliderStyleOption("glow", R.string.mock_slider_glow), SliderStyleOption("glass",
            R.string.mock_slider_glass), SliderStyleOption("segmented", R.string.mock_slider_segmented), SliderStyleOption("dots",
            R.string.mock_slider_dots), SliderStyleOption("gradient", R.string.mock_slider_gradient), SliderStyleOption("neumorphic",
            R.string.mock_slider_neumorphic), SliderStyleOption("line_pill", R.string.mock_slider_line_pill), SliderStyleOption("floating",
            R.string.mock_slider_floating))
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settings: AppSettings, onConfigurationModeChange: (String) -> Unit, onDarkModeChange: (Boolean) -> Unit, onBackgroundColorChange: (String) -> Unit,
    onBackgroundToneIndexChange: (Int) -> Unit, onBackgroundIntensityChange: (Float) -> Unit, onSettingsPanelToneChange: (Float) -> Unit,
    onSurfacePanelIntensityChange: (Float) -> Unit, onHeaderIntensityChange: (Float) -> Unit, onTextColorChange: (String) -> Unit,
    onTextOutlineEnabledChange: (Boolean) -> Unit, onSliderStyleChange: (String) -> Unit,
    onFontChange: (String) -> Unit, onFontSizeChange: (Float) -> Unit, onSoundEffectsEnabledChange: (Boolean) -> Unit,
    onSoundEffectsVolumeChange: (Float) -> Unit, onSoundEffectsThemeChange: (String) -> Unit,
    onReminderSoundEnabledChange: (Boolean) -> Unit, onReminderSoundVolumeChange: (Float) -> Unit,
    onReminderRingtoneChange: (String) -> Unit,
    onHapticEffectsEnabledChange: (Boolean) -> Unit, onHapticEffectsIntensityChange: (Float) -> Unit,
    onHapticEffectsStyleChange: (String) -> Unit, onLanguageChange: (String) -> Unit, onGridColumnsChange: (Int) -> Unit,
    onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit, onIconStyleChange: (String) -> Unit,
    onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit, onNoteCardCornerRadiusChange: (Float) -> Unit,
    onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit, onNoteCardImageHeightChange: (Float) -> Unit,
    onNoteCardOutlineWidthChange: (Float) -> Unit,
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
    val developerGoogleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
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
    var localProfileSize by
        remember {
            mutableFloatStateOf(settings.profileImageSize)
        }
    var localSoundEffectsVolume by
        remember {
            mutableFloatStateOf(settings.soundEffectsVolume)
        }
    var localReminderSoundVolume by
        remember {
            mutableFloatStateOf(settings.reminderSoundVolume)
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
    LaunchedEffect(settings.profileImageSize) {
        localProfileSize = settings.profileImageSize
    }
    LaunchedEffect(settings.soundEffectsVolume) {
        localSoundEffectsVolume = settings.soundEffectsVolume
    }
    LaunchedEffect(settings.reminderSoundVolume) {
        localReminderSoundVolume = settings.reminderSoundVolume
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
    val settingsScreenTextColor = resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background)
    val settingsScreenSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor,
        background = MaterialTheme.colorScheme.background)
    val settingsScreenGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = MaterialTheme.colorScheme.background)
    val settingsTopBarTextColor = resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.surface)
    val menuBackground = when (settings.textColor) {
            "white" -> MaterialTheme.colorScheme.inverseSurface
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val settingsMenuTextColor = resolveUiTextColor(value = settings.textColor, background = menuBackground)
    val settingsScrollState = rememberScrollState()
    val isAdvancedMode = settings.configurationMode == "advanced"

    /*
     * ==========================================================
     * ACTUALIZACIONES DESDE GITHUB RELEASES
     * ==========================================================
     *
     * El estado del actualizador vive sólo mientras Settings está compuesto.
     * No se guarda en DataStore porque comprobar/descargar una actualización
     * es una operación temporal, no una preferencia del usuario.
     */
    val updateScope = rememberCoroutineScope()
    val installedVersion = remember(context) { GitHubUpdateManager.currentVersionName(context) }
    var updateResult by remember { mutableStateOf<GitHubUpdateManager.CheckResult?>(null) }
    var checkingForUpdate by remember { mutableStateOf(false) }
    var downloadingUpdate by remember { mutableStateOf(false) }
    var updateActionMessage by remember { mutableStateOf<String?>(null) }
    var pendingReleasePermission by remember { mutableStateOf<GitHubUpdateManager.Release?>(null) }

    val downloadAndOpenInstaller: (GitHubUpdateManager.Release) -> Unit = { release ->
        updateScope.launch {
            downloadingUpdate = true
            updateActionMessage = null
            runCatching { GitHubUpdateManager.downloadApk(context, release) }
                .onSuccess { apk ->
                    if (!GitHubUpdateManager.launchSystemInstaller(context, apk)) {
                        updateActionMessage = context.getString(R.string.update_install_error)
                    }
                }
                .onFailure { error ->
                    updateActionMessage = context.getString(
                        R.string.update_download_error,
                        error.message ?: error.javaClass.simpleName
                    )
                }
            downloadingUpdate = false
        }
    }

    val unknownSourcesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val pendingRelease = pendingReleasePermission
        pendingReleasePermission = null
        if (pendingRelease != null) {
            if (GitHubUpdateManager.canInstallDownloadedPackages(context)) {
                downloadAndOpenInstaller(pendingRelease)
            } else {
                updateActionMessage = context.getString(R.string.update_permission_required)
            }
        }
    }

    val requestDownloadAndInstall: (GitHubUpdateManager.Release) -> Unit = { release ->
        if (GitHubUpdateManager.canInstallDownloadedPackages(context)) {
            downloadAndOpenInstaller(release)
        } else {
            pendingReleasePermission = release
            unknownSourcesLauncher.launch(GitHubUpdateManager.unknownSourcesSettingsIntent(context))
        }
    }
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(title = {
                    Text(text = stringResource(R.string.mock_settings),
                        color = settingsTopBarTextColor,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp)
                },
                navigationIcon = {
                    TextButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                            onBack()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = settingsTopBarTextColor)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back),
                            modifier = Modifier.size(25.dp))
                    }
                })
        }) {
            paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.widthIn(max = 840.dp).fillMaxWidth().verticalScroll(settingsScrollState).padding(start = 14.dp,
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
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileAndModePanel(
                        settings = settings,
                        fontFamily = fontFamily,
                        isAdvancedMode = isAdvancedMode,
                        profileSize = localProfileSize,
                        onProfileSizeValueChange = { localProfileSize = it },
                        onConfigurationModeChange = onConfigurationModeChange,
                        onProfileImageUriChange = onProfileImageUriChange,
                        onProfileImageSizeChange = { onProfileImageSizeChange(localProfileSize) }
                    )
                    if (isAdvancedMode) {
                        Spacer(modifier = Modifier.height(18.dp))
                        ExtremeCustomizationSection(settings = settings, fontFamily = fontFamily, textColor = settingsScreenTextColor,
                            secondaryTextColor = settingsScreenSecondaryTextColor, graphicColor = settingsScreenGraphicColor,
                            profileOnly = false, showProfileSection = false,
                            onProfileImageUriChange = onProfileImageUriChange, onProfileImageSizeChange = onProfileImageSizeChange,
                            onIconStyleChange = onIconStyleChange, onIconSizeChange = onIconSizeChange,
                            onAccentColorChange = onAccentColorChange, onNoteCardCornerRadiusChange = onNoteCardCornerRadiusChange,
                            onNoteCardElevationChange = onNoteCardElevationChange, onNoteCardPaddingChange = onNoteCardPaddingChange,
                            onNoteCardImageHeightChange = onNoteCardImageHeightChange,
                            onNoteCardOutlineWidthChange = onNoteCardOutlineWidthChange,
                            onNoteTitleMaxLinesChange = onNoteTitleMaxLinesChange,
                            onNoteContentMaxLinesChange = onNoteContentMaxLinesChange, onNoteLineSpacingChange = onNoteLineSpacingChange,
                            onShowNoteDateChange = onShowNoteDateChange, onShowCategoryChipChange = onShowCategoryChipChange,
                            onShowFavoriteIconChange = onShowFavoriteIconChange, onFabSizeChange = onFabSizeChange,
                            onPerformanceModeChange = onPerformanceModeChange, onAnimationsEnabledChange = onAnimationsEnabledChange,
                            onAnimationStyleChange = onAnimationStyleChange, onAnimationEasingChange = onAnimationEasingChange,
                            onAnimationSpeedChange = onAnimationSpeedChange, onAnimationIntensityChange = onAnimationIntensityChange)
                    }
                    if (isAdvancedMode) {
                        Spacer(modifier = Modifier.height(18.dp))
                        OptionsMenuCustomizationSection(settings = settings, fontFamily = fontFamily, textColor = settingsScreenTextColor,
                            secondaryTextColor = settingsScreenSecondaryTextColor, graphicColor = settingsScreenGraphicColor,
                            onOrderChange = onOptionMenuOrderChange, onHiddenItemsChange = onOptionMenuHiddenItemsChange,
                            onShowIconsChange = onOptionMenuShowIconsChange, onTextColorChange = onOptionMenuTextColorChange,
                            onOpacityChange = onOptionMenuOpacityChange, onPriorityHiddenItemsChange = onPriorityMenuHiddenItemsChange,
                            onColorHiddenItemsChange = onColorMenuHiddenItemsChange, onReset = onResetOptionMenu)
                        Spacer(modifier = Modifier.height(22.dp))
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
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
                        if (isAdvancedMode) {
                            Spacer(modifier = Modifier.height(20.dp))
                            /*
                             * En modo avanzado se conserva la barra secundaria
                             * de tono. En modo básico el tono se elige tocando
                             * directamente uno de los cuatro círculos de la paleta.
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
                    }
                    if (isAdvancedMode) {
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
                        horizontalOutset = 8.dp) { panelColors ->
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
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    /*
                     * -------------------------------------------------
                     * FONT
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp) { panelColors ->
                        if (isAdvancedMode) {
                            val systemFontLabel = stringResource(R.string.mock_font_default)
                            val systemSansLabel = stringResource(R.string.mock_font_system_sans)
                            val googleSansFlexLabel = stringResource(R.string.mock_font_google_sans_flex)
                            val serifLabel = stringResource(R.string.mock_font_serif)
                            val monospaceLabel = stringResource(R.string.mock_font_monospace)
                            val fontOptions = buildList {
                                add("system_default" to systemFontLabel)
                                add("system_sans" to systemSansLabel)
                                if (developerGoogleSansFlexUnlocked) {
                                    add("developer_google_sans_flex" to googleSansFlexLabel)
                                }
                                add("serif" to serifLabel)
                                add("monospace" to monospaceLabel)
                            }
                            SettingDropdown(title = stringResource(R.string.mock_font),
                            selectedLabel = when (settings.font) {
                                    "system_default" -> systemFontLabel
                                    "developer_google_sans_flex" -> googleSansFlexLabel
                                    "serif" -> serifLabel
                                    "monospace" -> monospaceLabel
                                    else -> systemSansLabel
                                },
                            options = fontOptions,
                            textColor = panelColors.text,
                            textColorMode = settings.textColor,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onSelected = onFontChange)
                            Spacer(modifier = Modifier.height(14.dp))
                        }
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
                            // En la configuración básica el tamaño de fuente usa siempre
                            // el diseño Capsule. En modo avanzado se respeta el estilo
                            // elegido por el usuario desde la personalización de sliders.
                            style = if (isAdvancedMode) settings.sliderStyle else "capsule",
                            valueLabel = "${localFontSize.roundToInt()} sp")
                    }
                    if (isAdvancedMode) {
                    Spacer(modifier = Modifier.height(14.dp))
                    /*
                     * -------------------------------------------------
                     * SOUND EFFECTS
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 10.dp, end = 0.dp, bottom = SoundHapticPanelBottomPadding),
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
                                        "material" -> stringResource(R.string.sound_theme_material)
                                        "expressive" -> stringResource(R.string.sound_theme_expressive)
                                        "prism" -> stringResource(R.string.sound_theme_prism)
                                        "aurora" -> stringResource(R.string.sound_theme_aurora)
                                        "fluid" -> stringResource(R.string.sound_theme_fluid)
                                        "pulse" -> stringResource(R.string.sound_theme_pulse)
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
                                        "neon" to stringResource(R.string.sound_theme_neon),
                                        "material" to stringResource(R.string.sound_theme_material),
                                        "expressive" to stringResource(R.string.sound_theme_expressive),
                                        "prism" to stringResource(R.string.sound_theme_prism),
                                        "aurora" to stringResource(R.string.sound_theme_aurora),
                                        "fluid" to stringResource(R.string.sound_theme_fluid),
                                        "pulse" to stringResource(R.string.sound_theme_pulse)),
                                textColor = panelColors.text,
                                textColorMode = settings.textColor,
                                menuBackground = menuBackground,
                                menuTextColor = settingsMenuTextColor,
                                fontFamily = fontFamily,
                                playDefaultSelectionFeedback = false,
                                onSelected = { selectedTheme ->
                                    UiHapticPlayer.play(context = context, haptic = UiHaptic.Selection)
                                    onSoundEffectsThemeChange(selectedTheme)
                                    UiSoundPlayer.previewTheme(
                                        context = context,
                                        theme = selectedTheme,
                                        sound = UiSound.Edit,
                                        volumePercent = localSoundEffectsVolume
                                    )
                                })
                            Spacer(modifier = Modifier.height(PreviewButtonVerticalGap))
                            RoundedPreviewButton(
                                text = stringResource(R.string.sound_effects_preview),
                                panelBackground = panelColors.background,
                                panelContentColor = panelColors.text,
                                textColorMode = settings.textColor,
                                fontFamily = fontFamily,
                                onClick = {
                                    UiSoundPlayer.previewTheme(
                                        context = context,
                                        theme = settings.soundEffectsTheme,
                                        sound = UiSound.Attachment,
                                        volumePercent = localSoundEffectsVolume
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(PreviewToSliderGap))
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
                     * REMINDER / ALERT SOUNDS
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(
                        textColorMode = settings.textColor,
                        contentPadding = PaddingValues(start = 6.dp, top = 10.dp, end = 0.dp, bottom = SoundHapticPanelBottomPadding),
                        horizontalOutset = 8.dp
                    ) { panelColors ->
                        Text(
                            text = stringResource(R.string.reminder_tones),
                            color = panelColors.text,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = stringResource(R.string.reminder_tones_description),
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                            color = panelColors.secondaryText,
                            fontFamily = fontFamily,
                            fontSize = 12.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.reminder_sound_enabled),
                                color = panelColors.text,
                                fontFamily = fontFamily,
                                fontSize = 14.sp
                            )
                            Switch(
                                checked = settings.reminderSoundEnabled,
                                onCheckedChange = { checked ->
                                    UiSoundPlayer.playToggleAudioOnly(context = context, checked = checked)
                                    UiHapticPlayer.playToggle(context = context, checked = checked)
                                    onReminderSoundEnabledChange(checked)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingDropdown(
                            title = stringResource(R.string.reminder_tone),
                            selectedLabel = when (settings.reminderRingtone) {
                                "bell" -> stringResource(R.string.reminder_tone_bell)
                                "crystal" -> stringResource(R.string.reminder_tone_crystal)
                                "pulse" -> stringResource(R.string.reminder_tone_pulse)
                                "sunrise" -> stringResource(R.string.reminder_tone_sunrise)
                                "digital" -> stringResource(R.string.reminder_tone_digital)
                                "alert" -> stringResource(R.string.reminder_tone_alert)
                                "urgent" -> stringResource(R.string.reminder_tone_urgent)
                                "beacon" -> stringResource(R.string.reminder_tone_beacon)
                                "radar" -> stringResource(R.string.reminder_tone_radar)
                                "warning" -> stringResource(R.string.reminder_tone_warning)
                                "signal" -> stringResource(R.string.reminder_tone_signal)
                                "pager" -> stringResource(R.string.reminder_tone_pager)
                                "double_alarm" -> stringResource(R.string.reminder_tone_double_alarm)
                                "serenity" -> stringResource(R.string.reminder_tone_serenity)
                                "soft_bell" -> stringResource(R.string.reminder_tone_soft_bell)
                                "breeze" -> stringResource(R.string.reminder_tone_breeze)
                                "dew" -> stringResource(R.string.reminder_tone_dew)
                                "bamboo" -> stringResource(R.string.reminder_tone_bamboo)
                                "horizon" -> stringResource(R.string.reminder_tone_horizon)
                                "calm" -> stringResource(R.string.reminder_tone_calm)
                                "moonlight" -> stringResource(R.string.reminder_tone_moonlight)
                                "orbit" -> stringResource(R.string.reminder_tone_orbit)
                                "droplet" -> stringResource(R.string.reminder_tone_droplet)
                                "glass_tap" -> stringResource(R.string.reminder_tone_glass_tap)
                                "clockwork" -> stringResource(R.string.reminder_tone_clockwork)
                                "spark" -> stringResource(R.string.reminder_tone_spark)
                                "bubble_pop" -> stringResource(R.string.reminder_tone_bubble_pop)
                                "comet" -> stringResource(R.string.reminder_tone_comet)
                                "echo_ping" -> stringResource(R.string.reminder_tone_echo_ping)
                                "woodblock" -> stringResource(R.string.reminder_tone_woodblock)
                                "starlight" -> stringResource(R.string.reminder_tone_starlight)
                                "sentinel" -> stringResource(R.string.reminder_tone_sentinel)
                                "siren" -> stringResource(R.string.reminder_tone_siren)
                                "cascade" -> stringResource(R.string.reminder_tone_cascade)
                                "escalation" -> stringResource(R.string.reminder_tone_escalation)
                                "distress" -> stringResource(R.string.reminder_tone_distress)
                                "interlock" -> stringResource(R.string.reminder_tone_interlock)
                                "scanner" -> stringResource(R.string.reminder_tone_scanner)
                                "command" -> stringResource(R.string.reminder_tone_command)
                                "rapid_triple" -> stringResource(R.string.reminder_tone_rapid_triple)
                                "priority_sequence" -> stringResource(R.string.reminder_tone_priority_sequence)
                                "double_sweep" -> stringResource(R.string.reminder_tone_double_sweep)
                                "attention_burst" -> stringResource(R.string.reminder_tone_attention_burst)
                                else -> stringResource(R.string.reminder_tone_classic)
                            },
                            options = listOf(
                                "classic" to stringResource(R.string.reminder_tone_classic),
                                "bell" to stringResource(R.string.reminder_tone_bell),
                                "crystal" to stringResource(R.string.reminder_tone_crystal),
                                "pulse" to stringResource(R.string.reminder_tone_pulse),
                                "sunrise" to stringResource(R.string.reminder_tone_sunrise),
                                "digital" to stringResource(R.string.reminder_tone_digital),
                                "alert" to stringResource(R.string.reminder_tone_alert),
                                "urgent" to stringResource(R.string.reminder_tone_urgent),
                                "beacon" to stringResource(R.string.reminder_tone_beacon),
                                "radar" to stringResource(R.string.reminder_tone_radar),
                                "warning" to stringResource(R.string.reminder_tone_warning),
                                "signal" to stringResource(R.string.reminder_tone_signal),
                                "pager" to stringResource(R.string.reminder_tone_pager),
                                "double_alarm" to stringResource(R.string.reminder_tone_double_alarm),
                                "serenity" to stringResource(R.string.reminder_tone_serenity),
                                "soft_bell" to stringResource(R.string.reminder_tone_soft_bell),
                                "breeze" to stringResource(R.string.reminder_tone_breeze),
                                "dew" to stringResource(R.string.reminder_tone_dew),
                                "bamboo" to stringResource(R.string.reminder_tone_bamboo),
                                "horizon" to stringResource(R.string.reminder_tone_horizon),
                                "calm" to stringResource(R.string.reminder_tone_calm),
                                "moonlight" to stringResource(R.string.reminder_tone_moonlight),
                                "orbit" to stringResource(R.string.reminder_tone_orbit),
                                "droplet" to stringResource(R.string.reminder_tone_droplet),
                                "glass_tap" to stringResource(R.string.reminder_tone_glass_tap),
                                "clockwork" to stringResource(R.string.reminder_tone_clockwork),
                                "spark" to stringResource(R.string.reminder_tone_spark),
                                "bubble_pop" to stringResource(R.string.reminder_tone_bubble_pop),
                                "comet" to stringResource(R.string.reminder_tone_comet),
                                "echo_ping" to stringResource(R.string.reminder_tone_echo_ping),
                                "woodblock" to stringResource(R.string.reminder_tone_woodblock),
                                "starlight" to stringResource(R.string.reminder_tone_starlight),
                                "sentinel" to stringResource(R.string.reminder_tone_sentinel),
                                "siren" to stringResource(R.string.reminder_tone_siren),
                                "cascade" to stringResource(R.string.reminder_tone_cascade),
                                "escalation" to stringResource(R.string.reminder_tone_escalation),
                                "distress" to stringResource(R.string.reminder_tone_distress),
                                "interlock" to stringResource(R.string.reminder_tone_interlock),
                                "scanner" to stringResource(R.string.reminder_tone_scanner),
                                "command" to stringResource(R.string.reminder_tone_command),
                                "rapid_triple" to stringResource(R.string.reminder_tone_rapid_triple),
                                "priority_sequence" to stringResource(R.string.reminder_tone_priority_sequence),
                                "double_sweep" to stringResource(R.string.reminder_tone_double_sweep),
                                "attention_burst" to stringResource(R.string.reminder_tone_attention_burst)
                            ),
                            textColor = panelColors.text,
                            textColorMode = settings.textColor,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            playDefaultSelectionFeedback = false,
                            onSelected = { selectedRingtone ->
                                UiHapticPlayer.play(context = context, haptic = UiHaptic.Selection)
                                onReminderRingtoneChange(selectedRingtone)
                                ReminderFeedbackPreferences.previewRingtone(
                                    context = context,
                                    ringtone = selectedRingtone,
                                    volumePercent = localReminderSoundVolume
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(PreviewButtonVerticalGap))
                        RoundedPreviewButton(
                            text = stringResource(R.string.reminder_tone_preview),
                            panelBackground = panelColors.background,
                            panelContentColor = panelColors.text,
                            textColorMode = settings.textColor,
                            fontFamily = fontFamily,
                            onClick = {
                                ReminderFeedbackPreferences.previewRingtone(
                                    context = context,
                                    ringtone = settings.reminderRingtone,
                                    volumePercent = localReminderSoundVolume
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(PreviewToSliderGap))
                        SettingTitleRow(
                            title = stringResource(R.string.reminder_sound_volume),
                            value = "${localReminderSoundVolume.roundToInt()}%",
                            color = panelColors.text,
                            fontFamily = fontFamily
                        )
                        StyledSettingsSlider(
                            value = localReminderSoundVolume,
                            onValueChange = { localReminderSoundVolume = it },
                            onValueChangeFinished = { onReminderSoundVolumeChange(localReminderSoundVolume) },
                            valueRange = 0f..100f,
                            steps = 19,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            style = settings.sliderStyle,
                            valueLabel = "${localReminderSoundVolume.roundToInt()}%"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    /*
                     * -------------------------------------------------
                     * HAPTIC / VIBRATION EFFECTS
                     * -------------------------------------------------
                     */
                    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(start = 6.dp, top = 10.dp, end = 0.dp, bottom = SoundHapticPanelBottomPadding),
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
                            Switch(
                                checked = settings.hapticEffectsEnabled,
                                onCheckedChange = { checked ->
                                    UiSoundPlayer.playToggleAudioOnly(context = context, checked = checked)
                                    UiHapticPlayer.playToggle(context = context, checked = checked, force = true)
                                    onHapticEffectsEnabledChange(checked)
                                }
                            )
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
                                playDefaultSelectionFeedback = false,
                                onSelected = { selectedStyle ->
                                    UiSoundPlayer.playActionAudioOnly(context = context, action = UiActionSound.Select)
                                    onHapticEffectsStyleChange(selectedStyle)
                                    UiHapticPlayer.previewStyle(
                                        context = context,
                                        style = selectedStyle,
                                        intensityPercent = localHapticEffectsIntensity,
                                        haptic = UiHaptic.Confirm
                                    )
                                })
                            Spacer(modifier = Modifier.height(PreviewButtonVerticalGap))
                            RoundedPreviewButton(
                                text = stringResource(R.string.haptic_effects_preview),
                                panelBackground = panelColors.background,
                                panelContentColor = panelColors.text,
                                textColorMode = settings.textColor,
                                fontFamily = fontFamily,
                                onClick = {
                                    UiSoundPlayer.playActionAudioOnly(context = context, action = UiActionSound.PlayPause)
                                    UiHapticPlayer.previewStyle(
                                        context = context,
                                        style = settings.hapticEffectsStyle,
                                        intensityPercent = localHapticEffectsIntensity,
                                        haptic = UiHaptic.Confirm
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(PreviewToSliderGap))
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
                                    "system" -> stringResource(R.string.language_system)
                                    "en" -> "English"
                                    "fr" -> "Français"
                                    "zh-CN" -> "中文（简体）"
                                    else -> "Español"
                                },
                            options = listOf("system" to stringResource(R.string.language_system),
                                        "es" to "Español", "en" to
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
                    if (isAdvancedMode) {
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
                    Spacer(modifier = Modifier.height(18.dp))
                    BackupRestoreSection(settings = settings, fontFamily = fontFamily, textColor = settingsTextColor,
                        secondaryTextColor = settingsSecondaryTextColor, graphicColor = settingsGraphicColor)
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
                    Spacer(modifier = Modifier.height(14.dp))
                    /*
                     * -------------------------------------------------
                     * ACTUALIZACIONES DE LA APLICACIÓN
                     * -------------------------------------------------
                     * Consulta la última Release estable de GitHub. Si la etiqueta
                     * es superior a versionName y existe un asset .apk, permite
                     * descargarlo y pasarlo al instalador oficial de Android.
                     */
                    SettingsSectionPanel(
                        textColorMode = settings.textColor,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                        horizontalOutset = 8.dp
                    ) { panelColors ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = panelColors.graphic,
                                    modifier = Modifier.size(23.dp)
                                )
                                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(
                                        text = stringResource(R.string.update_settings_title),
                                        color = panelColors.text,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.update_settings_description),
                                        modifier = Modifier.padding(top = 2.dp),
                                        color = panelColors.secondaryText,
                                        fontFamily = fontFamily,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Text(
                                text = stringResource(R.string.update_current_version, installedVersion),
                                modifier = Modifier.padding(top = 10.dp),
                                color = panelColors.secondaryText,
                                fontFamily = fontFamily,
                                fontSize = 12.sp
                            )

                            Button(
                                onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                    updateScope.launch {
                                        checkingForUpdate = true
                                        updateActionMessage = null
                                        updateResult = GitHubUpdateManager.checkForUpdate(context)
                                        checkingForUpdate = false
                                    }
                                },
                                enabled = !checkingForUpdate && !downloadingUpdate,
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                if (checkingForUpdate) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(17.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = stringResource(
                                        if (checkingForUpdate) R.string.update_checking else R.string.update_check
                                    ),
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            when (val result = updateResult) {
                                is GitHubUpdateManager.CheckResult.UpdateAvailable -> {
                                    Text(
                                        text = stringResource(R.string.update_available, result.release.version),
                                        modifier = Modifier.padding(top = 12.dp),
                                        color = panelColors.text,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    if (result.release.title.isNotBlank() && result.release.title != result.release.version) {
                                        Text(
                                            text = result.release.title,
                                            modifier = Modifier.padding(top = 2.dp),
                                            color = panelColors.secondaryText,
                                            fontFamily = fontFamily,
                                            fontSize = 12.sp
                                        )
                                    }
                                    if (result.release.apkDownloadUrl != null) {
                                        Button(
                                            onClick = { requestDownloadAndInstall(result.release) },
                                            enabled = !downloadingUpdate,
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            if (downloadingUpdate) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(17.dp),
                                                    strokeWidth = 2.dp,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }
                                            Text(
                                                text = stringResource(
                                                    if (downloadingUpdate) R.string.update_downloading
                                                    else R.string.update_download_install
                                                ),
                                                fontFamily = fontFamily,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(R.string.update_release_without_apk),
                                            modifier = Modifier.padding(top = 6.dp),
                                            color = panelColors.secondaryText,
                                            fontFamily = fontFamily,
                                            fontSize = 12.sp
                                        )
                                    }
                                    TextButton(
                                        onClick = { GitHubUpdateManager.openReleasePage(context, result.release.htmlUrl) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = stringResource(R.string.update_open_release), fontFamily = fontFamily)
                                    }
                                }

                                is GitHubUpdateManager.CheckResult.UpToDate -> Text(
                                    text = stringResource(R.string.update_up_to_date, result.latestVersion),
                                    modifier = Modifier.padding(top = 10.dp),
                                    color = panelColors.secondaryText,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp
                                )

                                is GitHubUpdateManager.CheckResult.NoPublishedRelease -> {
                                    Text(
                                        text = stringResource(R.string.update_no_release),
                                        modifier = Modifier.padding(top = 10.dp),
                                        color = panelColors.secondaryText,
                                        fontFamily = fontFamily,
                                        fontSize = 12.sp
                                    )
                                    TextButton(
                                        onClick = { GitHubUpdateManager.openReleasePage(context) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = stringResource(R.string.update_open_release), fontFamily = fontFamily)
                                    }
                                }

                                is GitHubUpdateManager.CheckResult.Failure -> Text(
                                    text = stringResource(R.string.update_error, result.reason),
                                    modifier = Modifier.padding(top = 10.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp
                                )

                                null -> Unit
                            }

                            updateActionMessage?.let { message ->
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    fontFamily = fontFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        /*
         * El indicador debe ser hermano del contenido desplazable, no un hijo
         * de la Column con verticalScroll. De esa forma permanece visible en
         * todo momento mientras Settings se desplaza y no termina colocado al
         * final del contenido, fuera del viewport actual.
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 2.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            ScrollPositionCapsule(
                state = settingsScrollState,
                backgroundColor = MaterialTheme.colorScheme.background,
                preferredColor = MaterialTheme.colorScheme.onBackground
            )
        }
        }
    }
    }
}

@Composable
private fun ProfileAndModePanel(
    settings: AppSettings,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    isAdvancedMode: Boolean,
    profileSize: Float,
    onProfileSizeValueChange: (Float) -> Unit,
    onConfigurationModeChange: (String) -> Unit,
    onProfileImageUriChange: (String) -> Unit,
    onProfileImageSizeChange: () -> Unit,
) {
    val context = LocalContext.current
    var profileEditorUri by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
            }
            UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
            profileEditorUri = uri
        }
    }

    profileEditorUri?.let { sourceUri ->
        ProfileImageEditorDialog(
            sourceUri = sourceUri,
            fontFamily = fontFamily,
            onDismissRequest = { profileEditorUri = null },
            onImageSaved = { croppedUri ->
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                onProfileImageUriChange(croppedUri.toString())
                profileEditorUri = null
            }
        )
    }

    SettingsSectionPanel(
        textColorMode = settings.textColor,
        contentPadding = PaddingValues(14.dp),
        horizontalOutset = 8.dp
    ) { panelColors ->
        /*
         * El texto de los controles conserva EXACTAMENTE el color resuelto para
         * el panel. En Automático esto significa blanco sobre familias oscuras
         * y negro sobre familias claras. Adaptamos el fondo del botón, no el
         * texto, para evitar botones grises con letras negras dentro de una UI
         * oscura (o el caso inverso en paletas claras).
         */
        val primaryColor = MaterialTheme.colorScheme.primary
        val accentTonalButtonBase = MaterialTheme.colorScheme.primaryContainer
        val secondaryButtonBase = MaterialTheme.colorScheme.surfaceContainerHigh
        val actionButtonColors = remember(accentTonalButtonBase, panelColors.background, settings.textColor) {
            resolveAdaptiveUiButtonColors(
                preferred = accentTonalButtonBase,
                background = panelColors.background,
                textColorMode = settings.textColor,
                minimumContentContrast = 4.5f,
                minimumSurfaceContrast = 1.65f
            )
        }
        val actionButtonContainerColor = actionButtonColors.container
        val actionButtonContentColor = actionButtonColors.content
        val actionButtonBorderColor = remember(actionButtonContentColor, actionButtonContainerColor) {
            ensureUiContrast(
                preferred = actionButtonContentColor,
                background = actionButtonContainerColor,
                minimumContrast = 3f
            )
        }
        val modeSelectedColors = remember(primaryColor, panelColors.background, settings.textColor) {
            resolveAdaptiveUiButtonColors(
                preferred = primaryColor,
                background = panelColors.background,
                textColorMode = settings.textColor,
                minimumContentContrast = 4.5f,
                minimumSurfaceContrast = 1.85f
            )
        }
        val modeSelectedContainerColor = modeSelectedColors.container
        val modeSelectedContentColor = modeSelectedColors.content
        val modeUnselectedColors = remember(secondaryButtonBase, panelColors.background, settings.textColor) {
            resolveAdaptiveUiButtonColors(
                preferred = secondaryButtonBase,
                background = panelColors.background,
                textColorMode = settings.textColor,
                minimumContentContrast = 4.5f,
                minimumSurfaceContrast = 1.35f
            )
        }
        val modeUnselectedContainerColor = modeUnselectedColors.container
        val modeUnselectedContentColor = modeUnselectedColors.content
        Text(
            text = stringResource(R.string.extreme_profile),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            /*
             * Vista previa del tamaño del avatar en tiempo real.
             *
             * profileSize usa el estado local que actualiza el slider en cada
             * movimiento. El valor persistente se guarda únicamente al terminar
             * de arrastrar, evitando escrituras continuas en DataStore mientras
             * la previsualización sigue respondiendo de forma inmediata.
             */
            val previewAvatarSize = profileSize.coerceIn(36f, 84f).dp
            val previewIconSize = (profileSize * 0.46f).coerceIn(20f, 38f).dp
            Surface(
                modifier = Modifier.size(previewAvatarSize).clickable(enabled = settings.profileImageUri.isNotBlank()) {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                    profileEditorUri = managedProfileSourceUri(context, settings.profileImageUri)
                        ?: Uri.parse(settings.profileImageUri)
                },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                if (settings.profileImageUri.isNotBlank()) {
                    AsyncImage(
                        model = Uri.parse(settings.profileImageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(previewIconSize),
                            tint = panelColors.text
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                        picker.launch(arrayOf("image/*"))
                    },
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, actionButtonBorderColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = actionButtonContainerColor,
                        contentColor = actionButtonContentColor
                    )
                ) {
                    Text(
                        text = stringResource(R.string.extreme_change_photo),
                        color = actionButtonContentColor,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
                if (settings.profileImageUri.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                            profileEditorUri = managedProfileSourceUri(context, settings.profileImageUri)
                                ?: Uri.parse(settings.profileImageUri)
                        },
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, actionButtonBorderColor),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = actionButtonContainerColor,
                            contentColor = actionButtonContentColor
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.extreme_edit_photo),
                            color = actionButtonContentColor,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                            clearManagedProfileImages(context)
                            onProfileImageUriChange("")
                        },
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, actionButtonBorderColor),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = actionButtonContainerColor,
                            contentColor = actionButtonContentColor
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.extreme_remove_photo),
                            color = actionButtonContentColor,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (isAdvancedMode) {
            Spacer(modifier = Modifier.height(12.dp))
            SettingTitleRow(
                title = stringResource(R.string.extreme_profile_size),
                value = "${profileSize.roundToInt()} dp",
                color = panelColors.text,
                fontFamily = fontFamily
            )
            StyledSettingsSlider(
                value = profileSize,
                onValueChange = onProfileSizeValueChange,
                onValueChangeFinished = onProfileImageSizeChange,
                valueRange = 36f..84f,
                steps = 23,
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                style = settings.sliderStyle,
                valueLabel = "${profileSize.roundToInt()} dp"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.configuration_mode_settings_title),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Text(
            text = stringResource(R.string.configuration_mode_settings_description),
            modifier = Modifier.padding(top = 2.dp),
            color = panelColors.secondaryText,
            fontFamily = fontFamily,
            fontSize = 12.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val basicSelected = settings.configurationMode == "basic"
            Button(
                onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                    onConfigurationModeChange("basic")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(13.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (basicSelected) modeSelectedContainerColor else modeUnselectedContainerColor,
                    contentColor = if (basicSelected) modeSelectedContentColor else modeUnselectedContentColor
                )
            ) {
                Text(
                    text = stringResource(R.string.configuration_mode_basic),
                    color = if (basicSelected) modeSelectedContentColor else modeUnselectedContentColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
            Button(
                onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                    onConfigurationModeChange("advanced")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(13.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (settings.configurationMode == "advanced") modeSelectedContainerColor else modeUnselectedContainerColor,
                    contentColor = if (settings.configurationMode == "advanced") modeSelectedContentColor else modeUnselectedContentColor
                )
            ) {
                Text(
                    text = stringResource(R.string.configuration_mode_advanced),
                    color = if (settings.configurationMode == "advanced") modeSelectedContentColor else modeUnselectedContentColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SettingTitle(text: String, color: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily) {
    Text(text = text,
        color = color,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp)
}


@Composable
private fun RoundedPreviewButton(
    text: String,
    panelBackground: Color,
    panelContentColor: Color,
    textColorMode: String,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val resolvedButtonColors = remember(primaryColor, panelBackground, textColorMode) {
        resolveAdaptiveUiButtonColors(
            preferred = primaryColor,
            background = panelBackground,
            textColorMode = textColorMode,
            minimumContentContrast = 4.5f,
            minimumSurfaceContrast = 1.55f
        )
    }
    val containerColor = resolvedButtonColors.container
    val buttonContentColor = resolvedButtonColors.content
    Button(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = 0.dp, minHeight = 40.dp),
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = buttonContentColor
        )
    ) {
        Text(
            text = text,
            fontFamily = fontFamily,
            color = buttonContentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
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

@Composable
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

@Composable
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

@Composable
private fun SettingDropdown(title: String, selectedLabel: String, options: List<Pair<String, String>>, textColor: Color,
    textColorMode: String, menuBackground: Color, menuTextColor: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily, playDefaultSelectionFeedback: Boolean = true, onSelected: (String) -> Unit) {
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
                                if (playDefaultSelectionFeedback) {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                                }
                                expanded = false
                                onSelected(option.first)
                            })
                    }
            }
        }
    }
}
