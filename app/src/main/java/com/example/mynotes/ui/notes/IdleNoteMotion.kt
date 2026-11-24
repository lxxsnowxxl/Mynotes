package com.example.mynotes.ui.notes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

/** Plays the same sequence and intensity on the settings sample as on a visible note. */
@Composable
internal fun rememberIdleNoteMotionPreview(
    enabled: Boolean,
    replayToken: Int,
    intensityLevel: Int,
    styleId: String
): Modifier {
    val player = remember { IdleNoteMotionPlayer() }
    LaunchedEffect(enabled, replayToken, intensityLevel, styleId) {
        if (enabled) {
            delay(140L)
            player.play(null, styleId, intensityLevel, Random.Default)
        }
    }
    return if (enabled) player.modifier else Modifier
}

private fun idleMotionPace(frequency: Int): Double = when (frequency.coerceIn(1, 3)) {
    1 -> 1.6
    3 -> 0.65
    else -> 1.0
}

@Stable
internal class NotesIdleMotion {
    private val coordinator = IdleNoteMotionCoordinator()
    private val player = IdleNoteMotionPlayer()

    var activityToken by mutableIntStateOf(0)
        private set

    fun onInteraction() {
        player.stop()
        activityToken++
    }

    @Composable
    fun modifierFor(noteId: Int): Modifier {
        val active by remember(this, noteId) { derivedStateOf { player.activeNoteId == noteId } }
        return if (active) player.modifier else Modifier
    }

    suspend fun play(noteId: Int, style: String, intensityLevel: Int, pace: Double, random: Random) {
        if (!coordinator.tryStart(noteId)) return
        try {
            player.play(noteId, style, intensityLevel, random)
        } finally {
            coordinator.finish(noteId, (random.nextLong(16_000L, 25_000L) * pace).toLong())
        }
    }
}

/** The grid owns one clock; it asks for candidates only when a note can move. */
internal suspend fun runNotesIdleMotion(
    motion: NotesIdleMotion,
    options: IdleMotionOptions,
    visibleNoteIds: () -> List<Int>
) {
    val random = Random(System.nanoTime())
    val pace = idleMotionPace(options.frequency)
    var previousNote: Int? = null
    delay((random.nextLong(16_000L, 27_000L) * pace).toLong())
    while (true) {
        val visible = visibleNoteIds()
        if (visible.isNotEmpty()) {
            val candidates = visible.filter { it != previousNote }.ifEmpty { visible }
            val noteId = candidates.random(random)
            motion.play(noteId, options.styleId, options.intensityLevel, pace, random)
            previousNote = noteId
        }
        delay((random.nextLong(25_000L, 45_000L) * pace).toLong())
    }
}

private class IdleNoteMotionPlayer {
    private val progress = Animatable(0f)
    private var playback by mutableStateOf<IdleNoteMotionPlayback?>(null)
    private var previousStyle: String? = null

    val activeNoteId: Int?
        get() = playback?.noteId

    // Only the active card reads progress. Layer changes do not recompose or measure the grid.
    val modifier = Modifier.graphicsLayer {
        playback?.applyTo(this, progress.value)
    }

    fun stop() {
        playback = null
    }

    suspend fun play(noteId: Int?, styleId: String, intensityLevel: Int, random: Random) {
        val selected = IdleNoteMotionStyles.normalize(styleId)
        val style = if (selected == IdleNoteMotionStyles.RANDOM) {
            IdleNoteMotionStyles.concrete.filter { it != previousStyle }.random(random)
        } else selected
        val direction = if (random.nextBoolean()) 1f else -1f
        val current = IdleNoteMotionPlayback(noteId, style, intensityLevel, direction)

        progress.snapTo(0f)
        previousStyle = style
        playback = current
        try {
            progress.animateTo(1f, tween(durationMillis = current.durationMillis, easing = LinearEasing))
        } finally {
            if (playback === current) stop()
        }
    }
}

private class IdleNoteMotionPlayback(
    val noteId: Int?,
    style: String,
    intensityLevel: Int,
    private val direction: Float
) {
    private val frames = IdleNoteMotionFrames.forStyle(style)
    val durationMillis: Int = frames.last().millis
    private val scaleDelta = IdleNoteMotionStyles.maxScaleDelta(intensityLevel)
    private val strength = scaleDelta / IdleNoteMotionStyles.maxScaleDelta(5)
    private val turnLimit = if (style == IdleNoteMotionStyles.SWAY) {
        IdleNoteMotionStyles.swayAngle(intensityLevel)
    } else IdleAngularMotionPolicy.maxAngleDegrees(intensityLevel)

    fun applyTo(layer: GraphicsLayerScope, progress: Float) = with(layer) {
        val time = progress.coerceIn(0f, 1f) * durationMillis
        var index = 1
        while (index < frames.lastIndex && time >= frames[index].millis) index++
        val start = frames[index - 1]
        val end = frames[index]
        val fraction = start.easing.transform((time - start.millis) / (end.millis - start.millis))

        val width = size.width
        val height = size.height
        val longSide = maxOf(width, height).coerceAtLeast(1f)
        val shortSide = minOf(width, height)
        // Keep corners and stretch inside the space between cards across different aspect ratios.
        val turnSpan = minOf(shortSide * 2f, 280.dp.toPx())
        val stretchSpan = minOf(shortSide * 2f, 180.dp.toPx())
        val aspectLimit = minOf(1f, turnSpan / longSide)

        translationX = mix(start.slide, end.slide, fraction) * minOf(5.5.dp.toPx(), width * 0.06f) *
            strength * direction
        translationY = -mix(start.lift, end.lift, fraction) * minOf(7.5.dp.toPx(), height * 0.16f) *
            strength
        rotationZ = mix(start.turn, end.turn, fraction) * turnLimit * aspectLimit * direction
        rotationX = mix(start.pitch, end.pitch, fraction) * strength * aspectLimit
        rotationY = mix(start.yaw, end.yaw, fraction) * strength * aspectLimit * direction
        cameraDistance = longSide * 4f
        scaleX = 1f + mix(start.stretchX, end.stretchX, fraction) * scaleDelta *
            minOf(1f, stretchSpan / width.coerceAtLeast(1f))
        scaleY = 1f + mix(start.stretchY, end.stretchY, fraction) * scaleDelta *
            minOf(1f, stretchSpan / height.coerceAtLeast(1f))
    }

    private fun mix(start: Float, end: Float, fraction: Float): Float =
        start + (end - start) * fraction
}
