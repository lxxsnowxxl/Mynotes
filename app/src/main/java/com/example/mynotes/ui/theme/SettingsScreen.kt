package com.example.mynotes.ui

import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.components.SettingsTitle
import com.example.mynotes.ui.components.AppHeading
import com.example.mynotes.ui.theme.rememberUiTextColors
import com.example.mynotes.ui.theme.rememberUiTextColor
import com.example.mynotes.ui.theme.rememberUiContentColors
import com.example.mynotes.ui.components.AppTextButton

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.mynotes.R
import com.example.mynotes.reminders.ReminderFeedbackPreferences
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.DeveloperFeatures
import com.example.mynotes.settings.FeedbackPreferencePolicy
import com.example.mynotes.settings.FontPreferencePolicy
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.ui.components.BackupRestoreSection
import com.example.mynotes.ui.components.ExtremeAccentSettingsSection
import com.example.mynotes.ui.components.ExtremeCustomizationHeader
import com.example.mynotes.ui.components.ExtremeFabSettingsSection
import com.example.mynotes.ui.components.ExtremeIconsSettingsSection
import com.example.mynotes.ui.components.ExtremeMotionSettingsSection
import com.example.mynotes.ui.components.ExtremeNoteCardsSettingsSection
import com.example.mynotes.ui.components.ExtremePerformanceSettingsSection
import com.example.mynotes.ui.components.OptionsMenuAppearanceSettingsSection
import com.example.mynotes.ui.components.OptionsMenuColorSettingsSection
import com.example.mynotes.ui.components.OptionsMenuHeader
import com.example.mynotes.ui.components.OptionsMenuMainActionsSettingsSection
import com.example.mynotes.ui.components.OptionsMenuPrioritySettingsSection
import com.example.mynotes.ui.components.PaletteSelectorRow
import com.example.mynotes.ui.components.ProfileImageEditorDialog
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.SettingsDropdownItem
import com.example.mynotes.ui.components.SettingsSectionPanel
import com.example.mynotes.ui.components.SettingsBackgroundOptions
import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.components.settingsBackgroundColors
import com.example.mynotes.ui.components.SettingsSecondaryText
import com.example.mynotes.ui.components.StyledSettingsSlider
import com.example.mynotes.ui.components.clearManagedProfileImages
import com.example.mynotes.ui.components.managedProfileSourceUri
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiHaptic
import com.example.mynotes.ui.sound.UiHapticPlayer
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.rememberAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.update.GitHubUpdateManager
import com.example.mynotes.ui.state.SettingsActions
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Immutable
private data class SliderStyleOption(val key: String, val labelRes: Int)

private val PreviewButtonVerticalGap = 4.dp
private val PreviewToSliderGap = 10.dp
private val SoundHapticPanelBottomPadding = 18.dp
private val StandardSettingsPanelPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp)
private val FeedbackSettingsPanelPadding = PaddingValues(start = 6.dp, top = 10.dp, end = 0.dp, bottom = SoundHapticPanelBottomPadding)

