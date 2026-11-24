package com.example.mynotes.ui.pdf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.motion.appMotionTransform
import com.example.mynotes.ui.motion.rememberMotionStyle

/** Anima el contenido sobre el fondo global y notifica el final real de la salida. */
@Composable
internal fun PdfScreenTransition(
    visibility: MutableTransitionState<Boolean>,
    motionSettings: AppSettings,
    closing: Boolean,
    onExitFinished: () -> Unit,
    content: @Composable () -> Unit
) {
    val style = rememberMotionStyle(visibility.targetState, motionSettings.animationStyle)
    val transform = appMotionTransform(
        animationsEnabled = motionSettings.animationsEnabled,
        animationSpeed = motionSettings.animationSpeed,
        animationStyle = style,
        animationEasing = motionSettings.animationEasing,
        animationIntensity = motionSettings.animationIntensity,
        performanceMode = motionSettings.performanceMode
    )
    val currentOnExitFinished by rememberUpdatedState(onExitFinished)
    LaunchedEffect(closing, visibility.isIdle, visibility.currentState, visibility.targetState) {
        if (closing && visibility.isIdle && !visibility.currentState && !visibility.targetState) {
            currentOnExitFinished()
        }
    }
    AnimatedVisibility(
        visibleState = visibility,
        modifier = Modifier.fillMaxSize(),
        enter = transform.targetContentEnter,
        exit = transform.initialContentExit,
        label = "pdfScreenMotion"
    ) {
        content()
    }
}
