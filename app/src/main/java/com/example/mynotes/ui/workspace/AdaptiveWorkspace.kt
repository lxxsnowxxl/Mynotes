package com.example.mynotes.ui.workspace

import com.example.mynotes.ui.components.LocalAppBackground
import com.example.mynotes.ui.sound.UiSoundPlayer

import android.view.DragEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.background
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.semantics.Role
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import kotlin.math.roundToInt
import com.example.mynotes.data.workspace.WorkspaceAssets
import com.example.mynotes.data.workspace.DroppedAsset
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.util.findActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
internal fun AdaptiveWorkspace(
    modifier: Modifier = Modifier,
    expandedTools: Boolean,
    toolPanelIntensity: Float,
    forceSinglePane: Boolean = false,
    compactTools: Boolean = false,
    toolsDrawer: Boolean = false,
    linkedSplit: Boolean = false,
    drawerTitleRes: Int = R.string.note_tools_panel_title,
    drawerMaxHeightFraction: Float = 0.94f,
    onDrawerOpenChange: (Boolean) -> Unit = {},
    drawerEnabled: Boolean = true,
    drawerEntranceHint: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
    supporting: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val wide = WorkspaceLayoutPolicy.useTwoPanes(availableWidth.value,availableHeight.value,LocalDensity.current.fontScale)
        if (linkedSplit) {
            // Split drawing mode: a single canvas above a connected, independently scrolling
            // tools panel. Both receive exactly half of the available workspace height.
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), content = content)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().heightIn(min = 32.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(drawerTitleRes), style = MaterialTheme.typography.titleMedium)
                        }
                        supporting()
                    }
                }
            }
        } else if (toolsDrawer) {
            // Panel reutilizable para las herramientas de notas, PDF y dibujo.
            // El documento conserva todo su espacio, y solo las herramientas se desplazan.
            Box(Modifier.fillMaxSize(), content = content)
            val appearance = LocalAppBackground.current
            val drawerColors = remember(appearance.palette, appearance.toneIndex, toolPanelIntensity) {
                workspaceDrawerColors(appearance.palette, appearance.toneIndex, toolPanelIntensity)
            }
            val toolColors = drawerColors.controls
            val drawerWidth = NoteToolsDrawerGeometry.drawerWidthDp(availableWidth.value).dp
            val tabWidth = NoteToolsDrawerGeometry.tabWidthDp.dp
            val tabHeight = NoteToolsDrawerGeometry.tabHeightDp.dp
            val tabTopInset = NoteToolsDrawerGeometry.tabTopInsetDp.dp
            val entrancePeek = remember { Animatable(0f) }
            var entranceHintShown by remember { mutableStateOf(false) }
            LaunchedEffect(drawerEntranceHint, drawerEnabled, expandedTools) {
                entrancePeek.snapTo(0f)
                if (drawerEntranceHint && !entranceHintShown && drawerEnabled && !expandedTools) {
                    delay(260L)
                    entranceHintShown = true
                    entrancePeek.animateTo(12f, tween(durationMillis = 150, easing = FastOutSlowInEasing))
                    entrancePeek.animateTo(0f, spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ))
                    delay(90L)
                    entrancePeek.animateTo(7f, tween(durationMillis = 110, easing = FastOutSlowInEasing))
                    entrancePeek.animateTo(0f, spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ))
                }
            }
            val maxPanelHeight = availableHeight * drawerMaxHeightFraction.coerceIn(0.35f, 0.94f)
            val closedOffset = NoteToolsDrawerGeometry.closedOffsetDp(drawerWidth.value).dp
            val drawerOffset = animateDpAsState(
                targetValue = if (expandedTools) 0.dp else closedOffset,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                label = "note-tools-horizontal-slide"
            )
            val drawerScroll = rememberScrollState()
            // En PDF Studio, volver a mostrar primero las herramientas principales
            // al reabrir el cajón en vez de recordar la última sección avanzada.
            LaunchedEffect(expandedTools) {
                if (compactTools && expandedTools) drawerScroll.scrollTo(0)
            }
            val renderTools by remember(expandedTools, closedOffset, drawerOffset) {
                derivedStateOf { expandedTools || drawerOffset.value > closedOffset + 0.5.dp }
            }
            // Guardar el desplazamiento en píxeles: corresponde al gesto real y no
            // cambia de escala al variar la densidad o el tamaño del editor.
            val availableHeightPx = with(LocalDensity.current) { availableHeight.roundToPx() }
            var rowHeightPx by remember { mutableIntStateOf(0) }
            // Null significa que aún no se ha movido el panel manualmente.
            // Evitar Float.NaN: Compose redondea el desplazamiento durante el primer layout.
            var userTopOffsetPx by remember { mutableStateOf<Float?>(null) }
            LaunchedEffect(availableHeightPx, rowHeightPx) {
                // Mantener la posición al cambiar orientación o mostrar el teclado.
                userTopOffsetPx = userTopOffsetPx?.let {
                    NoteToolsDrawerGeometry.clampVerticalOffsetPx(it, availableHeightPx, rowHeightPx)
                }
            }
            val dragHint = stringResource(R.string.note_tools_drag_vertical)
            // Este gesto existe SOLO en la cabecera y en la pestaña, no en el
            // contenido. Así se preservan el scroll de adjuntos y la escritura.
            val topDragHandle = if (drawerEnabled) {
                Modifier.pointerInput(availableHeightPx, rowHeightPx) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        val start = userTopOffsetPx ?: NoteToolsDrawerGeometry.defaultVerticalOffsetPx(
                            availableHeightPx, rowHeightPx)
                        userTopOffsetPx = NoteToolsDrawerGeometry.clampVerticalOffsetPx(
                            start + dragAmount, availableHeightPx, rowHeightPx)
                    }
                }
            } else Modifier
            Row(
                modifier = Modifier.align(Alignment.TopStart)
                    .offset {
                        val top = NoteToolsDrawerGeometry.clampVerticalOffsetPx(
                            userTopOffsetPx ?: NoteToolsDrawerGeometry.defaultVerticalOffsetPx(
                                availableHeightPx, rowHeightPx), availableHeightPx, rowHeightPx)
                        IntOffset(drawerOffset.value.roundToPx(), top.roundToInt())
                    }
                    .width(drawerWidth)
                    .heightIn(max = availableHeight)
                    .onSizeChanged { rowHeightPx = it.height },
                verticalAlignment = Alignment.Top
            ) {
                WorkspaceToolTheme(toolColors) {
                    if (renderTools) {
                        Surface(
                            modifier = Modifier.weight(1f).heightIn(max = maxPanelHeight),
                            shape = RoundedCornerShape(topEnd = 22.dp, bottomEnd = 22.dp),
                            color = toolColors.background,
                            contentColor = toolColors.onSurface,
                            border = BorderStroke(1.dp, toolColors.outlineVariant),
                            shadowElevation = 0.dp
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .heightIn(max = maxPanelHeight)
                                    .verticalScroll(drawerScroll)
                                    .padding(horizontal = if (compactTools) 10.dp else 12.dp,
                                        vertical = if (compactTools) 8.dp else 12.dp),
                                verticalArrangement = Arrangement.spacedBy(if (compactTools) 5.dp else 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .heightIn(min = if (compactTools) 36.dp else 44.dp)
                                        .semantics { contentDescription = dragHint }
                                        .then(topDragHandle),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(drawerTitleRes),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = toolColors.onSurface)
                                    Spacer(Modifier.weight(1f))
                                }
                                CompositionLocalProvider(LocalWorkspaceDrawerColors provides drawerColors) {
                                    supporting()
                                }
                            }
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
                val drawerAction = stringResource(if (expandedTools)
                    R.string.note_tools_collapse else R.string.note_tools_expand)
                val tabDescription = "${stringResource(drawerTitleRes)}. $drawerAction. $dragHint"
                Box(
                    modifier = Modifier.width(tabWidth)
                        .padding(top = tabTopInset)
                        .offset { IntOffset(entrancePeek.value.dp.roundToPx(), 0) }
                        .height(tabHeight)
                ) {
                    Surface(
                        onClick = {
                            onDrawerOpenChange(!expandedTools)
                            UiSoundPlayer.playFallbackClick()
                        },
                        enabled = drawerEnabled,
                        modifier = Modifier.fillMaxSize()
                            .then(topDragHandle)
                            .semantics(mergeDescendants = true) {
                                contentDescription = tabDescription
                                role = Role.Button
                            },
                        shape = RoundedCornerShape(
                            topStart = 6.dp, bottomStart = 6.dp,
                            topEnd = 24.dp, bottomEnd = 24.dp
                        ),
                        color = if (expandedTools) toolColors.primaryContainer else drawerColors.section,
                        contentColor = toolColors.onPrimaryContainer,
                        border = BorderStroke(0.75.dp, toolColors.outlineVariant.copy(alpha = 0.55f)),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
                        ) {
                            Box(
                                Modifier.width(14.dp).height(3.dp)
                                    .background(toolColors.primary, RoundedCornerShape(2.dp))
                            )
                            Icon(
                                imageVector = if (expandedTools) Icons.Default.ChevronLeft
                                    else Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        } else if (wide && !forceSinglePane) {
            val context = LocalContext.current
            val store = remember(context) { com.example.mynotes.studio.StudioStore.get(context) }
            val workspace = com.example.mynotes.studio.rememberStudioRow(store, "preferences", "workspace")
            val leftHand = workspace.optBoolean("leftHand")
            @Composable fun panel() { Surface(Modifier.fillMaxSize(), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerLow) { Column(Modifier.verticalScroll(rememberScrollState()).padding(if (compactTools) 10.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(if (compactTools) 7.dp else 12.dp), content = supporting) } }
            com.example.mynotes.studio.PlusResizableRow("tools", left = { if (leftHand) panel() else Box(Modifier.fillMaxSize(), content = content) }, right = { if (leftHand) Box(Modifier.fillMaxSize(), content = content) else panel() })
        } else {
            Column(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f).fillMaxWidth(), content = content)
                if (expandedTools) Surface(Modifier.fillMaxWidth().heightIn(max = (availableHeight * (if (compactTools) 0.34f else 0.42f)).coerceAtLeast(96.dp)),
                    shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(if (compactTools) 8.dp else 12.dp),
                        verticalArrangement = Arrangement.spacedBy(if (compactTools) 7.dp else 12.dp), content = supporting)
                }
            }
        }
    }
}

