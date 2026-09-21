# AppMotion.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt`  **SHA-256:** `d00156b94c88b5c5e083997f1842d42a9a2063de69c90a9c000d9702025c02b7`  **Líneas:** 342 · **Bytes:** 22229 · **Imports:** 40 · **Declaraciones detectadas:** 9
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Animaciones/transiciones globales controladas por AppSettings.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.motion`.

### Android / Jetpack / Compose

`androidx.compose.animation.AnimatedContent`, `androidx.compose.animation.AnimatedContentTransitionScope`, `androidx.compose.animation.ContentTransform`, `androidx.compose.animation.EnterTransition`, `androidx.compose.animation.ExitTransition`, `androidx.compose.animation.expandHorizontally`, `androidx.compose.animation.expandIn`, `androidx.compose.animation.expandVertically`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.shrinkHorizontally`, `androidx.compose.animation.shrinkOut`, `androidx.compose.animation.shrinkVertically`, `androidx.compose.animation.slideInHorizontally`, `androidx.compose.animation.slideInVertically`, `androidx.compose.animation.slideOutHorizontally`, `androidx.compose.animation.slideOutVertically`, `androidx.compose.animation.togetherWith`, `androidx.compose.animation.core.CubicBezierEasing`, `androidx.compose.animation.core.Easing`, `androidx.compose.animation.core.FastOutLinearInEasing`, `androidx.compose.animation.core.FiniteAnimationSpec`, `androidx.compose.animation.core.FastOutSlowInEasing`, `androidx.compose.animation.core.LinearEasing`, `androidx.compose.animation.core.LinearOutSlowInEasing`, `androidx.compose.animation.core.Spring`, `androidx.compose.animation.core.spring`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.background`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.unit.IntOffset`

### Kotlin / Coroutines / Java

`kotlin.math.roundToInt`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 55 | `object` | `AppMotion` | `object AppMotion {` |
| 63 | `fun` | `normalizeStyle` | `fun normalizeStyle(value: String): String = if (value in supportedStyles) value else "zoom"` |
| 64 | `fun` | `normalizeEasing` | `fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"` |
| 65 | `fun` | `normalizePerformanceMode` | `fun normalizePerformanceMode(value: String): String = when (value) {` |
| 69 | `fun` | `duration` | `fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int {` |
| 74 | `fun` | `easing` | `fun easing(key: String): Easing = when (normalizeEasing(key)) {` |
| 149 | `fun` | `horizontalOffset` | `fun horizontalOffset(fullWidth: Int): Int = (fullWidth * slideFactor).roundToInt()` |
| 150 | `fun` | `verticalOffset` | `fun verticalOffset(fullHeight: Int): Int = (fullHeight * slideFactor).roundToInt()` |
| 336 | `fun` | `AnimatedScreenEntry` | `@Composable` |

## 4. Estado, efectos y límites observables

- **Compose state:** 2 aparición/apariciones.
- **coerce*:** 28 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
