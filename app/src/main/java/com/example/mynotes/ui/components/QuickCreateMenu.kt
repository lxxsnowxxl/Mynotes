package com.example.mynotes.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast

private data class QuickCreateAction(
    val title: Int,
    val description: Int,
    val icon: ImageVector,
    val sound: UiActionSound,
    val onClick: () -> Unit
)

/**
 * Los accesos rápidos usan únicamente la superficie sólida del tema actual.
 * El FAB conserva su carbón oscuro fijo en cualquier paleta y efecto de fondo.
 * No se crean pinceles, animaciones de blur ni degradados para estos controles.
 */
internal data class QuickCreateSolidColors(val capsule: Color, val fab: Color)

internal fun quickCreateSolidColors(capsuleSurface: Color): QuickCreateSolidColors =
    QuickCreateSolidColors(
        capsule = capsuleSurface.copy(alpha = 1f),
        fab = Color(0xFF202124)
    )

/**
 * Accesos flotantes discretos, anclados al FAB. Las cápsulas ajustan su ancho
 * al texto traducido sin dejar espacios vacíos ni perder tamaño táctil.
 * Todas las acciones mantienen su destino y un único sonido semántico.
 */
@Composable
internal fun QuickCreateFabMenu(
    settings: AppSettings,
    fontFamily: FontFamily,
    containerColor: Color,
    contentColor: Color,
    onOpenReminders: () -> Unit,
    onAddNote: () -> Unit,
    onAddPdf: () -> Unit,
    onDrawNote: () -> Unit,
    onExpandedChange: (Boolean) -> Unit = {}
) {
    val preview = LocalLayoutPreview.current
    var expanded by remember { mutableStateOf(preview?.quickCreateExpanded == true) }
    val reportExpanded by rememberUpdatedState(onExpandedChange)
    LaunchedEffect(expanded) { reportExpanded(expanded) }
    DisposableEffect(Unit) {
        onDispose { reportExpanded(false) }
    }
    BackHandler(enabled = expanded) { expanded = false }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val duration = AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)
    val easing = AppMotion.easing(settings.animationEasing)
    val fabSize by animateDpAsState(
        settings.fabSize.coerceAtLeast(48f).dp,
        animationSpec = tween(duration, easing = easing),
        label = "fabSize"
    )
    val rotation by animateFloatAsState(
        if (expanded) 45f else 0f,
        animationSpec = tween(duration, easing = easing),
        label = "quickCreateRotation"
    )
    // Los colores de superficie provienen del esquema de la paleta actual, no
    // de los estilos de gradiente/blur elegidos para el fondo de la pantalla.
    val solidColors = remember(containerColor) { quickCreateSolidColors(containerColor) }
    val pillTextColor = remember(contentColor, solidColors.capsule) {
        ensureUiContrast(contentColor, solidColors.capsule, 4.5f)
    }
    val fabIconColor = Color.White
    val maxPillWidth = (configuration.screenWidthDp - 32).coerceAtLeast(160).dp
    val availableHeight = (configuration.screenHeightDp - fabSize.value - 92f).coerceAtLeast(80f).dp
    // El ancho se adapta al contenido de cada cápsula (sin tamaños prefijados).
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.weight(1f, fill = false),
            enter = fadeIn(tween(duration, easing = easing)) +
                expandVertically(tween(duration, easing = easing), expandFrom = Alignment.Bottom),
            exit = fadeOut(tween(duration, easing = easing)) +
                shrinkVertically(tween(duration, easing = easing), shrinkTowards = Alignment.Bottom)
        ) {
            val actions = remember(onAddNote, onOpenReminders, onAddPdf, onDrawNote) {
                listOf(
                    QuickCreateAction(R.string.mock_new_note, R.string.quick_create_note_hint,
                        Icons.Default.NoteAdd, UiActionSound.Add, onAddNote),
                    QuickCreateAction(R.string.reminders, R.string.quick_create_reminder_hint,
                        Icons.Default.NotificationsActive, UiActionSound.Select, onOpenReminders),
                    QuickCreateAction(R.string.add_pdf, R.string.quick_create_pdf_hint,
                        Icons.Default.PictureAsPdf, UiActionSound.Add, onAddPdf),
                    QuickCreateAction(R.string.create_drawing, R.string.quick_create_drawing_hint,
                        Icons.Default.Brush, UiActionSound.Select, onDrawNote)
                )
            }
            Column(
                // El margen queda dentro del área desplazable para que la sombra de Draw quepa completa.
                modifier = Modifier.heightIn(max = availableHeight)
                    .verticalScroll(rememberScrollState(), reverseScrolling = true)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEachIndexed { index, action ->
                    QuickCreateActionPill(
                        action = action,
                        settings = settings,
                        fontFamily = fontFamily,
                        panelColor = solidColors.capsule,
                        panelTextColor = pillTextColor,
                        prominent = index == 0,
                        maxPillWidth = maxPillWidth
                    ) {
                        expanded = false
                        UiSoundPlayer.runAction(context, action.sound, action.onClick)
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) { expanded = !expanded },
            modifier = Modifier.size(fabSize),
            containerColor = solidColors.fab,
            contentColor = fabIconColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 2.dp, pressedElevation = 3.dp,
                focusedElevation = 2.dp, hoveredElevation = 2.dp
            ),
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                stringResource(if (expanded) R.string.quick_create_close else R.string.quick_create_open),
                Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp)
                    .graphicsLayer { rotationZ = rotation },
                tint = fabIconColor
            )
        }
    }
}

@Composable
private fun QuickCreateActionPill(
    action: QuickCreateAction,
    settings: AppSettings,
    fontFamily: FontFamily,
    panelColor: Color,
    panelTextColor: Color,
    prominent: Boolean,
    maxPillWidth: Dp,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressDuration = AppMotion.duration(110, settings.animationsEnabled, settings.animationSpeed)
    val scale by animateFloatAsState(
        if (pressed && settings.animationsEnabled) 0.985f else 1f,
        animationSpec = tween(pressDuration, easing = AppMotion.easing(settings.animationEasing)),
        label = "quickCreatePillPress"
    )
    // Mantener contraste sobre el color sólido derivado de la paleta activa.
    val textColor = ensureUiContrast(panelTextColor, panelColor, 4.5f)
    val iconColor = compositeUiColor(textColor.copy(alpha = 0.075f), panelColor)

    Surface(
        // Sin anchuras fijas ni pesos: Row mide el icono y el texto real.
        // Solo se limita el ancho máximo para que encaje en teléfonos estrechos.
        modifier = Modifier.widthIn(
            min = if (prominent) 176.dp.coerceAtMost(maxPillWidth) else 112.dp,
            max = maxPillWidth
        )
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = panelColor,
        contentColor = textColor,
        tonalElevation = 0.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.heightIn(min = if (prominent) 64.dp else 56.dp)
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = iconColor,
                contentColor = textColor,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(action.icon, contentDescription = null, modifier = Modifier.size(21.dp), tint = textColor)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = stringResource(action.title),
                    fontFamily = fontFamily,
                    fontWeight = if (prominent) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (prominent) {
                    Text(
                        text = stringResource(action.description),
                        fontFamily = fontFamily,
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.76f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
