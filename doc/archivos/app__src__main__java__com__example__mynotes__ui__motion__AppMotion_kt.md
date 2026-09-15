# AppMotion.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt`  
**Paquete:** `com.example.mynotes.ui.motion`  
**Líneas:** 714 → 269 (62.3% menos)

## Responsabilidad

Catálogo y lógica de animaciones/transiciones de la aplicación. Normaliza opciones y construye transformaciones según estilo, curva, velocidad, intensidad y perfil de rendimiento.

## Papel dentro de la arquitectura

Centraliza movimiento para que las pantallas no codifiquen animaciones incompatibles entre sí y para poder reducir trabajo cuando el usuario prioriza rendimiento.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Compose:** `androidx.compose.animation.AnimatedContent`, `androidx.compose.animation.AnimatedContentTransitionScope`, `androidx.compose.animation.ContentTransform`, `androidx.compose.animation.EnterTransition`, `androidx.compose.animation.ExitTransition`, `androidx.compose.animation.expandHorizontally`, `androidx.compose.animation.expandIn`, `androidx.compose.animation.expandVertically`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.shrinkHorizontally`, `androidx.compose.animation.shrinkOut`, `androidx.compose.animation.shrinkVertically`, `androidx.compose.animation.slideInHorizontally`, `androidx.compose.animation.slideInVertically`, `androidx.compose.animation.slideOutHorizontally`….

**Kotlin/Java/corrutinas:** `kotlin.math.roundToInt`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 49 | object | `AppMotion` | `object AppMotion {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 57 | fun | `normalizeStyle` | `fun normalizeStyle(value: String): String = if (value in supportedStyles) value else "zoom"` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 58 | fun | `normalizeEasing` | `fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 59 | fun | `normalizePerformanceMode` | `fun normalizePerformanceMode(value: String): String = when (value) {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 63 | fun | `duration` | `fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 68 | fun | `easing` | `fun easing(key: String): Easing = when (normalizeEasing(key)) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 138 | fun | `horizontalOffset` | `fun horizontalOffset(fullWidth: Int): Int = (fullWidth * slideFactor).roundToInt()` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 139 | fun | `verticalOffset` | `fun verticalOffset(fullHeight: Int): Int = (fullHeight * slideFactor).roundToInt()` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 264 | composable | `AnimatedScreenEntry` | `fun AnimatedScreenEntry(animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
