# DevelopmentInfoScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/DevelopmentInfoScreen.kt`  **SHA-256:** `7527be1880ad5b104da099e58b86e03824344db995b295eabef0558c7d1a42e5`  **Líneas:** 312 · **Bytes:** 19016 · **Imports:** 54 · **Declaraciones detectadas:** 5
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Pantalla de información de desarrollo, versión, créditos y repositorio.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.content.Intent`, `android.content.pm.ApplicationInfo`, `android.net.Uri`, `android.os.Build`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Info`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material.icons.filled.Code`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.Icon`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.ScrollPositionCapsule`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`

### Kotlin / Coroutines / Java

`java.util.Calendar`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 70 | `fun` | `DevelopmentInfoScreen` | `@Composable` |
| 255 | `fun` | `DevelopmentSection` | `` |
| 271 | `fun` | `DevelopmentValueRow` | `` |
| 285 | `fun` | `DevelopmentParagraph` | `` |
| 292 | `fun` | `PerformanceProfileRow` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 4 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.ScrollPositionCapsule`
- `com.example.mynotes.ui.motion.AnimatedScreenEntry`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.appFontFamily`
- `com.example.mynotes.ui.theme.ensureUiContrast`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`

## 6. Recursos Android referenciados

- **R.string:** `development_android_integration_body`, `development_android_integration_section`, `development_app_section`, `development_architecture_body`, `development_architecture_section`, `development_assistance`, `development_assistance_value`, `development_build_body`, `development_build_debug`, `development_build_release`, `development_build_section`, `development_build_system`, `development_build_system_value`, `development_build_type`, `development_compatibility_body`, `development_compatibility_section`, `development_compile_sdk`, `development_copyright`, `development_copyright_value`, `development_credits_body`, `development_credits_section`, `development_current_profile`, `development_device_sdk`, `development_github_account`, `development_info_subtitle`, `development_info_title`, `development_java_compatibility`, `development_legal_body`, `development_legal_section`, `development_license`, `development_license_value`, `development_media_body`, `development_media_section`, `development_min_sdk`, `development_package`, `development_performance_intro`, `development_performance_section`, `development_primary_author`, `development_primary_author_value`, `development_profile_balanced`, `development_profile_balanced_detail`, `development_profile_performance`, `development_profile_performance_detail`, `development_profile_quality`, `development_profile_quality_detail`, `development_release_optimization`, `development_release_optimization_value`, `development_repository_host`, `development_repository_name`, `development_repository_open`, `development_repository_section`, `development_sdk_section`, `development_source_intro`, `development_source_open`, `development_source_section`, `development_stack_body`, `development_stack_section`, `development_storage_body`, `development_storage_section`, `development_target_sdk`, `development_ui_body`, `development_ui_section`, `development_version`, `mock_back`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
