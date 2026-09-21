# BackupRestoreSection.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt`  **SHA-256:** `89e8386c8cf33add82aa9ab4b96f66612c65b0dfbbf62788718af85d5a34f20b`  **Líneas:** 163 · **Bytes:** 9033 · **Imports:** 45 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Sección de Configuración para Backup & Restore.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`android.app.Activity`, `android.net.Uri`, `android.widget.Toast`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.size`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Download`, `androidx.compose.material.icons.filled.Upload`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.Icon`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.rememberCoroutineScope`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppAlertDialog`, `com.example.mynotes.data.AppDataBackupManager`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.launch`, `java.text.SimpleDateFormat`, `java.util.Date`, `java.util.Locale`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 48 | `fun` | `BackupRestoreSection` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 8 aparición/apariciones.
- **Coroutines:** 5 aparición/apariciones.
- **safe calls:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.AppDataBackupManager`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.AppAlertDialog`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`

## 6. Recursos Android referenciados

- **R.string:** `backup_cancel`, `backup_export_button`, `backup_export_skipped`, `backup_export_success`, `backup_import_button`, `backup_import_confirm_action`, `backup_import_confirm_message`, `backup_import_confirm_title`, `backup_import_success`, `backup_operation_error` ×2, `backup_processing`, `backup_restore_description`, `backup_restore_title`, `backup_unknown_error` ×2

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
