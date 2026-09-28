package com.example.mynotes.ui.media

import android.net.Uri
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Slider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import androidx.media3.common.PlaybackException
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Stable
class MediaPlaybackState internal constructor(initialBuffering: Boolean) {
    var prepared by mutableStateOf(false)
        private set
    var buffering by mutableStateOf(initialBuffering)
        private set
    var playing by mutableStateOf(false)
        private set
    var error by mutableStateOf(false)
        private set
    var duration by mutableIntStateOf(0)
        private set
    var position by mutableIntStateOf(0)
        private set

    fun reset(buffering: Boolean = false) {
        prepared = false
        this.buffering = buffering
        playing = false
        error = false
        duration = 0
        position = 0
    }

    fun beginBuffering() {
        error = false
        buffering = true
    }

    fun markError() {
        error = true
    }

    fun fail() {
        stopAfterPlayerError()
        error = true
    }

    fun seek(player: ExoPlayer, target: Int) {
        position = target
        if (prepared) ignorePlaybackException { player.seekTo(target.toLong()) }
    }

    fun toggle(player: ExoPlayer, beforePlay: (() -> Unit)? = null) {
        ignorePlaybackException {
            if (player.isPlaying) {
                player.pause()
            } else {
                if (duration > 0 && position >= duration - 250) {
                    player.seekTo(0L)
                    position = 0
                }
                beforePlay?.invoke()
                player.play()
            }
        }
    }

    internal fun onPlaybackStateChanged(player: ExoPlayer, playbackState: Int, clearErrorOnReady: Boolean) {
        when (playbackState) {
            Player.STATE_BUFFERING -> buffering = true
            Player.STATE_READY -> {
                buffering = false
                prepared = true
                if (clearErrorOnReady) error = false
                duration = player.duration.toPlaybackInt()
            }
            Player.STATE_ENDED -> {
                buffering = false
                prepared = true
                playing = false
                if (duration > 0) position = duration
            }
            Player.STATE_IDLE -> buffering = false
        }
    }

    internal fun setPlaying(isPlaying: Boolean) {
        playing = isPlaying
    }

    internal fun stopAfterPlayerError() {
        prepared = false
        playing = false
        buffering = false
    }

    internal fun updateProgress(player: ExoPlayer) {
        position = player.currentPosition.toPlaybackInt()
        updateDuration(player)
    }

    private fun updateDuration(player: ExoPlayer) {
        val currentDuration = player.duration.toPlaybackInt()
        if (currentDuration > 0) duration = currentDuration
    }
}


@Composable
fun MediaSeekSlider(
    player: ExoPlayer?, state: MediaPlaybackState, modifier: Modifier = Modifier,
    enabled: Boolean = state.prepared && !state.error, positionToInt: (Float) -> Int = { it.toInt() }
) {
    val context = LocalContext.current
    Slider(
        value = state.position.coerceIn(0, state.duration.coerceAtLeast(1)).toFloat(),
        onValueChange = { position ->
            UiSoundPlayer.playActionThrottled(context, UiActionSound.Navigation, 60L)
            player?.let { state.seek(it, positionToInt(position)) }
        },
        valueRange = 0f..state.duration.coerceAtLeast(1).toFloat(),
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
fun rememberMediaPlaybackState(key: Any?, initialBuffering: Boolean = false): MediaPlaybackState =
    remember(key) { MediaPlaybackState(initialBuffering) }

@Composable
fun BindMediaPlayer(
    player: ExoPlayer?,
    uri: Uri,
    state: MediaPlaybackState,
    playWhenReady: Boolean,
    textureView: TextureView? = null,
    clearErrorOnReady: Boolean = false,
    onActivate: ((ExoPlayer) -> Unit)? = null,
    onClear: ((ExoPlayer) -> Unit)? = null,
    onPlayerError: ((PlaybackException) -> Unit)? = null,
    onSetupError: (() -> Unit) = state::fail
) {
    DisposableEffect(player, textureView, uri) {
        if (player == null) {
            onDispose { }
        } else {
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    state.onPlaybackStateChanged(player, playbackState, clearErrorOnReady)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    state.setPlaying(isPlaying)
                }

                override fun onPlayerError(error: PlaybackException) {
                    state.stopAfterPlayerError()
                    if (onPlayerError != null) onPlayerError(error) else state.markError()
                }
            }
            player.addListener(listener)
            if (textureView != null) player.setVideoTextureView(textureView)
            try {
                player.setMediaItem(MediaItem.fromUri(uri))
                player.playWhenReady = playWhenReady
                player.prepare()
                onActivate?.invoke(player)
            } catch (_: Exception) {
                onSetupError()
            }
            onDispose {
                ignorePlaybackException { player.removeListener(listener) }
                if (textureView != null) {
                    ignorePlaybackException { player.clearVideoTextureView(textureView) }
                }
                onClear?.invoke(player)
                ignorePlaybackException { player.release() }
            }
        }
    }
    LaunchedEffect(player, state.prepared) {
        while (player != null) {
            if (state.prepared) state.updateProgress(player)
            delay(300)
        }
    }
}


private inline fun ignorePlaybackException(action: () -> Unit) {
    try { action() } catch (_: Exception) { }
}

private fun Long.toPlaybackInt(): Int = coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

fun formatMinuteSecondDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000L
    return "%d:%02d".format(totalSeconds / 60L, totalSeconds % 60L)
}
