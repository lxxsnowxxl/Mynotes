# AppPopupStyles.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt`  
**SHA-256:** `6630e22ea68d8169cac1d47b77fbea4c4b89c78cda43a79780d3e3d946b1e0b0`  
**Líneas:** 144  
**Package:** `com.example.mynotes.ui.components`

## 1. Para qué existe este archivo

Funciones/valores compartidos para mantener estilo consistente en menús y popups Compose.

## 2. Tipos/clases declarados

- No declara una clase/objeto propio; contiene funciones/valores de soporte o es un archivo marcador.

## 3. Estado, constantes y valores importantes

- **`borderColor`** (línea 49) inicia con `ensureUiContrast(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`capsuleColor`** (línea 54) inicia con `adaptiveScrollCapsuleColor(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`menuModifier`** (línea 66) inicia con `modifier.drawWithContent {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`maxScroll`** (línea 68) inicia con `scrollState.maxValue`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`edgeInset`** (línea 71) inicia con `2.dp.toPx(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`verticalInset`** (línea 73) inicia con `10.dp.toPx(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`thumbWidth`** (línea 74) inicia con `4.dp.toPx(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`usableHeight`** (línea 75) inicia con `(size.height - verticalInset * 2f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`totalScrollableHeight`** (línea 77) inicia con `size.height + maxScroll.toFloat(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`visibleFraction`** (línea 79) inicia con `(size.height / totalScrollableHeight`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`minimumThumbHeight`** (línea 80) inicia con `28.dp.toPx(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`thumbHeight`** (línea 81) inicia con `(usableHeight * visibleFraction`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`progress`** (línea 84) inicia con `(scrollState.value.toFloat(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`travel`** (línea 85) inicia con `(usableHeight - thumbHeight`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`top`** (línea 86) inicia con `verticalInset + travel * progress`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`left`** (línea 87) inicia con `size.width - edgeInset - thumbWidth`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`containerColor`** (línea 126) inicia con `MaterialTheme.colorScheme.surfaceContainerHigh`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `AppDropdownMenu` — líneas 36–110

**Firma:** `fun AppDropdownMenu( expanded: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh, properties: PopupProperties = PopupProperties( focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true ), scrollState: ScrollState = rememberScrollState(), content: @Composable ColumnScope.() -> Unit )`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `expanded: Boolean`
- `onDismissRequest: () -> Unit`
- `modifier: Modifier = Modifier`
- `containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh`
- `properties: PopupProperties = PopupProperties( focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true )`
- `scrollState: ScrollState = rememberScrollState()`
- `content: @Composable ColumnScope.() -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `ensureUiContrast`, `copy`, `adaptiveScrollCapsuleColor`, `drawContent`, `toPx`, `coerceAtLeast`, `toFloat`, `coerceIn`, `coerceAtMost`, `drawRoundRect`, `Offset`, `Size`, `CornerRadius`, `DropdownMenu`, `RoundedCornerShape`, `BorderStroke`.

### `AppAlertDialog` — líneas 113–143

**Firma:** `fun AppAlertDialog( onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties( dismissOnBackPress = true, dismissOnClickOutside = true ), title: @Composable (() -> Unit)? = null, text: @Composable (() -> Unit)? = null, confirmButton: @Composable () -> Unit, dismissButton: @Composable (() -> Unit)? = null, icon: @Composable (() -> Unit)? = null )`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `onDismissRequest: () -> Unit`
- `modifier: Modifier = Modifier`
- `properties: DialogProperties = DialogProperties( dismissOnBackPress = true, dismissOnClickOutside = true )`
- `title: @Composable (() -> Unit)? = null`
- `text: @Composable (() -> Unit)? = null`
- `confirmButton: @Composable () -> Unit`
- `dismissButton: @Composable (() -> Unit)? = null`
- `icon: @Composable (() -> Unit)? = null`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `AlertDialog`, `RoundedCornerShape`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.ui.theme.ensureUiContrast`.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Participa en estado/efectos de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `AppDropdownMenu` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
2. `AppAlertDialog` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Funciones/valores compartidos para mantener estilo consistente en menús y popups Compose. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