private val SliderStyleOptions = listOf(SliderStyleOption("minimal", R.string.mock_slider_minimal), SliderStyleOption("capsule",
            R.string.mock_slider_capsule), SliderStyleOption("glow", R.string.mock_slider_glow), SliderStyleOption("glass",
            R.string.mock_slider_glass), SliderStyleOption("segmented", R.string.mock_slider_segmented), SliderStyleOption("dots",
            R.string.mock_slider_dots), SliderStyleOption("gradient", R.string.mock_slider_gradient), SliderStyleOption("neumorphic",
            R.string.mock_slider_neumorphic), SliderStyleOption("line_pill", R.string.mock_slider_line_pill), SliderStyleOption("floating",
            R.string.mock_slider_floating))
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings, viewModel: SettingsActions, onLanguageChange: (String) -> Unit,
    onOpenDevelopmentInfo: () -> Unit, onBack: () -> Unit
) {
    val context = LocalContext.current
    val layoutPreview = LocalLayoutPreview.current
    val developerGoogleSansFlexUnlocked = if (layoutPreview != null) false else DeveloperFeatures.isGoogleSansFlexUnlocked(context)
    val fontFamily = rememberAppFontFamily(settings.font)
    var localSettingsPanelTone by rememberSyncedFloatState(settings.settingsPanelTone)
    var localSurfacePanelIntensity by rememberSyncedFloatState(settings.surfacePanelIntensity)
    var localFontSize by rememberSyncedFloatState(settings.fontSize)
    var localProfileSize by rememberSyncedFloatState(settings.profileImageSize)
    var localSoundEffectsVolume by rememberSyncedFloatState(settings.soundEffectsVolume)
    var localReminderSoundVolume by rememberSyncedFloatState(settings.reminderSoundVolume)
    var localHapticEffectsIntensity by rememberSyncedFloatState(settings.hapticEffectsIntensity)
    val selectedPalette = remember(settings.backgroundColor) {
            PaletteCatalog.find(settings.backgroundColor)
        }
    val selectedPaletteTone = selectedPalette.toneAt(settings.backgroundToneIndex)
    val settingsPanelBase = MaterialTheme.colorScheme.surfaceContainerLow
    val settingsPanelColor = remember(settingsPanelBase, selectedPaletteTone, localSettingsPanelTone) {
        lerp(settingsPanelBase, selectedPaletteTone, (localSettingsPanelTone / 100f).coerceIn(0f, 1f))
    }
    val (settingsTextColor, settingsSecondaryTextColor, settingsGraphicColor) =
        rememberUiContentColors(settings.textColor, settingsPanelColor)
    val screenBackground = MaterialTheme.colorScheme.background
    val (settingsScreenTextColor, settingsScreenSecondaryTextColor) = rememberUiTextColors(settings.textColor, screenBackground)
    val backgroundColors = remember(selectedPalette, settings.backgroundToneIndex, screenBackground, settingsScreenTextColor) {
        settingsBackgroundColors(selectedPalette, settings.backgroundToneIndex, screenBackground, settingsScreenTextColor)
    }
    val settingsContentBackground = if (settings.settingsBackgroundStyle == "solid") settingsPanelColor else Color.Transparent
    val settingsHeaderBackground = if (settings.settingsBackgroundStyle == "solid") screenBackground else Color.Transparent
    val baseColorScheme = MaterialTheme.colorScheme
    val settingsColorScheme = remember(baseColorScheme, settingsPanelColor, settings.settingsBackgroundStyle) {
        if (settings.settingsBackgroundStyle == "solid") baseColorScheme
        else baseColorScheme.copy(surfaceContainerLow = settingsPanelColor)
    }
    val settingsPalettes = remember(settings.backgroundColor) { PaletteCatalog.palettesForSettings(settings.backgroundColor) }
    val menuBackground = when (settings.textColor) {
            "white" -> MaterialTheme.colorScheme.inverseSurface
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val settingsMenuTextColor = rememberUiTextColor(settings.textColor, menuBackground)
    val settingsListState = rememberLazyListState(initialFirstVisibleItemIndex = layoutPreview?.settingsItemIndex ?: 0)
    val isAdvancedMode = settings.configurationMode == "advanced"

    val updateScope = rememberCoroutineScope()
    val installedVersion = remember(context) {
        if (layoutPreview != null) "1.7.0" else GitHubUpdateManager.currentVersionName(context)
    }
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
        MaterialTheme(colorScheme = settingsColorScheme) {
        Box(Modifier.fillMaxSize().appScreenBackground(screenBackground, settingsScreenTextColor)) {
        Scaffold(containerColor = Color.Transparent, contentColor = settingsScreenTextColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = settingsHeaderBackground,
                    scrolledContainerColor = settingsHeaderBackground,
                    titleContentColor = settingsScreenTextColor,
                    navigationIconContentColor = settingsScreenTextColor,
                    actionIconContentColor = settingsScreenTextColor
                ),
                title = {
                    SettingsTitle(stringResource(R.string.mock_settings), settingsScreenTextColor, fontFamily,
                        fontSize = 25.sp)
                },
                navigationIcon = {
                    TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Back, onBack),
                        colors = ButtonDefaults.textButtonColors(contentColor = settingsScreenTextColor)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back),
                            modifier = Modifier.size(25.dp))
                    }
                }
            )
        }) {
            paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                state = settingsListState,
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 14.dp, end = 14.dp, bottom = 32.dp)
            ) {
                item(key = "appearance_profile") {
                    SettingsContentSurface(backgroundColor = settingsContentBackground,
                        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)) {
                            AppHeading(
                                title = stringResource(R.string.mock_appearance), subtitle = stringResource(R.string.mock_appearance_description),
                                fontFamily = fontFamily, titleColor = settingsTextColor, subtitleColor = settingsSecondaryTextColor,
                                titleSize = 21.sp,
                                subtitleModifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileAndModePanel(
                                settings = settings,
                                fontFamily = fontFamily,
                                isAdvancedMode = isAdvancedMode,
                                profileSize = { localProfileSize },
                                onProfileSizeValueChange = { localProfileSize = it },
                                onConfigurationModeChange = viewModel::setConfigurationMode,
                                onProfileImageUriChange = viewModel::setProfileImageUri,
                                onProfileImageSizeChange = { viewModel.setProfileImageSize(localProfileSize) }
                            )
                            Spacer(modifier = Modifier.height(if (isAdvancedMode) 10.dp else 16.dp))
                    }
                }
                if (isAdvancedMode) {
                    settingsSurfaceItem(key = "extreme_header", contentType = "settings_header", backgroundColor = settingsContentBackground, bottomSpacing = 10.dp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ExtremeCustomizationHeader(
                            fontFamily = fontFamily,
                            textColor = settingsScreenTextColor
                        )
                    }
                    settingsSurfaceItem(key = "extreme_icons", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 12.dp) {
                        ExtremeIconsSettingsSection(settings, fontFamily, viewModel::setIconStyle, viewModel::setIconSize)
                    }
                    settingsSurfaceItem(key = "extreme_accent", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 12.dp) {
                        ExtremeAccentSettingsSection(settings, fontFamily, viewModel::setAccentColor)
                    }
                    settingsSurfaceItem(key = "extreme_note_cards", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 12.dp) {
                        ExtremeNoteCardsSettingsSection(
                            settings = settings,
                            fontFamily = fontFamily,
                            onNoteCardCornerRadiusChange = viewModel::setNoteCardCornerRadius,
                            onNoteCardElevationChange = viewModel::setNoteCardElevation,
                            onNoteCardPaddingChange = viewModel::setNoteCardPadding,
                            onNoteCardImageHeightChange = viewModel::setNoteCardImageHeight,
                            onNoteCardOutlineWidthChange = viewModel::setNoteCardOutlineWidth,
                            onNoteTitleMaxLinesChange = viewModel::setNoteTitleMaxLines,
                            onNoteContentMaxLinesChange = viewModel::setNoteContentMaxLines,
                            onNoteLineSpacingChange = viewModel::setNoteLineSpacing,
                            onShowNoteDateChange = viewModel::setShowNoteDate,
                            onShowCategoryChipChange = viewModel::setShowCategoryChip,
                            onShowFavoriteIconChange = viewModel::setShowFavoriteIcon
                        )
                    }
                    settingsSurfaceItem(key = "extreme_fab", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 12.dp) {
                        ExtremeFabSettingsSection(settings, fontFamily, viewModel::setFabSize)
                    }
                    settingsSurfaceItem(key = "extreme_performance", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 12.dp) {
                        ExtremePerformanceSettingsSection(settings, fontFamily, viewModel::setPerformanceMode)
                    }
                    settingsSurfaceItem(key = "extreme_motion", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 18.dp) {
                        ExtremeMotionSettingsSection(
                            settings = settings,
                            fontFamily = fontFamily,
                            onAnimationsEnabledChange = viewModel::setAnimationsEnabled,
                            onAnimationStyleChange = viewModel::setAnimationStyle,
                            onAnimationEasingChange = viewModel::setAnimationEasing,
                            onAnimationSpeedChange = viewModel::setAnimationSpeed,
                            onAnimationIntensityChange = viewModel::setAnimationIntensity
                        )
                    }
                    settingsSurfaceItem(key = "options_header", contentType = "settings_header", backgroundColor = settingsContentBackground, bottomSpacing = 10.dp) {
                        OptionsMenuHeader(fontFamily, settingsScreenTextColor, settingsScreenSecondaryTextColor)
                    }
                    settingsSurfaceItem(key = "options_appearance", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 8.dp) {
                        OptionsMenuAppearanceSettingsSection(
                            settings,
                            fontFamily,
                            viewModel::setOptionMenuShowIcons,
                            viewModel::setOptionMenuTextColor,
                            viewModel::setOptionMenuOpacity
                        )
                    }
                    settingsSurfaceItem(key = "options_main_actions", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 8.dp) {
                        OptionsMenuMainActionsSettingsSection(
                            settings,
                            fontFamily,
                            viewModel::setOptionMenuOrder,
                            viewModel::setOptionMenuHiddenItems
                        )
                    }
                    settingsSurfaceItem(key = "options_priority", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 8.dp) {
                        OptionsMenuPrioritySettingsSection(settings, fontFamily, viewModel::setPriorityMenuHiddenItems)
                    }
                    settingsSurfaceItem(key = "options_color", contentType = "settings_panel", backgroundColor = settingsContentBackground, bottomSpacing = 8.dp) {
                        OptionsMenuColorSettingsSection(settings, fontFamily, viewModel::setColorMenuHiddenItems)
                    }
                }
                settingsSurfaceItem(key = "palette_background", contentType = "palette_background", backgroundColor = settingsContentBackground, bottomSpacing = 12.dp) {
                    SettingsSectionPanel(
                        textColorMode = settings.textColor,
                        contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 6.dp, bottom = 14.dp),
                        horizontalOutset = 8.dp
                    ) { panelColors ->
                        SettingTitle(
                            text = stringResource(R.string.mock_color_palette),
                            color = panelColors.text,
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PaletteSelectorRow(
                            palettes = settingsPalettes,
                            selectedPaletteKey = settings.backgroundColor,
                            selectedToneIndex = settings.backgroundToneIndex,
                            onToneSelected = viewModel::setBackgroundSelection,
                            animationsEnabled = settings.animationsEnabled,
                            animationSpeed = settings.animationSpeed,
                            textColorMode = settings.textColor,
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        SettingsBackgroundOptions(
                            selectedStyle = settings.settingsBackgroundStyle,
                            colors = backgroundColors,
                            panel = panelColors,
                            fontFamily = fontFamily,
                            onStyleSelected = viewModel::setSettingsBackgroundStyle
                        )
                    }
                }
                if (isAdvancedMode) {
                    percentSliderItem("appearance_panel_tone", R.string.settings_panel_tone, localSettingsPanelTone, settings,
                        settingsContentBackground, fontFamily, { localSettingsPanelTone = it }, { viewModel.setSettingsPanelTone(localSettingsPanelTone) })
                    percentSliderItem("appearance_translucent_panels", R.string.surface_panels_intensity, localSurfacePanelIntensity, settings,
                        settingsContentBackground, fontFamily, { localSurfacePanelIntensity = it }, { viewModel.setSurfacePanelIntensity(localSurfacePanelIntensity) },
                        descriptionRes = R.string.surface_panels_intensity_description)

                    settingsPanelItem("appearance_text_color", settings, settingsContentBackground) { panelColors ->
                        SettingTitle(stringResource(R.string.mock_text_color), panelColors.text, fontFamily)
                        Spacer(Modifier.height(8.dp))
                        TextColorSelector(settings.textColor, viewModel::setTextColor, settings.font)
                    }

                    settingsPanelItem(
                        "appearance_text_outline", settings, settingsContentBackground, topSpacing = 12.dp,
                        contentPadding = PaddingValues(vertical = 0.dp)
                    ) { panelColors ->
                        SettingsToggleRow(
                            title = stringResource(R.string.text_black_outline),
                            description = stringResource(R.string.text_black_outline_description),
                            checked = settings.textOutlineEnabled,
                            colors = panelColors,
                            fontFamily = fontFamily,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            onCheckedChange = UiSoundPlayer.toggleHandler(context, actionBlock = viewModel::setTextOutlineEnabled)
                        )
                    }
                }
                settingsPanelItem("font", settings, settingsContentBackground, topSpacing = 20.dp, contentType = null) { panelColors ->
                    if (isAdvancedMode) {
                        val systemFontLabel = stringResource(R.string.mock_font_default)
                        val googleSansFlexLabel = stringResource(R.string.mock_font_google_sans_flex)
                        val serifLabel = stringResource(R.string.mock_font_serif)
                        val monospaceLabel = stringResource(R.string.mock_font_monospace)
                        val fontOptions = listOfNotNull(
                            FontPreferencePolicy.SYSTEM_DEFAULT to systemFontLabel,
                            (FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX to googleSansFlexLabel)
                                .takeIf { developerGoogleSansFlexUnlocked },
                            FontPreferencePolicy.SERIF to serifLabel,
                            FontPreferencePolicy.MONOSPACE to monospaceLabel
                        )
                        SettingDropdown(
                            title = stringResource(R.string.mock_font),
                            selectedLabel = when (settings.font) {
                                FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX -> googleSansFlexLabel
                                FontPreferencePolicy.SERIF -> serifLabel
                                FontPreferencePolicy.MONOSPACE -> monospaceLabel
                                else -> systemFontLabel
                            },
                            options = fontOptions, textColor = panelColors.text, textColorMode = settings.textColor,
                            menuBackground = menuBackground, menuTextColor = settingsMenuTextColor, fontFamily = fontFamily,
                            onSelected = viewModel::setFont
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                    LabeledSettingsSlider(
                        title = stringResource(R.string.mock_font_size), value = localFontSize,
                        valueLabel = "${localFontSize.roundToInt()} sp", textColor = panelColors.text, fontFamily = fontFamily,
                        onValueChange = { localFontSize = it }, onValueChangeFinished = { viewModel.setFontSize(localFontSize) },
                        valueRange = 12f..28f, style = if (isAdvancedMode) settings.sliderStyle else "capsule"
                    )
                }
                if (isAdvancedMode) {
                    settingsPanelItem("feedback_sound_effects", settings, settingsContentBackground, contentPadding = FeedbackSettingsPanelPadding) { colors ->
                        FeedbackSettingsPanel(
                            title = stringResource(R.string.sound_effects),
                            description = stringResource(R.string.sound_effects_description),
                            toggleTitle = stringResource(R.string.sound_effects_enabled),
                            checked = settings.soundEffectsEnabled,
                            dropdownTitle = stringResource(R.string.sound_effects_theme),
                            selectedKey = settings.soundEffectsTheme,
                            options = FeedbackPreferencePolicy.soundThemeResources.map { it.key to stringResource(it.labelRes) },
                            previewText = stringResource(R.string.sound_effects_preview),
                            sliderTitle = stringResource(R.string.sound_effects_volume),
                            sliderValue = localSoundEffectsVolume,
                            settings = settings,
                            colors = colors,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onCheckedChange = UiSoundPlayer.toggleHandler(context, force = true, actionBlock = viewModel::setSoundEffectsEnabled),
                            onSelected = { selectedTheme ->
                                UiHapticPlayer.play(context = context, haptic = UiHaptic.Selection)
                                viewModel.setSoundEffectsTheme(selectedTheme)
                                UiSoundPlayer.previewTheme(context, selectedTheme, UiSound.Edit, localSoundEffectsVolume)
                            },
                            onPreview = {
                                UiSoundPlayer.previewTheme(context, settings.soundEffectsTheme, UiSound.Attachment, localSoundEffectsVolume)
                            },
                            onSliderValueChange = { localSoundEffectsVolume = it },
                            onSliderValueChangeFinished = { viewModel.setSoundEffectsVolume(localSoundEffectsVolume) }
                        )
                    }
                    settingsPanelItem("feedback_alert_sounds", settings, settingsContentBackground, topSpacing = 16.dp,
                        contentPadding = FeedbackSettingsPanelPadding) { colors ->
                        FeedbackSettingsPanel(
                            title = stringResource(R.string.reminder_tones),
                            description = stringResource(R.string.reminder_tones_description),
                            toggleTitle = stringResource(R.string.reminder_sound_enabled),
                            checked = settings.reminderSoundEnabled,
                            dropdownTitle = stringResource(R.string.reminder_tone),
                            selectedKey = settings.reminderRingtone,
                            options = FeedbackPreferencePolicy.reminderTones.map { it.key to stringResource(it.labelRes) },
                            previewText = stringResource(R.string.reminder_tone_preview),
                            sliderTitle = stringResource(R.string.reminder_sound_volume),
                            sliderValue = localReminderSoundVolume,
                            settings = settings,
                            colors = colors,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            showControls = true,
                            onCheckedChange = { checked ->
                                UiSoundPlayer.playToggleAudioOnly(context = context, checked = checked)
                                UiHapticPlayer.playToggle(context = context, checked = checked)
                                viewModel.setReminderSoundEnabled(checked)
                            },
                            onSelected = { selectedRingtone ->
                                UiHapticPlayer.play(context = context, haptic = UiHaptic.Selection)
                                viewModel.setReminderRingtone(selectedRingtone)
                                ReminderFeedbackPreferences.previewRingtone(context, selectedRingtone, localReminderSoundVolume)
                            },
                            onPreview = {
                                ReminderFeedbackPreferences.previewRingtone(context, settings.reminderRingtone, localReminderSoundVolume)
                            },
                            onSliderValueChange = { localReminderSoundVolume = it },
                            onSliderValueChangeFinished = { viewModel.setReminderSoundVolume(localReminderSoundVolume) }
                        )
                    }
                    settingsPanelItem("feedback_vibration", settings, settingsContentBackground, topSpacing = 16.dp,
                        contentPadding = FeedbackSettingsPanelPadding) { colors ->
                        FeedbackSettingsPanel(
                            title = stringResource(R.string.haptic_effects),
                            description = stringResource(R.string.haptic_effects_description),
                            toggleTitle = stringResource(R.string.haptic_effects_enabled),
                            checked = settings.hapticEffectsEnabled,
                            dropdownTitle = stringResource(R.string.haptic_effects_style),
                            selectedKey = settings.hapticEffectsStyle,
                            options = FeedbackPreferencePolicy.hapticStyles.map { it.key to stringResource(it.labelRes) },
                            previewText = stringResource(R.string.haptic_effects_preview),
                            sliderTitle = stringResource(R.string.haptic_effects_intensity),
                            sliderValue = localHapticEffectsIntensity,
                            settings = settings,
                            colors = colors,
                            menuBackground = menuBackground,
                            menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onCheckedChange = { checked ->
                                UiSoundPlayer.playToggleAudioOnly(context = context, checked = checked)
                                UiHapticPlayer.playToggle(context = context, checked = checked, force = true)
                                viewModel.setHapticEffectsEnabled(checked)
                            },
                            onSelected = { selectedStyle ->
                                UiSoundPlayer.playActionAudioOnly(context = context, action = UiActionSound.Select)
                                viewModel.setHapticEffectsStyle(selectedStyle)
                                UiHapticPlayer.previewStyle(context, selectedStyle, localHapticEffectsIntensity, UiHaptic.Confirm)
                            },
                            onPreview = {
                                UiSoundPlayer.playActionAudioOnly(context = context, action = UiActionSound.PlayPause)
                                UiHapticPlayer.previewStyle(context, settings.hapticEffectsStyle, localHapticEffectsIntensity, UiHaptic.Confirm)
                            },
                            onSliderValueChange = { localHapticEffectsIntensity = it },
                            onSliderValueChangeFinished = {
                                viewModel.setHapticEffectsIntensity(localHapticEffectsIntensity)
                                UiHapticPlayer.previewStyle(context, settings.hapticEffectsStyle, localHapticEffectsIntensity, UiHaptic.Tick)
                            }
                        )
                    }
                }
                settingsPanelItem("language", settings, settingsContentBackground, topSpacing = 18.dp, contentType = null) { panelColors ->
                    SettingDropdown(
                        title = stringResource(R.string.mock_language),
                        selectedLabel = when (settings.language) {
                            "system" -> stringResource(R.string.language_system)
                            "en" -> "English"
                            "fr" -> "Français"
                            "zh-CN" -> "中文（简体）"
                            else -> "Español"
                        },
                        options = listOf(
                            "system" to stringResource(R.string.language_system), "es" to "Español", "en" to "English",
                            "fr" to "Français", "zh-CN" to "中文（简体）"
                        ),
                        textColor = panelColors.text, textColorMode = settings.textColor, menuBackground = menuBackground,
                        menuTextColor = settingsMenuTextColor, fontFamily = fontFamily, onSelected = onLanguageChange
                    )
                }
                if (isAdvancedMode) {
                    settingsPanelItem("layout_columns", settings, settingsContentBackground) { panelColors ->
                        SettingDropdown(
                            title = stringResource(R.string.mock_columns), selectedLabel = settings.gridColumns.toString(),
                            options = listOf("1" to "1", "2" to "2", "3" to "3"), textColor = panelColors.text,
                            textColorMode = settings.textColor, menuBackground = menuBackground, menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily,
                            onSelected = { viewModel.setGridColumns(it.toIntOrNull()?.coerceIn(1, 3) ?: 2) }
                        )
                    }

                    settingsPanelItem("layout_slider_design", settings, settingsContentBackground) { panelColors ->
                        KeyedSettingDropdown(
                            title = stringResource(R.string.mock_slider_style), selectedKey = settings.sliderStyle,
                            options = SliderStyleOptions.map { it.key to stringResource(it.labelRes) }, textColor = panelColors.text,
                            textColorMode = settings.textColor, menuBackground = menuBackground, menuTextColor = settingsMenuTextColor,
                            fontFamily = fontFamily, onSelected = viewModel::setSliderStyle
                        )
                    }

                    settingsPanelItem("layout_dark_mode", settings, settingsContentBackground, topSpacing = 17.dp) { panelColors ->
                        SettingsToggleRow(
                            title = stringResource(R.string.mock_dark_mode),
                            description = stringResource(R.string.mock_dark_mode_description),
                            checked = settings.darkMode,
                            colors = panelColors,
                            fontFamily = fontFamily,
                            onCheckedChange = UiSoundPlayer.toggleHandler(context, actionBlock = viewModel::setDarkMode)
                        )
                    }

                    settingsSurfaceItem(key = "layout_backup_restore", contentType = "settings_panel", backgroundColor = settingsContentBackground) {
                            Spacer(modifier = Modifier.height(18.dp))
                            BackupRestoreSection(settings = settings, fontFamily = fontFamily, textColor = settingsTextColor,
                                secondaryTextColor = settingsSecondaryTextColor, graphicColor = settingsGraphicColor)
                        }
                }
                settingsPanelItem(
                    "development", settings, settingsContentBackground, topSpacing = 22.dp, contentType = null,
                    contentPadding = PaddingValues(0.dp)
                ) { panelColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                            onOpenDevelopmentInfo()
                        }.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = panelColors.graphic, modifier = Modifier.size(23.dp))
                        Column(Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {
                            SettingsTitle(stringResource(R.string.development_info_settings_title), panelColors.text, fontFamily,
                                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            SettingsSecondaryText(stringResource(R.string.development_info_settings_description), panelColors.secondaryText, fontFamily, Modifier.padding(top = 2.dp))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = panelColors.graphic, modifier = Modifier.size(20.dp))
                    }
                }
                item(key = "updates") {
                    SettingsContentSurface(backgroundColor = settingsContentBackground,
                        shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp),
                        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp)) {
                            Spacer(modifier = Modifier.height(14.dp))
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
                                            SettingsTitle(stringResource(R.string.update_settings_title), panelColors.text, fontFamily,
                                                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                            SettingsSecondaryText(stringResource(R.string.update_settings_description), panelColors.secondaryText, fontFamily, Modifier.padding(top = 2.dp))
                                        }
                                    }

                                    SettingsSecondaryText(stringResource(R.string.update_current_version, installedVersion), panelColors.secondaryText, fontFamily, Modifier.padding(top = 10.dp))

                                    Button(
                                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
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
                                        UpdateActionContent(
                                            checkingForUpdate,
                                            stringResource(if (checkingForUpdate) R.string.update_checking else R.string.update_check),
                                            fontFamily
                                        )
                                    }

                                    when (val result = updateResult) {
                                        is GitHubUpdateManager.CheckResult.UpdateAvailable -> {
                                            SettingsTitle(stringResource(R.string.update_available, result.release.version), panelColors.text, fontFamily,
                                                modifier = Modifier.padding(top = 12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            if (result.release.title.isNotBlank() && result.release.title != result.release.version) {
                                                SettingsSecondaryText(result.release.title, panelColors.secondaryText, fontFamily, Modifier.padding(top = 2.dp))
                                            }
                                            if (result.release.apkDownloadUrl != null) {
                                                Button(
                                                    onClick = { requestDownloadAndInstall(result.release) },
                                                    enabled = !downloadingUpdate,
                                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                                    shape = RoundedCornerShape(14.dp)
                                                ) {
                                                    UpdateActionContent(
                                                        downloadingUpdate,
                                                        stringResource(if (downloadingUpdate) R.string.update_downloading else R.string.update_download_install),
                                                        fontFamily
                                                    )
                                                }
                                            } else {
                                                SettingsSecondaryText(stringResource(R.string.update_release_without_apk), panelColors.secondaryText, fontFamily, Modifier.padding(top = 6.dp))
                                            }
                                            AppTextButton(stringResource(R.string.update_open_release),
                                                onClick = { GitHubUpdateManager.openReleasePage(context, result.release.htmlUrl) },
                                                modifier = Modifier.fillMaxWidth(), fontFamily = fontFamily)
                                        }

                                        is GitHubUpdateManager.CheckResult.UpToDate -> SettingsSecondaryText(
                                            stringResource(R.string.update_up_to_date, result.latestVersion), panelColors.secondaryText, fontFamily, Modifier.padding(top = 10.dp)
                                        )

                                        is GitHubUpdateManager.CheckResult.NoPublishedRelease -> {
                                            SettingsSecondaryText(stringResource(R.string.update_no_release), panelColors.secondaryText, fontFamily, Modifier.padding(top = 10.dp))
                                            AppTextButton(stringResource(R.string.update_open_release),
                                                onClick = { GitHubUpdateManager.openReleasePage(context) }, modifier = Modifier.fillMaxWidth(),
                                                fontFamily = fontFamily)
                                        }

                                        is GitHubUpdateManager.CheckResult.Failure -> SettingsSecondaryText(stringResource(R.string.update_error, result.reason), MaterialTheme.colorScheme.error, fontFamily, modifier = Modifier.padding(top = 10.dp))

                                        null -> Unit
                                    }

                                    updateActionMessage?.let { message ->
                                        SettingsSecondaryText(message, MaterialTheme.colorScheme.error, fontFamily, modifier = Modifier.padding(top = 8.dp))
                                    }
                                }
                            }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 2.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            ScrollPositionCapsule(
                state = settingsListState,
                backgroundColor = MaterialTheme.colorScheme.background,
                preferredColor = MaterialTheme.colorScheme.onBackground
            )
        }
        }
    }
    }
    }
    }

