package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.sound.UiSemanticSoundPolicy

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.workspace.WorkspaceLayoutPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DateFormat
import java.util.Date

internal val LocalCleanWorkspace = compositionLocalOf { false }
internal object PlusSaveEvents { val last = MutableStateFlow(0L); fun saved() { last.value = System.currentTimeMillis() } }

@Composable internal fun PlusPrimaryAction(label:String,onClick:()->Unit,enabled:Boolean=true,modifier:Modifier=Modifier,colors:ButtonColors=ButtonDefaults.buttonColors(),compact:Boolean=false,icon:ImageVector?=null){
    val iconOnly = compact && icon != null
    Button(onClick = { UiSoundPlayer.runWithFallbackClick(UiSemanticSoundPolicy.forLabel(label), onClick) },enabled=enabled,modifier=modifier.heightIn(min=48.dp).then(if(iconOnly)Modifier.width(48.dp)else Modifier),shape=RoundedCornerShape(24.dp),colors=colors,
        contentPadding=if(iconOnly)PaddingValues(0.dp)else ButtonDefaults.ContentPadding){
        if(iconOnly)Icon(requireNotNull(icon),contentDescription=label,modifier=Modifier.size(24.dp))
        else Text(label,maxLines=1)
    }
}

@Composable internal fun PlusStyle(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preview = LocalLayoutPreview.current != null
    val store = remember(context) { StudioStore.get(context) }
    val polish = rememberStudioRow(store, "preferences", "polish")
    LaunchedEffect(store) { if (!preview) store.load() }
    val clean = polish.optBoolean("clean", false)
    CompositionLocalProvider(LocalCleanWorkspace provides clean, LocalMinimumInteractiveComponentSize provides 48.dp,
        LocalTonalElevationEnabled provides !clean, content = content)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun PlusStatusPill(modifier: Modifier = Modifier, hosted: Boolean = false, showSaved: Boolean = true) {
    if (LocalLayoutPreview.current != null) return
    val context = LocalContext.current
    val manager = remember { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    fun connected(): Boolean = manager.getNetworkCapabilities(manager.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    var online by remember { mutableStateOf(connected()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(manager, lifecycleOwner) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { online = connected() }
            override fun onLost(network: Network) { online = connected() }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) { online = connected() }
        }
        var registered = false
        fun start() {
            if (!registered) {
                manager.registerDefaultNetworkCallback(callback)
                registered = true
            }
            online = connected()
        }
        fun stop() {
            if (registered) {
                runCatching { manager.unregisterNetworkCallback(callback) }
                registered = false
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) start()
            else if (event == Lifecycle.Event.ON_STOP) stop()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) start()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stop()
        }
    }
    val jobs by StudioJobs.state.collectAsStateWithLifecycle()
    val saved = if (showSaved) PlusSaveEvents.last.collectAsStateWithLifecycle().value else 0L
    var dismissed by remember { mutableStateOf("") }
    val current = jobs.firstOrNull { it.status == "running" || it.status == "error" && it.id != dismissed }
    if(!online&&current==null){
        Text("Offline · changes stay on this device",modifier.padding(4.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=2)
        return
    }
    if (!online || current != null) Surface(modifier.widthIn(max = 500.dp).padding(8.dp), shape = RoundedCornerShape(16.dp),
        color = if (current?.status == "error") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp, shadowElevation = 1.dp) {
        Column(Modifier.padding(horizontal = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (current?.status == "running") CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            val label = when {
                current?.status == "error" -> current.title + ": " + current.message
                current != null -> current.title
                else -> "Offline · changes stay on this device"
            }
            Text(label, Modifier.weight(1f).padding(8.dp).semantics { liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.labelMedium, maxLines = 2)
        }
        FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
            if (current?.status == "error") TextButton(onClick = { dismissed = current.id ; UiSoundPlayer.playFallbackClick()}) { Text("Dismiss") }
        }
        }
    } else if (showSaved && saved > 0) Text("Last local save · " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(saved)),
        modifier.padding(4.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** Shared tablet divider, with accessible buttons as an alternative to dragging. */
@Composable internal fun PlusResizableRow(key: String, left: @Composable () -> Unit, right: @Composable () -> Unit) {
    val context = LocalContext.current
    val store = remember { StudioStore.get(context) }
    val panel = rememberStudioRow(store, "panels", key)
    var fraction by remember(key) { mutableFloatStateOf(panel.optDouble("fraction", .64).toFloat().coerceIn(.25f, .75f)) }
    val density = LocalDensity.current
    val savedFraction = panel.optDouble("fraction", .64).toFloat()
    LaunchedEffect(key, savedFraction) { fraction = if(savedFraction.isFinite()) savedFraction else .64f }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val windowWidth=maxWidth.value
        val pixels = with(density) { (maxWidth - WorkspaceLayoutPolicy.dividerWidth.dp).coerceAtLeast(1.dp).toPx() }
        val visibleFraction = WorkspaceLayoutPolicy.panelFraction(fraction, windowWidth, density.fontScale)
        fun update(value: Float) { fraction = WorkspaceLayoutPolicy.panelFraction(value, windowWidth, density.fontScale) }
        fun persist() {
            val saved = WorkspaceLayoutPolicy.panelFraction(fraction, windowWidth, density.fontScale).toDouble()
            store.put("panels", key) { it.put("fraction", saved) }
        }
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(visibleFraction).fillMaxHeight().testTag("${key}_primary_panel")) { left() }
            Column(Modifier.width(48.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceContainerLow)
                .pointerInput(key,pixels,density.fontScale) { detectDragGestures(onDrag = { change, delta -> change.consume(); update(WorkspaceLayoutPolicy.panelFraction(fraction,windowWidth,density.fontScale) + delta.x / pixels) }, onDragEnd = { persist() },onDragCancel={persist()}) },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                IconButton(onClick = { update(visibleFraction - .05f); persist() ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.size(48.dp)) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft,"Reduce left panel width") }
                Text("⋮", Modifier.semantics { contentDescription = "Drag to resize panels" })
                IconButton(onClick = { update(visibleFraction + .05f); persist() ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.size(48.dp)) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight,"Increase left panel width") }
            }
            Box(Modifier.weight(1f - visibleFraction).fillMaxHeight().testTag("${key}_secondary_panel")) { right() }
        }
    }
}
