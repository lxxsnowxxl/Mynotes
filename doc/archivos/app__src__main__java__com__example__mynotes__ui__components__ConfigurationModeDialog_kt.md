# ConfigurationModeDialog.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/ConfigurationModeDialog.kt`  
**SHA-256:** `b9f0581f2c1ed9ab9146a4b89d30d9ad0acd886d9f99b86ad18df604e63719d5`  
**Líneas:** 295  
**Package:** `com.example.mynotes.ui.components`

## 1. Para qué existe este archivo

Diálogo para seleccionar Basic/Advanced manteniendo el estilo y contraste de MyNotes.

## 2. Tipos/clases declarados

- No declara una clase/objeto propio; contiene funciones/valores de soporte o es un archivo marcador.

## 3. Estado, constantes y valores importantes

- **`context`** (línea 64) inicia con `LocalContext.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`dialogBackground`** (línea 66) inicia con `MaterialTheme.colorScheme.surfaceContainerHigh`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`primaryColor`** (línea 68) inicia con `MaterialTheme.colorScheme.primary`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`unselectedBaseColor`** (línea 69) inicia con `MaterialTheme.colorScheme.surfaceContainer`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`dialogContentColor`** (línea 70) inicia con `remember(dialogBackground`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`dialogSecondaryContentColor`** (línea 73) inicia con `remember(dialogContentColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedButtonColors`** (línea 81) inicia con `remember(primaryColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedContainerColor`** (línea 90) inicia con `selectedButtonColors.container`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedContentColor`** (línea 91) inicia con `selectedButtonColors.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`unselectedButtonColors`** (línea 92) inicia con `remember(unselectedBaseColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`unselectedContainerColor`** (línea 101) inicia con `unselectedButtonColors.container`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`unselectedContentColor`** (línea 102) inicia con `unselectedButtonColors.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`unselectedBorderColor`** (línea 103) inicia con `remember(unselectedContentColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`contentColor`** (línea 248) inicia con `if (selected`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`content`** (línea 249) inicia con `{`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `ConfigurationModeDialog` — líneas 59–233

**Firma:** `fun ConfigurationModeDialog( fontFamily: FontFamily, onBasicSelected: () -> Unit, onAdvancedSelected: () -> Unit )`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `fontFamily: FontFamily`
- `onBasicSelected: () -> Unit`
- `onAdvancedSelected: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `automaticUiTextColor`, `softenUiColorToContrast`, `resolveAdaptiveUiButtonColors`, `ensureUiContrast`, `BackHandler`, `fillMaxSize`, `background`, `copy`, `pointerInput`, `padding`, `fillMaxWidth`, `widthIn`, `RoundedCornerShape`, `height`, `ConfigurationModeChoiceButton`, `playAction`, `Button`, `onBasicSelected`.

### `ConfigurationModeChoiceButton` — líneas 236–293

**Firma:** `private fun ConfigurationModeChoiceButton( selected: Boolean, title: String, description: String, fontFamily: FontFamily, selectedContentColor: androidx.compose.ui.graphics.Color, unselectedContentColor: androidx.compose.ui.graphics.Color, selectedContainerColor: androidx.compose.ui.graphics.Color, unselectedContainerColor: androidx.compose.ui.graphics.Color, unselectedBorderColor: androidx.compose.ui.graphics.Color, onClick: () -> Unit )`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `selected: Boolean`
- `title: String`
- `description: String`
- `fontFamily: FontFamily`
- `selectedContentColor: androidx.compose.ui.graphics.Color`
- `unselectedContentColor: androidx.compose.ui.graphics.Color`
- `selectedContainerColor: androidx.compose.ui.graphics.Color`
- `unselectedContainerColor: androidx.compose.ui.graphics.Color`
- `unselectedBorderColor: androidx.compose.ui.graphics.Color`
- `onClick: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `Composable`, `padding`, `Button`, `fillMaxWidth`, `RoundedCornerShape`, `buttonColors`, `content`, `OutlinedButton`, `BorderStroke`, `outlinedButtonColors`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.
- Usa `com.example.mynotes.ui.sound.UiActionSound`.
- Usa `com.example.mynotes.ui.sound.UiSoundPlayer`.
- Usa `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`.
- Usa `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`.
- Usa `com.example.mynotes.ui.theme.automaticUiTextColor`.
- Usa `com.example.mynotes.ui.theme.ensureUiContrast`.
- Usa `com.example.mynotes.ui.theme.softenUiColorToContrast`.

## 6. Recursos Android que utiliza

- `R.string`: `configuration_mode_advanced`, `configuration_mode_advanced_description`, `configuration_mode_basic`, `configuration_mode_basic_description`, `configuration_mode_change_later`, `configuration_mode_confirm`, `configuration_mode_welcome_description`, `configuration_mode_welcome_title`

## 7. Tecnologías y efectos relevantes

- Participa en estado/efectos de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `ConfigurationModeDialog` — Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

## 9. Qué no debe romperse al modificarlo

- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Diálogo para seleccionar Basic/Advanced manteniendo el estilo y contraste de MyNotes. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
