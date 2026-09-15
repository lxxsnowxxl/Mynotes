# BackupRestoreSection.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 330 → 163 (50.6% menos)

## Responsabilidad

Sección de Configuración dedicada a exportar/importar respaldos y a presentar los controles asociados.

## Papel dentro de la arquitectura

Es UI: recibe callbacks/estado y evita colocar toda la interfaz de respaldo directamente dentro de SettingsScreen.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppAlertDialog`, `com.example.mynotes.data.AppDataBackupManager`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Compose:** `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.size`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Download`, `androidx.compose.material.icons.filled.Upload`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.Icon`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`….

**Android/Jetpack:** `android.app.Activity`, `android.net.Uri`, `android.widget.Toast`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.launch`, `java.text.SimpleDateFormat`, `java.util.Date`, `java.util.Locale`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 50 | composable | `BackupRestoreSection` | `fun BackupRestoreSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color, graphicColor: Color) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
