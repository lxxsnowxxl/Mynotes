package com.example.mynotes.ui.pdf

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.resolveUiTextColor
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class PdfLibraryActivity : ComponentActivity() {
    private val refreshSignal = mutableIntStateOf(0)

    companion object {
        private const val LOCALE_PREFS = "locale_prefs"
        private const val LANGUAGE_KEY = "language"
        private const val DEFAULT_LANGUAGE = "system"
    }

    override fun attachBaseContext(newBase: Context) {
        val preferences = newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        if (language == DEFAULT_LANGUAGE) {
            super.attachBaseContext(newBase)
            return
        }
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideAndroidNavigationBar()
        window.decorView.post { hideAndroidNavigationBar() }

        setContent {
            val repository = remember { SettingsRepository(applicationContext) }
            val settings by repository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val effectiveDark = if (settings.configurationMode == "advanced") settings.darkMode else systemDark

            /*
             * Mantiene esta Window sincronizada con el mismo perfil de
             * rendimiento que el resto de MyNotes. Compose renderiza con
             * Choreographer/VSYNC; aquí solo solicitamos 60 o hasta 120 Hz
             * según el modo guardado.
             */
            LaunchedEffect(settings.performanceMode) {
                DisplayPerformanceController.requestForPerformanceMode(
                    window = window,
                    performanceMode = settings.performanceMode
                )
            }

            LaunchedEffect(
                settings.soundEffectsEnabled,
                settings.soundEffectsVolume,
                settings.soundEffectsTheme,
                settings.hapticEffectsEnabled,
                settings.hapticEffectsIntensity,
                settings.hapticEffectsStyle
            ) {
                UiSoundPlayer.configure(
                    context = this@PdfLibraryActivity,
                    enabled = settings.soundEffectsEnabled,
                    volumePercent = settings.soundEffectsVolume,
                    theme = settings.soundEffectsTheme,
                    hapticEnabled = settings.hapticEffectsEnabled,
                    hapticIntensityPercent = settings.hapticEffectsIntensity,
                    hapticStyle = settings.hapticEffectsStyle
                )
            }

            MyNotesTheme(
                darkTheme = effectiveDark,
                backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                textColor = settings.textColor,
                textOutlineEnabled = settings.textOutlineEnabled,
                accentColor = settings.accentColor
            ) {
                val handleBack: () -> Unit = {
                    // Usa el mismo feedback global configurado en Settings.
                    // playAction reproduce tanto el efecto de sonido como el
                    // háptico correspondiente (si cada opción está habilitada).
                    UiSoundPlayer.playAction(this@PdfLibraryActivity, UiActionSound.Back)
                    finish()
                }

                BackHandler(onBack = handleBack)

                PdfLibraryScreen(
                    settings = settings,
                    refreshSignal = refreshSignal.intValue,
                    onBack = handleBack,
                    onNew = {
                        UiSoundPlayer.playAction(this, UiActionSound.Add)
                        startActivity(Intent(this, PdfEditorActivity::class.java))
                    },
                    onOpen = { projectId ->
                        UiSoundPlayer.playAction(this, UiActionSound.Open)
                        startActivity(
                            Intent(this, PdfEditorActivity::class.java)
                                .putExtra(PdfEditorActivity.EXTRA_PROJECT_ID, projectId)
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DisplayPerformanceController.reapplyLastRequest(window)
        hideAndroidNavigationBar()
        window.decorView.post { hideAndroidNavigationBar() }
        refreshSignal.intValue++
    }

    override fun onDestroy() {
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideAndroidNavigationBar()
    }

    private fun hideAndroidNavigationBar() {
        val decorView = window.decorView
        val controller = WindowCompat.getInsetsController(window, decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility =
            decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = AndroidColor.BLACK
        }
    }
}

@Composable
private fun PdfLibraryScreen(
    settings: AppSettings,
    refreshSignal: Int,
    onBack: () -> Unit,
    onNew: () -> Unit,
    onOpen: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val screenBackground = MaterialTheme.colorScheme.background
    val primaryText = resolveUiTextColor(settings.textColor, screenBackground)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)

    // La cabecera forma parte del fondo principal de Mis PDF. La separación visual
    // se reserva para las tarjetas editables, que necesitan distinguirse con claridad
    // en cualquier paleta sin convertir el título en otra tarjeta.
    val cardContrastOverlay = if (screenBackground.luminance() > 0.5f) {
        Color.Black.copy(alpha = 0.14f)
    } else {
        Color.White.copy(alpha = 0.17f)
    }
    val baseProjectCardColor = compositeUiColor(cardContrastOverlay, screenBackground)

    /*
     * En modo Automático la tarjeta se adapta al color de texto que ya resolvió
     * la pantalla. Si Automático está usando negro (tonos claros), aclaramos la
     * tarjeta en vez de oscurecerla; así el nombre, páginas, fecha y menú
     * conservan contraste real. Si Automático usa blanco se mantiene el fondo
     * anterior. Los modos Negro/Blanco manuales no cambian su fondo.
     */
    val automaticProjectText = if (settings.textColor == "auto") {
        if (primaryText.luminance() < 0.5f) Color.Black else Color.White
    } else {
        null
    }
    val projectCardColor = if (automaticProjectText == Color.Black) {
        // El fondo conserva el matiz de la paleta, pero se acerca claramente a
        // blanco para que las tarjetas PDF no queden gris oscuro bajo texto negro.
        compositeUiColor(Color.White.copy(alpha = 0.52f), screenBackground)
    } else {
        baseProjectCardColor
    }
    val projectCardText = automaticProjectText ?: resolveUiTextColor(settings.textColor, projectCardColor)
    val projectCardSecondaryText = resolveSecondaryUiTextColor(settings.textColor, projectCardColor)

    /*
     * Si la tarjeta termina usando texto negro, añadimos una base muy clara casi
     * blanca detrás de los textos principales para reforzar todavía más el contraste
     * sin romper la paleta. Cuando el texto es blanco, no se aplica este recuadro.
     */
    val blackTextBadgeEnabled = projectCardText.luminance() < 0.18f
    val blackTextBadgeBackground = if (blackTextBadgeEnabled) {
        Color(0xFFF8F8F6)
    } else {
        Color.Transparent
    }
    val blackTextBadgeBorder = if (blackTextBadgeEnabled) {
        ensureUiContrast(
            preferred = Color(0xFFD9D6CF),
            background = blackTextBadgeBackground,
            minimumContrast = 1.2f
        )
    } else {
        Color.Transparent
    }
    val secondaryTextBadgeEnabled = projectCardSecondaryText.luminance() < 0.42f
    val secondaryTextBadgeBackground = if (secondaryTextBadgeEnabled) {
        Color(0xFFF8F8F6)
    } else {
        Color.Transparent
    }
    val secondaryTextBadgeBorder = if (secondaryTextBadgeEnabled) {
        ensureUiContrast(
            preferred = Color(0xFFD9D6CF),
            background = secondaryTextBadgeBackground,
            minimumContrast = 1.2f
        )
    } else {
        Color.Transparent
    }
    val projectCardBorder = ensureUiContrast(
        preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.78f),
        background = projectCardColor,
        minimumContrast = 2.55f
    )

    val headerText = primaryText
    val headerSecondaryText = secondaryText
    val infoChipColor = compositeUiColor(
        MaterialTheme.colorScheme.primary.copy(alpha = if (screenBackground.luminance() > 0.5f) 0.13f else 0.21f),
        screenBackground
    )
    val infoChipText = resolveUiTextColor(settings.textColor, infoChipColor)

    // El menú de PDFs utiliza exactamente la misma lógica visual/configurable que
    // el menú de opciones de las notas: color del menú, opacidad, texto e iconos.
    val effectivePopupTextColorMode = if (settings.optionMenuTextColor == "note") settings.textColor else settings.optionMenuTextColor
    val defaultPopupSurface = MaterialTheme.colorScheme.surfaceContainerHigh
    val inversePopupSurface = MaterialTheme.colorScheme.inverseSurface
    val popupBaseColor = when (effectivePopupTextColorMode) {
        "white" -> if (defaultPopupSurface.luminance() < 0.46f) defaultPopupSurface else inversePopupSurface
        "black" -> if (defaultPopupSurface.luminance() > 0.54f) defaultPopupSurface else inversePopupSurface
        else -> defaultPopupSurface
    }
    val popupAlpha = (settings.optionMenuOpacity / 100f).coerceIn(0.35f, 1f)
    val popupColor = popupBaseColor.copy(alpha = popupAlpha)
    val popupVisualBackground = compositeUiColor(popupColor, projectCardColor)
    val popupTextColor = resolveUiTextColor(effectivePopupTextColorMode, popupVisualBackground)
    var projects by remember { mutableStateOf<List<PdfProjectRepository.Summary>>(emptyList()) }
    var deleteTarget by remember { mutableStateOf<PdfProjectRepository.Summary?>(null) }
    var renameTarget by remember { mutableStateOf<PdfProjectRepository.Summary?>(null) }
    var renameText by remember { mutableStateOf("") }
    var menuProjectId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(refreshSignal) {
        projects = PdfProjectRepository.listProjects(context)
    }

    renameTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text(stringResource(R.string.pdf_library_rename_title), fontFamily = fontFamily) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.pdf_library_name), fontFamily = fontFamily) }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        PdfProjectRepository.renameProject(context, target.id, renameText)
                        projects = PdfProjectRepository.listProjects(context)
                        UiSoundPlayer.playAction(context, UiActionSound.Confirm)
                    }
                    renameTarget = null
                }) {
                    Text(stringResource(R.string.pdf_library_rename), fontFamily = fontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text(stringResource(android.R.string.cancel), fontFamily = fontFamily)
                }
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = {
                Text(
                    stringResource(R.string.pdf_library_delete_title),
                    fontFamily = fontFamily
                )
            },
            text = {
                Text(
                    stringResource(R.string.pdf_library_delete_description, target.name),
                    fontFamily = fontFamily
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        PdfProjectRepository.deleteProject(context, target.id)
                        projects = PdfProjectRepository.listProjects(context)
                        UiSoundPlayer.play(context, UiSound.Delete)
                    }
                    deleteTarget = null
                }) {
                    Text(stringResource(R.string.pdf_delete), fontFamily = fontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(stringResource(android.R.string.cancel), fontFamily = fontFamily)
                }
            }
        )
    }

    val newPdfButtonColors = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.primary,
        background = screenBackground,
        textColorMode = settings.textColor,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.40f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(top = 30.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 18.dp)
            ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                        ) {
                            IconButton(onClick = onBack, modifier = Modifier.size(42.dp)) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(R.string.pdf_back),
                                    tint = headerText
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.pdf_library_title),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (26f * (settings.fontSize / 16f).coerceIn(0.82f, 1.24f)).coerceIn(21f, 33f).sp,
                                color = headerText
                            )
                            Text(
                                stringResource(R.string.pdf_library_subtitle),
                                fontFamily = fontFamily,
                                fontSize = (13f * (settings.fontSize / 16f).coerceIn(0.82f, 1.24f)).coerceIn(11f, 17f).sp,
                                color = headerSecondaryText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = infoChipColor,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                stringResource(R.string.pdf_library_document_count, projects.size),
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                color = infoChipText,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                        Surface(
                            color = infoChipColor,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                stringResource(R.string.pdf_library_autosave),
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                color = infoChipText,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

            if (projects.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(20.dp).size(48.dp)
                        )
                    }
                    Text(
                        stringResource(R.string.pdf_library_empty_title),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = primaryText,
                        fontSize = 19.sp,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        stringResource(R.string.pdf_library_empty_description),
                        fontFamily = fontFamily,
                        color = secondaryText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 7.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 6.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(projects, key = { it.id }) { project ->
                        val date = remember(project.modifiedAt) {
                            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                .format(Date(project.modifiedAt))
                        }
                        val cardInteractionSource = remember(project.id) { MutableInteractionSource() }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = cardInteractionSource,
                                    indication = null
                                ) { onOpen(project.id) },
                            shape = RoundedCornerShape(settings.noteCardCornerRadius.coerceIn(16f, 30f).dp),
                            color = projectCardColor,
                            tonalElevation = settings.noteCardElevation.coerceIn(0f, 8f).dp,
                            shadowElevation = (settings.noteCardElevation * 0.7f).coerceIn(0f, 6f).dp,
                            border = BorderStroke(1.dp, projectCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PdfProjectThumbnail(
                                    project = project,
                                    cardBackground = projectCardColor
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = blackTextBadgeBackground,
                                        border = if (blackTextBadgeEnabled) BorderStroke(1.dp, blackTextBadgeBorder) else null
                                    ) {
                                        Text(
                                            project.name,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = projectCardText,
                                            fontSize = (16.5f * (settings.fontSize / 16f).coerceIn(0.82f, 1.24f)).coerceIn(14f, 22f).sp,
                                            lineHeight = (18f * (settings.fontSize / 16f).coerceIn(0.82f, 1.18f)).coerceIn(15f, 20f).sp,
                                            maxLines = 2
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (blackTextBadgeEnabled) {
                                                Color(0xFFF8F8F6)
                                            } else {
                                                compositeUiColor(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                    projectCardColor
                                                )
                                            },
                                            border = if (blackTextBadgeEnabled) BorderStroke(1.dp, blackTextBadgeBorder) else null
                                        ) {
                                            Text(
                                                stringResource(R.string.pdf_library_pages, project.pageCount),
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                                fontFamily = fontFamily,
                                                color = projectCardText,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Surface(
                                        modifier = Modifier.padding(top = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = secondaryTextBadgeBackground,
                                        border = if (secondaryTextBadgeEnabled) BorderStroke(1.dp, secondaryTextBadgeBorder) else null
                                    ) {
                                        Text(
                                            stringResource(R.string.pdf_library_last_edit, date),
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                            fontFamily = fontFamily,
                                            color = projectCardSecondaryText,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box {
                                    Surface(
                                        shape = CircleShape,
                                        color = projectCardText.copy(alpha = 0.08f)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                UiSoundPlayer.playAction(context, UiActionSound.Menu)
                                                menuProjectId = project.id
                                            },
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Icon(Icons.Default.MoreVert, contentDescription = null, tint = projectCardText)
                                        }
                                    }
                                    AppDropdownMenu(
                                        modifier = Modifier
                                            .heightIn(max = 300.dp)
                                            .widthIn(min = 170.dp, max = 224.dp),
                                        expanded = menuProjectId == project.id,
                                        onDismissRequest = { menuProjectId = null },
                                        containerColor = popupColor
                                    ) {
                                        PdfOptionMenuItem(
                                            label = stringResource(R.string.pdf_library_edit),
                                            icon = Icons.Default.Edit,
                                            showIcon = settings.optionMenuShowIcons,
                                            textColor = popupTextColor
                                        ) {
                                            menuProjectId = null
                                            UiSoundPlayer.playAction(context, UiActionSound.Open)
                                            onOpen(project.id)
                                        }
                                        PdfOptionMenuItem(
                                            label = stringResource(R.string.pdf_library_rename),
                                            icon = Icons.Default.TextFields,
                                            showIcon = settings.optionMenuShowIcons,
                                            textColor = popupTextColor
                                        ) {
                                            menuProjectId = null
                                            UiSoundPlayer.playAction(context, UiActionSound.Select)
                                            renameText = project.name
                                            renameTarget = project
                                        }
                                        PdfOptionMenuItem(
                                            label = stringResource(R.string.pdf_duplicate),
                                            icon = Icons.Default.ContentCopy,
                                            showIcon = settings.optionMenuShowIcons,
                                            textColor = popupTextColor
                                        ) {
                                            menuProjectId = null
                                            scope.launch {
                                                val copyName = context.getString(R.string.pdf_library_copy_name, project.name)
                                                PdfProjectRepository.duplicateProject(context, project.id, copyName)
                                                projects = PdfProjectRepository.listProjects(context)
                                                UiSoundPlayer.playAction(context, UiActionSound.Add)
                                            }
                                        }

                                        HorizontalDivider(
                                            color = ensureUiContrast(
                                                preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                                                background = popupVisualBackground,
                                                minimumContrast = 1.7f
                                            )
                                        )

                                        PdfOptionMenuItem(
                                            label = stringResource(R.string.pdf_delete),
                                            icon = Icons.Default.Delete,
                                            showIcon = settings.optionMenuShowIcons,
                                            textColor = Color(0xFFC62828)
                                        ) {
                                            menuProjectId = null
                                            deleteTarget = project
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onNew,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(22.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = newPdfButtonColors.container,
                contentColor = newPdfButtonColors.content
            ),
            contentPadding = PaddingValues(horizontal = 17.dp, vertical = 13.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(21.dp))
            Spacer(Modifier.width(7.dp))
            Text(
                stringResource(R.string.pdf_library_new),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun PdfProjectThumbnail(
    project: PdfProjectRepository.Summary,
    cardBackground: Color
) {
    val context = LocalContext.current
    var thumbnail by remember(project.id, project.modifiedAt) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(project.id, project.modifiedAt) {
        thumbnail = PdfProjectRepository.loadProjectThumbnail(
            context = context,
            id = project.id,
            modifiedAt = project.modifiedAt,
            targetWidthPx = 260
        )
    }

    Surface(
        modifier = Modifier
            .width(74.dp)
            .height(98.dp),
        shape = RoundedCornerShape(12.dp),
        color = compositeUiColor(Color.White.copy(alpha = 0.72f), cardBackground),
        border = BorderStroke(
            1.dp,
            ensureUiContrast(
                preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.58f),
                background = cardBackground,
                minimumContrast = 1.55f
            )
        )
    ) {
        val bitmap = thumbnail
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = ensureUiContrast(
                        preferred = MaterialTheme.colorScheme.primary,
                        background = cardBackground,
                        minimumContrast = 4.5f
                    ),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun PdfOptionMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showIcon: Boolean,
    textColor: Color,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        modifier = Modifier.defaultMinSize(minHeight = 42.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = {
            Text(
                text = label,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        },
        leadingIcon = if (showIcon) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            null
        },
        onClick = onClick
    )
}