@Composable
private fun rememberSyncedFloatState(value: Float): MutableFloatState =
    remember(value) { mutableFloatStateOf(value) }

@Composable
private fun SettingsFeedbackHeader(
    title: String, description: String, toggleTitle: String, checked: Boolean,
    colors: SettingsSectionColors, fontFamily: FontFamily, onCheckedChange: (Boolean) -> Unit
) {
    SettingsTitle(title, colors.text, fontFamily,
        fontSize = 16.sp)
    SettingsSecondaryText(description, colors.secondaryText, fontFamily, Modifier.padding(top = 2.dp, bottom = 8.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = toggleTitle, color = colors.text, fontFamily = fontFamily, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun FeedbackSettingsPanel(
    title: String,
    description: String,
    toggleTitle: String,
    checked: Boolean,
    dropdownTitle: String,
    selectedKey: String,
    options: List<Pair<String, String>>,
    previewText: String,
    sliderTitle: String,
    sliderValue: Float,
    settings: AppSettings,
    colors: SettingsSectionColors,
    menuBackground: Color,
    menuTextColor: Color,
    fontFamily: FontFamily,
    showControls: Boolean = checked,
    onCheckedChange: (Boolean) -> Unit,
    onSelected: (String) -> Unit,
    onPreview: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit
) {
    SettingsFeedbackHeader(title, description, toggleTitle, checked, colors, fontFamily, onCheckedChange)
    if (!showControls) return
    Spacer(Modifier.height(8.dp))
    KeyedSettingDropdown(
        title = dropdownTitle, selectedKey = selectedKey, options = options, textColor = colors.text,
        textColorMode = settings.textColor, menuBackground = menuBackground, menuTextColor = menuTextColor,
        fontFamily = fontFamily, playDefaultSelectionFeedback = false, onSelected = onSelected
    )
    Spacer(Modifier.height(PreviewButtonVerticalGap))
    RoundedPreviewButton(
        text = previewText, panelBackground = colors.background, textColorMode = settings.textColor,
        fontFamily = fontFamily, onClick = onPreview
    )
    Spacer(Modifier.height(PreviewToSliderGap))
    LabeledSettingsSlider(
        title = sliderTitle, value = sliderValue, valueLabel = "${sliderValue.roundToInt()}%", textColor = colors.text,
        fontFamily = fontFamily, onValueChange = onSliderValueChange, onValueChangeFinished = onSliderValueChangeFinished,
        valueRange = 0f..100f, steps = 19, style = settings.sliderStyle
    )
}

@Composable
private fun UpdateActionContent(loading: Boolean, label: String, fontFamily: FontFamily) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(17.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
    Text(label, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    colors: SettingsSectionColors,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            SettingsTitle(title, colors.text, fontFamily,
                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            SettingsSecondaryText(description, colors.secondaryText, fontFamily, Modifier.padding(top = 2.dp))
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsContentSurface(
    backgroundColor: Color,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(0.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = shape, color = backgroundColor, tonalElevation = 1.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(contentPadding), content = content)
    }
}

private fun LazyListScope.settingsSurfaceItem(
    key: String, backgroundColor: Color, contentType: Any? = "settings_panel",
    bottomSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    item(key = key, contentType = contentType) {
        SettingsContentSurface(backgroundColor = backgroundColor) {
            content()
            if (bottomSpacing > 0.dp) Spacer(Modifier.height(bottomSpacing))
        }
    }
}

private fun LazyListScope.settingsPanelItem(
    key: String,
    settings: AppSettings,
    backgroundColor: Color,
    topSpacing: androidx.compose.ui.unit.Dp = 14.dp,
    contentType: Any? = "settings_panel",
    contentPadding: PaddingValues = StandardSettingsPanelPadding,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.(SettingsSectionColors) -> Unit
) {
    settingsSurfaceItem(key = key, contentType = contentType, backgroundColor = backgroundColor) {
        Spacer(Modifier.height(topSpacing))
        SettingsSectionPanel(
            textColorMode = settings.textColor,
            contentPadding = contentPadding,
            horizontalOutset = 8.dp,
            content = content
        )
    }
}

private fun LazyListScope.percentSliderItem(
    key: String, titleRes: Int, value: Float, settings: AppSettings, backgroundColor: Color, fontFamily: FontFamily,
    onValueChange: (Float) -> Unit, onValueChangeFinished: () -> Unit, descriptionRes: Int? = null
) {
    settingsSurfaceItem(key = key, contentType = "settings_panel", backgroundColor = backgroundColor) {
        Spacer(modifier = Modifier.height(8.dp))
        SettingsSectionPanel(
            textColorMode = settings.textColor,
            contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 0.dp, bottom = 14.dp),
            horizontalOutset = 8.dp
        ) { colors ->
            LabeledSettingsSlider(
                title = stringResource(titleRes), value = value, valueLabel = "${value.roundToInt()}%", textColor = colors.text,
                fontFamily = fontFamily, onValueChange = onValueChange, onValueChangeFinished = onValueChangeFinished,
                valueRange = 0f..100f, style = settings.sliderStyle
            )
            if (descriptionRes != null) {
                SettingsSecondaryText(stringResource(descriptionRes), colors.secondaryText, fontFamily, Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Composable
private fun LabeledSettingsSlider(
    title: String, value: Float, valueLabel: String, textColor: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    onValueChange: (Float) -> Unit, onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float>, style: String, steps: Int = 0
) {
    SettingTitleRow(title = title, value = valueLabel, color = textColor, fontFamily = fontFamily)
    StyledSettingsSlider(value = value, onValueChange = onValueChange, onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange, steps = steps, activeColor = MaterialTheme.colorScheme.primary,
        inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), style = style, valueLabel = valueLabel)
}

@Composable
private fun ProfileSecondaryAction(
    label: String, icon: ImageVector, fontFamily: FontFamily, contentColor: Color,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    TextButton(
        onClick = onClick, modifier = modifier.heightIn(min = 48.dp), shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp)
    }
}

@Composable
private fun ConfigurationModeCard(
    title: String, description: String, isSelected: Boolean,
    containerColor: Color, contentColor: Color, fontFamily: FontFamily,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { selected = isSelected; role = Role.RadioButton },
        shape = RoundedCornerShape(16.dp), color = containerColor, contentColor = contentColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp,
            contentColor.copy(alpha = if (isSelected) 0.65f else 0.20f)),
        tonalElevation = 0.dp, shadowElevation = 0.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, modifier = Modifier.weight(1f), color = contentColor, fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp)
                Surface(
                    modifier = Modifier.size(20.dp), shape = CircleShape,
                    color = if (isSelected) contentColor else Color.Transparent,
                    border = BorderStroke(1.dp, contentColor.copy(alpha = if (isSelected) 1f else 0.55f))
                ) {
                    if (isSelected) Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = containerColor,
                            modifier = Modifier.size(14.dp))
                    }
                }
            }
            SettingsSecondaryText(description, contentColor, fontFamily,
                Modifier.padding(top = 8.dp), lineHeight = 16.sp)
        }
    }
}

