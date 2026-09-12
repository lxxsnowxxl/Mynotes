package com.example.mynotes.ui.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

/**
 * Motor central de movimiento de la app.
 *
 * Todos los presets usan primitivas de Compose para evitar ventanas nuevas,
 * destellos negros y trabajo extra del sistema de navegación de Android.
 */
object AppMotion {
    const val FAST = 140
    const val NORMAL = 220
    const val SLOW = 320
    val supportedStyles = setOf("zoom", "zoom_fade", "fade", "slide_left", "slide_right", "slide_up", "slide_down", "slide_zoom_left",
        "slide_zoom_up", "axis_x", "axis_y", "axis_z", "expand", "expand_horizontal", "expand_vertical", "bounce", "elastic", "pop",
        "subtle", "random")
    val supportedEasings = setOf("standard", "linear", "accelerate", "decelerate", "emphasized")
    fun normalizeStyle(value: String): String = if (value in supportedStyles) value else "zoom"
    fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"
    fun normalizePerformanceMode(value: String): String = when (value) {
            "performance", "balanced", "quality" -> value
            else -> "balanced"
        }
    fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int {
        if (!animationsEnabled) return 0
        val speed = animationSpeed.coerceIn(0.5f, 2f)
        return (baseMilliseconds / speed).roundToInt().coerceAtLeast(1)
    }
    fun easing(key: String): Easing = when (normalizeEasing(key)) {
            "linear" -> LinearEasing
            "accelerate" -> FastOutLinearInEasing
            "decelerate" -> LinearOutSlowInEasing
            "emphasized" -> CubicBezierEasing(0.2f, 0f, 0f, 1f)
            else -> FastOutSlowInEasing
        }
}

private val randomStylePool = listOf("zoom", "zoom_fade", "fade", "slide_left", "slide_right", "slide_up", "slide_down", "slide_zoom_left",
    "slide_zoom_up", "axis_x", "axis_y", "axis_z", "expand", "expand_horizontal", "expand_vertical", "bounce", "elastic", "pop", "subtle")
