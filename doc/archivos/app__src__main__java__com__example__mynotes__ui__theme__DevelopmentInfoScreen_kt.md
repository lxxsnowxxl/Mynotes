# DevelopmentInfoScreen.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/DevelopmentInfoScreen.kt`  
**SHA-256 actual del archivo, sin modificar:** `de5fcb4f5434b4a9d1738c6cd25433c5ced3cfa6729990036a6440142519f5a7`  
**Líneas del código real:** 304  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Pantalla secundaria de información técnica y autoría. Presenta versión, SDK, arquitectura, tecnologías, rendimiento, repositorio, créditos y avisos legales.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes agregan autores/créditos, copyright, repositorio GitHub, datos técnicos de compilación y acceso a la pantalla que describe la estructura del código.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **52 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.content.Intent`, `android.content.pm.ApplicationInfo`, `android.net.Uri`, `android.os.Build`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Info`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material.icons.filled.Code`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.Icon`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`

### Java / Kotlin estándar

`java.util.Calendar`

## 3. Restricciones, límites e invariantes detectables

- **Guardias de API Android: 3 aparición/apariciones.** protegen llamadas cuya disponibilidad cambia según la versión de Android.
- **Límites visuales: 2 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Estado Compose: 6 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `DevelopmentInfoScreen` — fun, líneas 69–246

```kotlin
fun DevelopmentInfoScreen(settings: AppSettings, onOpenSourceCode: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val packageInfo = remember(context.packageName) {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName.orEmpty().ifBlank { "1.0" }
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
    }
    val applicationInfo = context.applicationInfo
    val buildType = if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
        stringResource(R.string.development_build_debug)
    } else {
        stringResource(R.string.development_build_release)
    }
    val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) applicationInfo.minSdkVersion else 24
    val targetSdk = applicationInfo.targetSdkVersion
    val screenBackground = MaterialTheme.colorScheme.background
    val primaryText = resolveUiTextColor(settings.textColor, screenBackground)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = screenBackground,
            topBar = {
                TopAppBar(title = {
                        Text(text = stringResource(R.string.development_info_title), fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    },
                    navigationIcon = {
                        TextButton(onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                                onBack()
                            }, colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.mock_back), modifier = Modifier.size(25.dp))
                        }
                    })
            }) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())
                    .widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = primaryText,
                        modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(text = "MyNotes", color = primaryText, fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text(text = stringResource(R.string.development_info_subtitle), color = secondaryText,
                            fontFamily = fontFamily, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))

                DevelopmentSection(title = stringResource(R.string.development_app_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_version), "$versionName ($versionCode)", settings)
                    DevelopmentValueRow(stringResource(R.string.development_package), context.packageName, settings)
                    DevelopmentValueRow(stringResource(R.string.development_build_type), buildType, settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_sdk_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_min_sdk), "API $minSdk", settings)
                    DevelopmentValueRow(stringResource(R.string.development_target_sdk), "API $targetSdk", settings)
                    DevelopmentValueRow(stringResource(R.string.development_compile_sdk), "API $MYNOTES_COMPILE_SDK", settings)
                    DevelopmentValueRow(stringResource(R.string.development_device_sdk), "API ${Build.VERSION.SDK_INT}", settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_build_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_build_system),
                        stringResource(R.string.development_build_system_value), settings)
                    DevelopmentValueRow(stringResource(R.string.development_java_compatibility),
                        "Java $MYNOTES_JAVA_COMPATIBILITY", settings)
                    DevelopmentValueRow(stringResource(R.string.development_release_optimization),
                        stringResource(R.string.development_release_optimization_value), settings)
                    Spacer(Modifier.height(6.dp))
                    DevelopmentParagraph(stringResource(R.string.development_build_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_ui_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_ui_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_architecture_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_architecture_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_storage_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_storage_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_media_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_media_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_performance_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_performance_intro), settings)
                    Spacer(Modifier.height(10.dp))
                    PerformanceProfileRow(title = stringResource(R.string.development_profile_performance),
                        detail = stringResource(R.string.development_profile_performance_detail), selected = settings.performanceMode == "performance",
                        settings = settings)
                    Spacer(Modifier.height(8.dp))
                    PerformanceProfileRow(title = stringResource(R.string.development_profile_balanced),
                        detail = stringResource(R.string.development_profile_balanced_detail), selected = settings.performanceMode == "balanced",
                        settings = settings)
                    Spacer(Modifier.height(8.dp))
                    PerformanceProfileRow(title = stringResource(R.string.development_profile_quality),
                        detail = stringResource(R.string.development_profile_quality_detail), selected = settings.performanceMode == "quality",
                        settings = settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_stack_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_stack_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_compatibility_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_compatibility_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_android_integration_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_android_integration_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_credits_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_primary_author),
                        stringResource(R.string.development_primary_author_value), settings)
                    DevelopmentValueRow(stringResource(R.string.development_github_account), "@lxxsnowxxl", settings)
                    DevelopmentValueRow(stringResource(R.string.development_assistance),
                        stringResource(R.string.development_assistance_value), settings)
                    Spacer(Modifier.height(6.dp))
                    DevelopmentParagraph(stringResource(R.string.development_credits_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_repository_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_repository_host), "GitHub", settings)
                    DevelopmentValueRow(stringResource(R.string.development_repository_name), "lxxsnowxxl/Mynotes", settings)
                    DevelopmentParagraph(MYNOTES_REPOSITORY_URL, settings)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MYNOTES_REPOSITORY_URL)))
                        }
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(19.dp))
                        Text(text = stringResource(R.string.development_repository_open), modifier = Modifier.padding(start = 8.dp),
                            fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                DevelopmentSection(title = stringResource(R.string.development_source_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_source_intro), settings)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        onOpenSourceCode()
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(19.dp))
                        Text(text = stringResource(R.string.development_source_open), modifier = Modifier.padding(start = 8.dp),
                            fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                DevelopmentSection(title = stringResource(R.string.development_legal_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_copyright),
                        stringResource(R.string.development_copyright_value, currentYear), settings)
                    DevelopmentValueRow(stringResource(R.string.development_license),
                        stringResource(R.string.development_license_value), settings)
                    Spacer(Modifier.height(6.dp))
                    DevelopmentParagraph(stringResource(R.string.development_legal_body), settings)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
```

**Firma/entrada.** `fun DevelopmentInfoScreen(settings: AppSettings, onOpenSourceCode: () -> Unit, onBack: () -> Unit) {`

**Parámetros.**
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onOpenSourceCode: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onBack: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; ramifica o parametriza comportamiento según el perfil de rendimiento; produce navegación/interacción externa mediante Intent; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.2 `DevelopmentSection` — fun, líneas 249–262

```kotlin
private fun DevelopmentSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val border = ensureUiContrast(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f), background, 2.2f)
    Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(18.dp),
        color = background, border = BorderStroke(1.dp, border), tonalElevation = 0.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = text, fontFamily = appFontFamily(settings.font),
                fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
```

**Firma/entrada.** `private fun DevelopmentSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {`

**Parámetros.**
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `content: @Composable () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.3 `DevelopmentValueRow` — fun, líneas 265–276

```kotlin
private fun DevelopmentValueRow(label: String, value: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top) {
        Text(text = label, modifier = Modifier.weight(0.42f), color = secondary,
            fontFamily = appFontFamily(settings.font), fontSize = 13.sp)
        Text(text = value, modifier = Modifier.weight(0.58f), color = text, fontFamily = appFontFamily(settings.font),
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
```

**Firma/entrada.** `private fun DevelopmentValueRow(label: String, value: String, settings: AppSettings) {`

**Parámetros.**
- `label: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `value: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.4 `DevelopmentParagraph` — fun, líneas 279–283

```kotlin
private fun DevelopmentParagraph(text: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Text(text = text, color = secondary, fontFamily = appFontFamily(settings.font), fontSize = 13.sp, lineHeight = 19.sp)
}
```

**Firma/entrada.** `private fun DevelopmentParagraph(text: String, settings: AppSettings) {`

**Parámetros.**
- `text: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.5 `PerformanceProfileRow` — fun, líneas 286–304

```kotlin
private fun PerformanceProfileRow(title: String, detail: String, selected: Boolean, settings: AppSettings) {
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val titleColor = resolveUiTextColor(settings.textColor, container)
    val detailColor = resolveSecondaryUiTextColor(settings.textColor, container)
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = container, tonalElevation = 0.dp) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, modifier = Modifier.weight(1f), color = titleColor, fontFamily = appFontFamily(settings.font),
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (selected) {
                    Text(text = stringResource(R.string.development_current_profile), color = titleColor,
                        fontFamily = appFontFamily(settings.font), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Text(text = detail, modifier = Modifier.padding(top = 3.dp), color = detailColor,
                fontFamily = appFontFamily(settings.font), fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}
```

**Firma/entrada.** `private fun PerformanceProfileRow(title: String, detail: String, selected: Boolean, settings: AppSettings) {`

**Parámetros.**
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `detail: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `selected: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

## 5. Variables y propiedades, una por una

Se detectaron **27 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 56 | `MYNOTES_REPOSITORY_URL` | `private const val MYNOTES_REPOSITORY_URL: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private const val MYNOTES_REPOSITORY_URL = "https://github.com/lxxsnowxxl/Mynotes"` |
| 57 | `MYNOTES_COMPILE_SDK` | `private const val MYNOTES_COMPILE_SDK: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private const val MYNOTES_COMPILE_SDK = 37` |
| 58 | `MYNOTES_JAVA_COMPATIBILITY` | `private const val MYNOTES_JAVA_COMPATIBILITY: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private const val MYNOTES_JAVA_COMPATIBILITY = 11` |
| 70 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 71 | `fontFamily` | `local/pública por contexto val fontFamily: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val fontFamily = remember(settings.font) { appFontFamily(settings.font) }` |
| 72 | `packageInfo` | `local/pública por contexto val packageInfo: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val packageInfo = remember(context.packageName) {` |
| 76 | `versionName` | `local/pública por contexto val versionName: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val versionName = packageInfo.versionName.orEmpty().ifBlank { "1.0" }` |
| 77 | `versionCode` | `local/pública por contexto val versionCode: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else {` |
| 81 | `applicationInfo` | `local/pública por contexto val applicationInfo: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val applicationInfo = context.applicationInfo` |
| 82 | `buildType` | `local/pública por contexto val buildType: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val buildType = if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {` |
| 87 | `minSdk` | `local/pública por contexto val minSdk: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) applicationInfo.minSdkVersion else 24` |
| 88 | `targetSdk` | `local/pública por contexto val targetSdk: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val targetSdk = applicationInfo.targetSdkVersion` |
| 89 | `screenBackground` | `local/pública por contexto val screenBackground: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val screenBackground = MaterialTheme.colorScheme.background` |
| 90 | `primaryText` | `local/pública por contexto val primaryText: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val primaryText = resolveUiTextColor(settings.textColor, screenBackground)` |
| 91 | `secondaryText` | `local/pública por contexto val secondaryText: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)` |
| 92 | `currentYear` | `local/pública por contexto val currentYear: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }` |
| 250 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val background = MaterialTheme.colorScheme.surfaceContainerLow` |
| 251 | `text` | `local/pública por contexto val text: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val text = resolveUiTextColor(settings.textColor, background)` |
| 252 | `border` | `local/pública por contexto val border: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val border = ensureUiContrast(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f), background, 2.2f)` |
| 266 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val background = MaterialTheme.colorScheme.surfaceContainerLow` |
| 267 | `text` | `local/pública por contexto val text: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val text = resolveUiTextColor(settings.textColor, background)` |
| 268 | `secondary` | `local/pública por contexto val secondary: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondary = resolveSecondaryUiTextColor(settings.textColor, background)` |
| 280 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val background = MaterialTheme.colorScheme.surfaceContainerLow` |
| 281 | `secondary` | `local/pública por contexto val secondary: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondary = resolveSecondaryUiTextColor(settings.textColor, background)` |
| 287 | `container` | `local/pública por contexto val container: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val container = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh` |
| 288 | `titleColor` | `local/pública por contexto val titleColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val titleColor = resolveUiTextColor(settings.textColor, container)` |
| 289 | `detailColor` | `local/pública por contexto val detailColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val detailColor = resolveSecondaryUiTextColor(settings.textColor, container)` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **27 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 69–246 | 0 | ámbito/lambda anónima | `fun DevelopmentInfoScreen(settings: AppSettings, onOpenSourceCode: () -> Unit, onBack: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 71–71 | 1 | `remember` / memoria de composición | `val fontFamily = remember(settings.font) { appFontFamily(settings.font) }` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 72–75 | 1 | `remember` / memoria de composición | `val packageInfo = remember(context.packageName) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 76–76 | 1 | ámbito/lambda anónima | `val versionName = packageInfo.versionName.orEmpty().ifBlank { "1.0" }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 77–80 | 1 | condición `if` | `val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 82–84 | 1 | condición `if` | `val buildType = if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 84–86 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 92–92 | 1 | `remember` / memoria de composición | `val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 94–245 | 1 | ámbito/lambda anónima | `AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 96–110 | 2 | ámbito/lambda anónima | `topBar = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 97–100 | 3 | ámbito/lambda anónima | `TopAppBar(title = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 101–109 | 3 | ámbito/lambda anónima | `navigationIcon = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 110–244 | 2 | ámbito/lambda anónima | `}) { paddingValues ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 112–243 | 3 | ámbito/lambda anónima | `.widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 113–122 | 4 | bloque UI Compose | `Row(verticalAlignment = Alignment.CenterVertically) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 116–121 | 5 | bloque UI Compose | `Column(modifier = Modifier.padding(start = 12.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 249–262 | 0 | ámbito/lambda anónima | `private fun DevelopmentSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 254–261 | 1 | ámbito/lambda anónima | `color = background, border = BorderStroke(1.dp, border), tonalElevation = 0.dp) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 255–260 | 2 | bloque UI Compose | `Column(modifier = Modifier.padding(16.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 265–276 | 0 | ámbito/lambda anónima | `private fun DevelopmentValueRow(label: String, value: String, settings: AppSettings) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 270–275 | 1 | ámbito/lambda anónima | `verticalAlignment = Alignment.Top) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 279–283 | 0 | ámbito/lambda anónima | `private fun DevelopmentParagraph(text: String, settings: AppSettings) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 286–304 | 0 | ámbito/lambda anónima | `private fun PerformanceProfileRow(title: String, detail: String, selected: Boolean, settings: AppSettings) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 290–303 | 1 | bloque UI Compose | `Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = container, tonalElevation = 0.dp) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 291–302 | 2 | bloque UI Compose | `Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 292–299 | 3 | bloque UI Compose | `Row(verticalAlignment = Alignment.CenterVertically) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 295–298 | 4 | condición `if` | `if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |

## 7. Side effects, rendimiento y lifecycle

- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Navegación / Intents:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

La pantalla mezcla datos dinámicos (versión/API/arquitectura cuando aplica) con información estática del proyecto (autoría, repositorio, arquitectura declarada). El botón hacia GitHub es una acción externa; la navegación a `SourceCodeInfoScreen` es interna y conserva el tema de la app.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
