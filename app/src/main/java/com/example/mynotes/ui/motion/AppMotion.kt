package com.example.mynotes.ui.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import com.example.mynotes.settings.MotionStylePolicy
import kotlin.math.roundToInt

private val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private val ExpressiveEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
private val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
private val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

/**
 * Motor central de movimiento de la app.
 *
 * Todos los presets usan primitivas de Compose para evitar ventanas nuevas,
 * destellos negros y trabajo extra del sistema de navegación de Android.
 */
object AppMotion {
    const val FAST = 140
    const val NORMAL = 220
    val supportedEasings = setOf("standard", "linear", "accelerate", "decelerate", "emphasized", "expressive", "emphasized_accel", "emphasized_decel")
    fun normalizeStyle(value: String): String = MotionStylePolicy.normalize(value)
    fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"
    fun normalizePerformanceMode(value: String): String = if (value == "performance" || value == "quality") value else "balanced"
    fun <T> performanceValue(mode: String, performance: T, balanced: T, quality: T, other: T = balanced): T =
        when (mode) { "performance" -> performance; "balanced" -> balanced; "quality" -> quality; else -> other }
    fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int {
        if (!animationsEnabled) return 0
        val speed = animationSpeed.coerceIn(0.5f, 2f)
        return (baseMilliseconds / speed).roundToInt().coerceAtLeast(1)
    }

    /** Same exit timing used by [ConfigurableAnimatedContent] for full-screen navigation. */
    fun screenExitDuration(animationsEnabled: Boolean, animationSpeed: Float, performanceMode: String): Int {
        if (!animationsEnabled) return 0
        val mode = normalizePerformanceMode(performanceMode)
        if (mode == "performance") return 24
        val speedBoost = when (mode) {
            "balanced" -> 1.10f
            else -> 1f
        }
        val speed = (animationSpeed * speedBoost).coerceIn(0.5f, 2.4f)
        return duration(FAST, true, speed)
    }
    fun easing(key: String): Easing = when (normalizeEasing(key)) {
            "linear" -> LinearEasing
            "accelerate" -> FastOutLinearInEasing
            "decelerate" -> LinearOutSlowInEasing
            "emphasized" -> EmphasizedEasing
            "expressive" -> ExpressiveEasing
            "emphasized_accel" -> EmphasizedAccelerateEasing
            "emphasized_decel" -> EmphasizedDecelerateEasing
            else -> FastOutSlowInEasing
        }
}

/** Se resuelve una sola vez por destino, también en la vista previa y las herramientas PDF. */
@Composable
internal fun rememberMotionStyle(targetState: Any?, animationStyle: String): String =
    remember(targetState, animationStyle) {
        val normalized = AppMotion.normalizeStyle(animationStyle)
        if (normalized == "random") MotionStylePolicy.concreteStyles.random() else normalized
    }