private fun <T> AnimatedContentTransitionScope<T>.motionTransform(animationsEnabled: Boolean, animationSpeed: Float, animationStyle: String,
    animationEasing: String, animationIntensity: Float, performanceMode: String): ContentTransform {
    if (!animationsEnabled) {
        return EnterTransition.None togetherWith ExitTransition.None
    }
    val mode = AppMotion.normalizePerformanceMode(performanceMode)
    val requestedStyle = AppMotion.normalizeStyle(animationStyle)
    /*
     * Expand/shrink modifica el layout completo en cada frame.
     * En los perfiles orientados a fluidez conservamos una apariencia
     * equivalente mediante transformaciones de capa (scale/slide/fade),
     * que son mucho más baratas en dispositivos antiguos.
     */
    val style = when (mode) {
            "performance" -> when (requestedStyle) {
                    "expand", "axis_z", "bounce", "elastic" -> "zoom_fade"
                    "expand_horizontal" -> "slide_zoom_left"
                    "expand_vertical" -> "slide_zoom_up"
                    else -> requestedStyle
                }
            "balanced" -> when (requestedStyle) {
                    "expand" -> "zoom_fade"
                    "expand_horizontal" -> "slide_zoom_left"
                    "expand_vertical" -> "slide_zoom_up"
                    else -> requestedStyle
                }
            else -> requestedStyle
        }
    val speedBoost = when (mode) {
            "performance" -> 1.28f
            "balanced" -> 1.10f
            else -> 1f
        }
    val speed = (animationSpeed * speedBoost).coerceIn(0.5f, 2.4f)
    val intensityFactor = if (mode == "performance") {
            0.82f
        } else {
            1f
        }
    val intensity = (animationIntensity * intensityFactor).coerceIn(0.45f, 1.5f)
    val easing = AppMotion.easing(animationEasing)
    val enterDuration = AppMotion.duration(AppMotion.NORMAL, true, speed)
    /*
     * Con salida casi instantánea el contenido anterior deja de dibujarse
     * muy pronto y no compite con la pantalla entrante por CPU/GPU.
     */
    val exitDuration = if (mode == "performance") {
            24
        } else {
            AppMotion.duration(AppMotion.FAST, true, speed)
        }
    val slowDuration = AppMotion.duration(AppMotion.SLOW, true, speed)
    fun <V> enterSpec(): FiniteAnimationSpec<V> = tween(durationMillis = enterDuration, easing = easing)
    fun <V> exitSpec(): FiniteAnimationSpec<V> = tween(durationMillis = exitDuration, easing = easing)
    fun <V> slowSpec(): FiniteAnimationSpec<V> = tween(durationMillis = slowDuration, easing = easing)
    val zoomScale = (1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)
    val deepZoomScale = (1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)
    val subtleScale = (1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)
    val slideFactor = intensity.coerceIn(0.5f, 1.5f)
    fun horizontalOffset(fullWidth: Int): Int = (fullWidth * slideFactor).roundToInt()
    fun verticalOffset(fullHeight: Int): Int = (fullHeight * slideFactor).roundToInt()
    return when (style) {
        "fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith
                fadeOut(targetAlpha = (1f - 0.85f * intensity).coerceIn(0f, 0.55f), animationSpec = exitSpec())
        "slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { -horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { -verticalOffset(it) }, animationSpec = exitSpec())
        "slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { verticalOffset(it) }, animationSpec = exitSpec())
        "slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },
                    animationSpec = enterSpec()) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(
                        initialAlpha = 0.55f, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.40f).roundToInt() }, animationSpec = exitSpec()) +
                        scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f,
                            animationSpec = exitSpec()))
        "slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()
                ) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.55f,
                        animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.35f).roundToInt() }, animationSpec = exitSpec()) + scaleOut(
                            targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f, animationSpec = exitSpec())
                    )
        "axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.16f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.14f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,
                        animationSpec = slowSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.05f * intensity).coerceAtMost(1.12f), animationSpec = exitSpec()) + fadeOut(
                            targetAlpha = 0f, animationSpec = exitSpec()))
        "expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.30f, animationSpec = enterSpec())) togetherWith
                (shrinkOut(shrinkTowards = Alignment.Center, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkVertically(shrinkTowards = Alignment.CenterVertically, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "bounce" -> {
            val bounceSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = bounceSpec) + fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec()))
        }
        "elastic" -> {
            val elasticSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = elasticSpec) + fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec())
                    )
        }
        "pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.15f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.08f * intensity).coerceAtMost(1.15f), animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0f, animationSpec = exitSpec()))
        "subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.72f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.65f, animationSpec = exitSpec())
                    )
        "zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,
                        animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith
                scaleOut(targetScale = zoomScale, animationSpec = exitSpec())
    }
}

/**
 * Contenedor configurable de navegación entre pantallas.
 * El fondo queda pintado durante toda la transición, incluso en expand/shrink,
 * para que nunca aparezca el fondo negro de la ventana.
 */
@Composable
fun <T> ConfigurableAnimatedContent(targetState: T, animationsEnabled: Boolean, animationSpeed: Float, animationStyle: String,
    animationEasing: String, animationIntensity: Float, performanceMode: String = "balanced", modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit) {
    val resolvedStyle = remember(targetState, animationStyle) {
            val normalized = AppMotion.normalizeStyle(animationStyle)
            if (normalized == "random") {
                randomStylePool.random()
            } else {
                normalized
            }
        }
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        AnimatedContent(targetState = targetState, modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center,
            transitionSpec = {
                motionTransform(animationsEnabled = animationsEnabled, animationSpeed = animationSpeed, animationStyle = resolvedStyle,
                    animationEasing = animationEasing, animationIntensity = animationIntensity, performanceMode = performanceMode)
            }, label = "screenMotionTransition") { state -> Box(modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)) {
                content(state)
            }
        }
    }
}

/** Compatibilidad con el nombre usado por versiones anteriores. */
@Composable
fun <T> ZoomAnimatedContent(targetState: T, animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit) {
    ConfigurableAnimatedContent(targetState = targetState, animationsEnabled = animationsEnabled, animationSpeed = animationSpeed,
        animationStyle = "zoom", animationEasing = "standard", animationIntensity = 1f, performanceMode = "balanced", modifier = modifier,
        content = content)
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