@Composable
internal fun workspaceShortcuts(onSave: () -> Unit, onUndo: (() -> Unit)? = null, onRedo: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null): Modifier {
    val focus = remember { FocusRequester() }
    val preview=LocalLayoutPreview.current
    LaunchedEffect(Unit){if(preview==null)focus.requestFocus()}
    val actions=mutableMapOf("save" to onSave);onUndo?.let{actions["undo"]=it};onRedo?.let{actions["redo"]=it};onSearch?.let{actions["find"]=it}
    return Modifier.focusRequester(focus).then(com.example.mynotes.studio.studioKeys(actions)).focusable()
}

/** Flush recent edits when the app goes into the background, before a process can be reclaimed. */
@Composable
internal fun WorkspaceOnStop(onStop: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    val latest by rememberUpdatedState(onStop)
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_STOP) latest() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
}

/** Copy while the drag-and-drop URI grant is alive; consumers receive private durable files. */
@Composable
internal fun WorkspaceDropReceiver(onAssets: (List<DroppedAsset>) -> Unit, onError: () -> Unit) {
    val view = LocalView.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val latestAssets by rememberUpdatedState(onAssets)
    val latestError by rememberUpdatedState(onError)
    val preview = LocalLayoutPreview.current
    DisposableEffect(view) {
        if (preview != null) onDispose { }
        else {
            view.setOnDragListener { _, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> event.clipDescription?.let { description ->
                        (0 until description.mimeTypeCount).any { description.getMimeType(it) != "text/plain" }
                    } == true
                    DragEvent.ACTION_DROP -> {
                        val permission = context.findActivity()?.requestDragAndDropPermissions(event)
                        val clip = event.clipData
                        val uris = if (clip == null) emptyList() else (0 until clip.itemCount).mapNotNull { clip.getItemAt(it).uri }
                        scope.launch {
                            try {
                                val assets = WorkspaceAssets.copy(context, uris)
                                if (assets.isNotEmpty()) latestAssets(assets) else latestError()
                            } catch (cancelled: CancellationException) { throw cancelled }
                            catch (_: Exception) { latestError() }
                            finally { permission?.release() }
                        }
                        true
                    }
                    else -> true
                }
            }
            onDispose { view.setOnDragListener(null) }
        }
    }
}