@Composable
private fun ProfileAvatarPreview(
    profileSize: () -> Float, model: Any?, hasPhoto: Boolean, profileLabel: String,
    actionLabel: String, panelColors: SettingsSectionColors, placeholderColor: Color,
    onClick: () -> Unit
) {
    val size = profileSize().coerceIn(36f, 84f)
    Surface(
        modifier = Modifier.size(size.dp + 12.dp).clickable(onClickLabel = actionLabel, onClick = onClick),
        shape = CircleShape, color = panelColors.background,
        border = BorderStroke(1.5.dp, panelColors.graphic.copy(alpha = 0.35f))
    ) {
        Surface(modifier = Modifier.fillMaxSize().padding(6.dp), shape = CircleShape, color = placeholderColor) {
            if (hasPhoto) {
                AsyncImage(model = model, contentDescription = profileLabel,
                    modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = profileLabel,
                        modifier = Modifier.size((size * 0.46f).coerceIn(20f, 38f).dp), tint = panelColors.text)
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatarSizeControl(
    profileSize: () -> Float, textColor: Color, fontFamily: FontFamily, style: String,
    onValueChange: (Float) -> Unit, onValueChangeFinished: () -> Unit
) {
    val size = profileSize()
    LabeledSettingsSlider(
        title = stringResource(R.string.extreme_profile_size), value = size,
        valueLabel = "${size.roundToInt()} dp", textColor = textColor, fontFamily = fontFamily,
        onValueChange = onValueChange, onValueChangeFinished = onValueChangeFinished,
        valueRange = 36f..84f, steps = 23, style = style
    )
}

@Composable
private fun ProfileAndModePanel(
    settings: AppSettings,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    isAdvancedMode: Boolean,
    profileSize: () -> Float,
    onProfileSizeValueChange: (Float) -> Unit,
    onConfigurationModeChange: (String) -> Unit,
    onProfileImageUriChange: (String) -> Unit,
    onProfileImageSizeChange: () -> Unit,
) {
    val context = LocalContext.current
    val layoutPreview = LocalLayoutPreview.current
    val avatarPixels = with(LocalDensity.current) { 84.dp.roundToPx() }
    val avatarModel = remember(context, settings.profileImageUri, avatarPixels, layoutPreview?.mediaBitmap) {
        if (layoutPreview?.mediaBitmap != null) layoutPreview.mediaBitmap
        else if (settings.profileImageUri.isNotBlank()) {
            ImageRequest.Builder(context).data(Uri.parse(settings.profileImageUri)).size(avatarPixels).build()
        } else null
    }
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
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryButtonBase = MaterialTheme.colorScheme.surfaceContainerHigh
        val modeSelectedColors = rememberAdaptiveUiButtonColors(
            primaryColor, panelColors.background, settings.textColor, minimumSurfaceContrast = 1.85f)
        val modeSelectedContainerColor = modeSelectedColors.container
        val modeSelectedContentColor = modeSelectedColors.content
        val modeUnselectedColors = rememberAdaptiveUiButtonColors(
            secondaryButtonBase, panelColors.background, settings.textColor, minimumSurfaceContrast = 1.35f)
        val modeUnselectedContainerColor = modeUnselectedColors.container
        val modeUnselectedContentColor = modeUnselectedColors.content
        val hasPhoto = settings.profileImageUri.isNotBlank()
        val profileLabel = stringResource(R.string.extreme_profile)
        val photoActionLabel = stringResource(if (hasPhoto) R.string.extreme_edit_photo else R.string.extreme_change_photo)
        val largeText = LocalDensity.current.fontScale > 1.3f
        val avatar: @Composable () -> Unit = {
            ProfileAvatarPreview(
                profileSize = profileSize, model = avatarModel, hasPhoto = hasPhoto, profileLabel = profileLabel,
                actionLabel = photoActionLabel, panelColors = panelColors, placeholderColor = secondaryButtonBase,
                onClick = {
                    if (hasPhoto) {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                        profileEditorUri = managedProfileSourceUri(context, settings.profileImageUri)
                            ?: Uri.parse(settings.profileImageUri)
                    } else {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                        picker.launch(arrayOf("image/*"))
                    }
                }
            )
        }
        val photoActions: @Composable () -> Unit = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                        picker.launch(arrayOf("image/*"))
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = modeSelectedContainerColor,
                        contentColor = modeSelectedContentColor)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.extreme_change_photo), fontFamily = fontFamily,
                        fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                if (hasPhoto) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ProfileSecondaryAction(stringResource(R.string.settings_profile_edit_short), Icons.Default.Crop,
                            fontFamily, panelColors.text, modifier = Modifier.weight(1f)) {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                            profileEditorUri = managedProfileSourceUri(context, settings.profileImageUri) ?: Uri.parse(settings.profileImageUri)
                        }
                        ProfileSecondaryAction(stringResource(R.string.settings_profile_remove_short), Icons.Default.DeleteOutline,
                            fontFamily, panelColors.text, modifier = Modifier.weight(1f)) {
                            UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                            clearManagedProfileImages(context)
                            onProfileImageUriChange("")
                        }
                    }
                }
            }
        }
        SettingsTitle(profileLabel, panelColors.text, fontFamily, fontSize = 17.sp)
        Spacer(Modifier.height(14.dp))
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth < 260.dp || largeText) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) { avatar() }
                    photoActions()
                }
            } else {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) { avatar() }
                    Column(Modifier.weight(1f)) { photoActions() }
                }
            }
        }
        if (isAdvancedMode) {
            Spacer(modifier = Modifier.height(16.dp))
            ProfileAvatarSizeControl(
                profileSize = profileSize, textColor = panelColors.text, fontFamily = fontFamily,
                style = settings.sliderStyle,
                onValueChange = onProfileSizeValueChange,
                onValueChangeFinished = onProfileImageSizeChange
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = panelColors.graphic.copy(alpha = 0.16f))
        SettingsTitle(stringResource(R.string.configuration_mode_settings_title), panelColors.text, fontFamily,
            fontSize = 15.sp)
        Spacer(Modifier.height(12.dp))
        val modes = listOf(
            Triple("basic", R.string.configuration_mode_basic, R.string.settings_mode_basic_hint),
            Triple("advanced", R.string.configuration_mode_advanced, R.string.settings_mode_advanced_hint)
        )
        val modeCard: @Composable (Triple<String, Int, Int>, Modifier) -> Unit = { (mode, labelRes, hintRes), modifier ->
            val isSelected = settings.configurationMode == mode
            ConfigurationModeCard(
                title = stringResource(labelRes), description = stringResource(hintRes), isSelected = isSelected,
                containerColor = if (isSelected) modeSelectedContainerColor else modeUnselectedContainerColor,
                contentColor = if (isSelected) modeSelectedContentColor else modeUnselectedContentColor,
                fontFamily = fontFamily, modifier = modifier,
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Select) {
                    onConfigurationModeChange(mode)
                }
            )
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth < 260.dp || largeText) {
                Column(Modifier.fillMaxWidth().selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    modes.forEach { modeCard(it, Modifier.fillMaxWidth()) }
                }
            } else {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    modes.forEach { modeCard(it, Modifier.weight(1f).fillMaxHeight()) }
                }
            }
        }
    }
}