/** Primitivas de desplazamiento, escala y opacidad, sin expand/shrink. */
internal fun appMotionTransform(
    animationsEnabled: Boolean, animationSpeed: Float, animationStyle: String,
    animationEasing: String, animationIntensity: Float, performanceMode: String
): ContentTransform {
    if (!animationsEnabled) return EnterTransition.None togetherWith ExitTransition.None
    val mode = AppMotion.normalizePerformanceMode(performanceMode)
    val style = AppMotion.normalizeStyle(animationStyle)
    val speedBoost = when (mode) {
        "performance" -> 1.28f
        "balanced" -> 1.10f
        else -> 1f
    }
    val speed = (animationSpeed * speedBoost).coerceIn(0.5f, 2.4f)
    val intensity = (animationIntensity * if (mode == "performance") 0.82f else 1f).coerceIn(0.45f, 1.5f)
    val easing = AppMotion.easing(animationEasing)
    val enterDuration = AppMotion.duration(AppMotion.NORMAL, true, speed)
    val exitDuration = AppMotion.screenExitDuration(true, animationSpeed, mode)
    fun <V> enterSpec(delayMillis: Int = 0): FiniteAnimationSpec<V> =
        tween(durationMillis = enterDuration, delayMillis = delayMillis, easing = easing)
    fun <V> exitSpec(): FiniteAnimationSpec<V> = tween(durationMillis = exitDuration, easing = easing)
    val incoming = fadeIn(animationSpec = enterSpec())
    val outgoing = fadeOut(animationSpec = exitSpec())
    val cardScale = (1f - 0.045f * intensity).coerceIn(0.93f, 0.98f)
    val awayScale = (1f - 0.065f * intensity).coerceIn(0.90f, 0.98f)
    fun offset(size: Int, fraction: Float): Int = (size * fraction * intensity).roundToInt()
    return when (style) {
        "cross_dissolve" -> incoming togetherWith outgoing
        "lift_card" -> (incoming +
            slideInVertically(initialOffsetY = { offset(it, 0.10f) }, animationSpec = enterSpec()) +
            scaleIn(initialScale = cardScale, transformOrigin = TransformOrigin(0.5f, 1f), animationSpec = enterSpec())) togetherWith
            (outgoing + slideOutVertically(targetOffsetY = { offset(it, -0.035f) }, animationSpec = exitSpec()))
        "lateral_stack" -> (incoming +
            slideInHorizontally(initialOffsetX = { offset(it, 0.16f) }, animationSpec = enterSpec()) +
            scaleIn(initialScale = cardScale, transformOrigin = TransformOrigin(1f, 0.5f), animationSpec = enterSpec())) togetherWith
            (outgoing + slideOutHorizontally(targetOffsetX = { offset(it, -0.05f) }, animationSpec = exitSpec()) +
                scaleOut(targetScale = awayScale, animationSpec = exitSpec()))
        "depth_pull" -> (incoming +
            scaleIn(initialScale = (1f + 0.075f * intensity).coerceIn(1.03f, 1.12f), animationSpec = enterSpec())) togetherWith
            (outgoing + scaleOut(targetScale = awayScale, animationSpec = exitSpec()))
        "diagonal_flow" -> (incoming + slideIn(
            initialOffset = { IntOffset(offset(it.width, 0.12f), offset(it.height, 0.08f)) },
            animationSpec = enterSpec())) togetherWith
            (outgoing + slideOut(
                targetOffset = { IntOffset(offset(it.width, -0.06f), offset(it.height, -0.04f)) },
                animationSpec = exitSpec()))
        "corner_unfold" -> (incoming + scaleIn(
            initialScale = (1f - 0.12f * intensity).coerceIn(0.82f, 0.95f),
            transformOrigin = TransformOrigin(1f, 0f), animationSpec = enterSpec())) togetherWith
            (outgoing + scaleOut(targetScale = cardScale,
                transformOrigin = TransformOrigin(1f, 0f), animationSpec = exitSpec()))
        "focus_swap" -> (fadeIn(animationSpec = enterSpec(delayMillis = exitDuration / 2)) +
            scaleIn(initialScale = cardScale, animationSpec = enterSpec(delayMillis = exitDuration / 2))) togetherWith
            (outgoing + scaleOut(targetScale = (1f + 0.025f * intensity), animationSpec = exitSpec()))
        // Incluye el respaldo seguro para un valor no resuelto por un llamador antiguo.
        else -> incoming togetherWith outgoing
    }
}

/**
 * Contenedor configurable de navegación entre pantallas.
 * El fondo compartido queda pintado durante toda la transición,
 * para que nunca aparezca el fondo negro de la ventana.
 */
@Composable
fun <T> ConfigurableAnimatedContent(targetState: T, animationsEnabled: Boolean, animationSpeed: Float, animationStyle: String,
    animationEasing: String, animationIntensity: Float, performanceMode: String = "balanced", modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit) {
    val resolvedStyle = rememberMotionStyle(targetState, animationStyle)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedContent(targetState = targetState, modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center,
            transitionSpec = {
                appMotionTransform(animationsEnabled = animationsEnabled, animationSpeed = animationSpeed, animationStyle = resolvedStyle,
                    animationEasing = animationEasing, animationIntensity = animationIntensity, performanceMode = performanceMode)
            }, label = "screenMotionTransition") { state -> Box(modifier = Modifier.fillMaxSize()) {
                content(state)
            }
        }
    }
}

/**
 * Las pantallas ya no se animan por separado. Esto evita dobles transiciones,
 * recomposiciones innecesarias y flashes al navegar.
 */
@Composable
fun AnimatedScreenEntry(animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}
