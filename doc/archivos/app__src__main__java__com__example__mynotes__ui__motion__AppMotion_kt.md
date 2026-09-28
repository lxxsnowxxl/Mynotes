# AppMotion.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt`  
**SHA-256:** `d00156b94c88b5c5e083997f1842d42a9a2063de69c90a9c000d9702025c02b7`  
**Líneas:** 343  
**Package:** `com.example.mynotes.ui.motion`

## 1. Para qué existe este archivo

Motor de animaciones/transiciones parametrizado por ajustes de velocidad, intensidad, easing y estilo.

## 2. Tipos/clases declarados

- Línea **55** — `object AppMotion`.

## 3. Estado, constantes y valores importantes

- **`EmphasizedEasing`** (línea 43) inicia con `CubicBezierEasing(0.2f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ExpressiveEasing`** (línea 45) inicia con `CubicBezierEasing(0.16f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`EmphasizedAccelerateEasing`** (línea 46) inicia con `CubicBezierEasing(0.3f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`EmphasizedDecelerateEasing`** (línea 47) inicia con `CubicBezierEasing(0.05f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`FAST`** (línea 56) inicia con `140`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`NORMAL`** (línea 57) inicia con `220`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`SLOW`** (línea 58) inicia con `320`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`supportedStyles`** (línea 59) inicia con `setOf("zoom"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`supportedEasings`** (línea 62) inicia con `setOf("standard"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`speed`** (línea 71) inicia con `animationSpeed.coerceIn(0.5f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`randomStylePool`** (línea 85) inicia con `listOf("zoom"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`mode`** (línea 94) inicia con `AppMotion.normalizePerformanceMode(performanceMode`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`requestedStyle`** (línea 95) inicia con `AppMotion.normalizeStyle(animationStyle`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`style`** (línea 102) inicia con `when (mode`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`speedBoost`** (línea 118) inicia con `when (mode`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`intensityFactor`** (línea 124) inicia con `if (mode == "performance"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`intensity`** (línea 129) inicia con `(animationIntensity * intensityFactor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`easing`** (línea 130) inicia con `AppMotion.easing(animationEasing`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`enterDuration`** (línea 131) inicia con `AppMotion.duration(AppMotion.NORMAL`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`exitDuration`** (línea 136) inicia con `if (mode == "performance"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`slowDuration`** (línea 141) inicia con `AppMotion.duration(AppMotion.SLOW`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`zoomScale`** (línea 145) inicia con `(1f - 0.08f * intensity`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`deepZoomScale`** (línea 146) inicia con `(1f - 0.16f * intensity`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`subtleScale`** (línea 147) inicia con `(1f - 0.025f * intensity`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`slideFactor`** (línea 148) inicia con `intensity.coerceIn(0.5f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`bounceSpec`** (línea 203) inicia con `spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`elasticSpec`** (línea 209) inicia con `spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`springFloat`** (línea 224) inicia con `spring<Float>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`springInt`** (línea 228) inicia con `spring<IntOffset>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`containerSpringFloat`** (línea 238) inicia con `spring<Float>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`containerSpringInt`** (línea 242) inicia con `spring<IntOffset>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`elasticSlideFloat`** (línea 256) inicia con `spring<Float>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`elasticSlideInt`** (línea 260) inicia con `spring<IntOffset>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`tonalSpring`** (línea 276) inicia con `spring<Float>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`resolvedStyle`** (línea 302) inicia con `remember(targetState`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `normalizeStyle` — líneas 63–63

**Firma:** `fun normalizeStyle(value: String): String`

Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.

**Entradas:**
- `value: String`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

### `normalizeEasing` — líneas 64–64

**Firma:** `fun normalizeEasing(value: String): String`

Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.

**Entradas:**
- `value: String`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

### `normalizePerformanceMode` — líneas 65–65

**Firma:** `fun normalizePerformanceMode(value: String): String`

Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.

**Entradas:**
- `value: String`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

### `duration` — líneas 69–73

**Firma:** `fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `baseMilliseconds: Int`
- `animationsEnabled: Boolean`
- `animationSpeed: Float`

**Salida:** Int.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `coerceIn`, `roundToInt`, `coerceAtLeast`.

### `easing` — líneas 74–74

**Firma:** `fun easing(key: String): Easing`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `key: String`

**Salida:** Easing.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `normalizeEasing`.

### `horizontalOffset` — líneas 149–149

**Firma:** `fun horizontalOffset(fullWidth: Int): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `fullWidth: Int`

**Salida:** Int.

**Operaciones/funciones que coordina:** `roundToInt`.

### `verticalOffset` — líneas 150–150

**Firma:** `fun verticalOffset(fullHeight: Int): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `fullHeight: Int`

**Salida:** Int.

**Operaciones/funciones que coordina:** `roundToInt`.

### `AnimatedScreenEntry` — líneas 337–342

**Firma:** `fun AnimatedScreenEntry(animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit )`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `animationsEnabled: Boolean`
- `animationSpeed: Float`
- `modifier: Modifier = Modifier`
- `content: @Composable () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `fillMaxSize`, `content`.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Participa en estado/efectos de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `normalizeStyle` — Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.
2. `normalizeEasing` — Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.
3. `normalizePerformanceMode` — Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.
4. `duration` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `easing` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `horizontalOffset` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
7. `verticalOffset` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
8. `AnimatedScreenEntry` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Motor de animaciones/transiciones parametrizado por ajustes de velocidad, intensidad, easing y estilo. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
