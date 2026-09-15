# DisplayPerformanceController.kt

**Ruta:** `app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt`  
**Paquete:** `com.example.mynotes.performance`  
**Líneas:** 189 → 119 (37.0% menos)

## Responsabilidad

Centraliza la frecuencia de refresco preferida de las ventanas de MyNotes de acuerdo con el perfil de rendimiento.

## Papel dentro de la arquitectura

Evita duplicar reglas de 60/120 Hz en Activities. Selecciona un modo compatible del panel, recuerda la última petición por Window y permite reaplicarla al volver a primer plano.

## Flujo funcional principal

Flujo típico: la Activity recibe/observa `performanceMode` -> llama `requestForPerformanceMode` -> se normaliza la clave -> se determina 60/120 Hz -> se buscan modos de pantalla compatibles preservando resolución -> se escribe la preferencia en `WindowManager.LayoutParams`.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `android.os.Build`, `android.view.Display`, `android.view.Window`.

**Kotlin/Java/corrutinas:** `java.util.WeakHashMap`, `kotlin.math.abs`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 25 | object | `DisplayPerformanceController` | `object DisplayPerformanceController {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 32 | fun | `requestForPerformanceMode` | `fun requestForPerformanceMode(window: Window, performanceMode: String) {` | Normaliza el perfil, lo recuerda para la Window y aplica la frecuencia objetivo correspondiente. |
| 41 | fun | `reapplyLastRequest` | `fun reapplyLastRequest(window: Window) {` | Reaplica la última preferencia conocida al regresar al primer plano sin duplicar reglas de Hz en la Activity. |
| 49 | fun | `release` | `fun release(window: Window) {` | Libera referencias o recursos asociados al ciclo de vida. |
| 52 | fun | `refreshRateFor` | `private fun refreshRateFor(performanceMode: String): Float = when (performanceMode) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 57 | fun | `normalizePerformanceMode` | `private fun normalizePerformanceMode(value: String): String = when (value.trim().lowercase()) {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 62 | fun | `requestRefreshRate` | `private fun requestRefreshRate(window: Window, targetRefreshRate: Float) {` | Escribe preferredRefreshRate/preferredDisplayModeId y elige un modo de pantalla compatible conservando resolución cuando es posible. |
| 107 | fun | `chooseClosestMode` | `private fun chooseClosestMode(modes: List<Display.Mode>, targetRefreshRate: Float): Display.Mode? = modes.minWithOrNull(` | Ordena los modos compatibles por distancia respecto a la frecuencia objetivo y devuelve el más apropiado. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