@Composable
private fun SettingTitle(text: String, color: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily) {
    SettingsTitle(text, color, fontFamily,
        fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
}

@Composable
private fun RoundedPreviewButton(
    text: String,
    panelBackground: Color,
    textColorMode: String,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val resolvedButtonColors = rememberAdaptiveUiButtonColors(primaryColor, panelBackground, textColorMode)
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
        SettingsTitle(text, buttonContentColor, fontFamily,
            fontWeight = FontWeight.SemiBold)
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
        SettingsSecondaryText(value, color, fontFamily)
    }
}

@Composable
private fun TextColorSelector(selected: String, onSelected: (String) -> Unit, fontKey: String) {
    val context = LocalContext.current
    val selectorFontFamily = remember(fontKey) { appFontFamily(fontKey) }
    val autoLabel = stringResource(R.string.mock_auto)
    val blackLabel = stringResource(R.string.mock_black)
    val whiteLabel = stringResource(R.string.mock_white)
    val automaticSampleColor = MaterialTheme.colorScheme.onSurface
    val options = remember(autoLabel, blackLabel, whiteLabel, automaticSampleColor) {
        listOf(
            Triple("auto", autoLabel, automaticSampleColor),
            Triple("black", blackLabel, Color.Black),
            Triple("white", whiteLabel, Color.White)
        )
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { (key, label, sampleColor) ->
            TextColorButton(
                modifier = Modifier.weight(1f), label = label, sampleColor = sampleColor, selected = selected == key,
                fontFamily = selectorFontFamily,
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Select) { onSelected(key) }
            )
        }
    }
}

