# SourceCodeInfoScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/SourceCodeInfoScreen.kt`  **SHA-256:** `ac7747acb83bd6b6ea3e76ef187aca5d5903c4c211b20c0c3db50a728f16398d`  **Líneas:** 220 · **Bytes:** 14079 · **Imports:** 48 · **Declaraciones detectadas:** 4
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Mapa de código/arquitectura visible desde Información de desarrollo.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.content.Intent`, `android.net.Uri`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Code`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.ScrollPositionCapsule`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 62 | `fun` | `SourceCodeInfoScreen` | `@Composable` |
| 185 | `fun` | `SourceSection` | `` |
| 201 | `fun` | `SourceCodeRow` | `` |
| 214 | `fun` | `SourceParagraph` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 2 aparición/apariciones.

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

- **R.string:** `development_repository_open`, `development_repository_section`, `development_source_attachment_cache`, `development_source_attachment_dao`, `development_source_attachment_viewer`, `development_source_audio_haptics`, `development_source_backup_manager`, `development_source_backup_restore`, `development_source_core_section`, `development_source_data_section`, `development_source_database`, `development_source_detail_screen`, `development_source_development_screen`, `development_source_display_controller`, `development_source_editor_screen`, `development_source_gradle`, `development_source_heading`, `development_source_layouts`, `development_source_link_card`, `development_source_link_repo`, `development_source_location_body`, `development_source_location_section`, `development_source_main_activity`, `development_source_manifest`, `development_source_motion`, `development_source_note_body`, `development_source_note_card`, `development_source_note_dao`, `development_source_note_section`, `development_source_note_vm`, `development_source_notes_screen`, `development_source_palette_catalog`, `development_source_performance_section`, `development_source_resources_section`, `development_source_settings_repo`, `development_source_settings_screen`, `development_source_settings_vm`, `development_source_source_screen`, `development_source_subtitle`, `development_source_support_section`, `development_source_title`, `development_source_ui_section`, `development_source_update_manager`, `development_source_values`, `mock_back`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