@Composable
private fun TextColorButton(modifier: Modifier, label: String, sampleColor: Color, selected: Boolean, fontFamily:
        androidx.compose.ui.text.font.FontFamily, onClick: () -> Unit) {
    val buttonBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val buttonOutline = MaterialTheme.colorScheme.outline
    val (buttonContentColor, buttonBorderColor) = remember(buttonBackground, buttonOutline) {
        resolveUiTextColor(value = "auto", background = buttonBackground) to
            ensureUiContrast(preferred = buttonOutline, background = buttonBackground, minimumContrast = 3f)
    }
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
private fun KeyedSettingDropdown(
    title: String, selectedKey: String, options: List<Pair<String, String>>, textColor: Color,
    textColorMode: String, menuBackground: Color, menuTextColor: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    playDefaultSelectionFeedback: Boolean = true, onSelected: (String) -> Unit
) {
    val selectedLabel = remember(selectedKey, options) {
        (options.firstOrNull { it.first == selectedKey } ?: options.first()).second
    }
    SettingDropdown(title = title, selectedLabel = selectedLabel, options = options, textColor = textColor,
        textColorMode = textColorMode, menuBackground = menuBackground, menuTextColor = menuTextColor,
        fontFamily = fontFamily, playDefaultSelectionFeedback = playDefaultSelectionFeedback, onSelected = onSelected)
}

@Composable
private fun SettingDropdown(title: String, selectedLabel: String, options: List<Pair<String, String>>, textColor: Color,
    textColorMode: String, menuBackground: Color, menuTextColor: Color, fontFamily:
        androidx.compose.ui.text.font.FontFamily, playDefaultSelectionFeedback: Boolean = true, onSelected: (String) -> Unit) {
    val context = LocalContext.current
    val layoutPreview = LocalLayoutPreview.current
    var expanded by remember { mutableStateOf(title == layoutPreview?.expandedDropdownTitle) }
    val dropdownButtonBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val dropdownOutline = MaterialTheme.colorScheme.outline
    val dropdownColors = remember(textColorMode, dropdownButtonBackground, dropdownOutline) {
        Triple(
            resolveUiTextColor(value = textColorMode, background = dropdownButtonBackground),
            com.example.mynotes.ui.theme.resolveUiGraphicColor(value = textColorMode, background = dropdownButtonBackground),
            ensureUiContrast(preferred = dropdownOutline, background = dropdownButtonBackground, minimumContrast = 3f)
        )
    }
    val dropdownButtonTextColor = dropdownColors.first
    val dropdownButtonGraphicColor = dropdownColors.second
    val dropdownButtonBorderColor = dropdownColors.third
    val (dropdownWidth, compactMenuWidth) = remember(selectedLabel, options) {
        val buttonWidth = when {
            selectedLabel.length <= 3 -> 112.dp
            selectedLabel.length <= 7 -> 132.dp
            selectedLabel.length <= 11 -> 154.dp
            selectedLabel.length <= 16 -> 188.dp
            else -> 228.dp
        }
        val longestOptionLength = maxOf(selectedLabel.length, options.maxOfOrNull { it.second.length } ?: 0)
        val menuWidth = when {
            longestOptionLength <= 6 -> 112.dp
            longestOptionLength <= 10 -> 132.dp
            longestOptionLength <= 14 -> 150.dp
            longestOptionLength <= 18 -> 170.dp
            else -> 194.dp
        }
        buttonWidth to menuWidth
    }
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        SettingsTitle(title, textColor, fontFamily,
            modifier = Modifier.weight(0.36f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Box(modifier = Modifier.weight(0.64f),
            contentAlignment = Alignment.CenterEnd) {
            Surface(modifier = Modifier.width(dropdownWidth),
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
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
                containerColor = menuBackground) {
                options.forEach {
                            option ->
                        SettingsDropdownItem(
                            label = option.second,
                            textColor = menuTextColor, fontFamily = fontFamily,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            onClick = {
                                if (playDefaultSelectionFeedback) {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                                }
                                expanded = false
                                onSelected(option.first)
                            }
                        )
                    }
            }
        }
    }
}
