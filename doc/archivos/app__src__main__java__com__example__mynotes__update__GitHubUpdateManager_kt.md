# GitHubUpdateManager.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/update/GitHubUpdateManager.kt`  **SHA-256:** `28439ad2edaaa453050ade9c88515f1bfc0895b9d5502b965e368c87dfd79b30`  **Líneas:** 228 · **Bytes:** 10753 · **Imports:** 16 · **Declaraciones detectadas:** 14
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Consulta GitHub Releases, descarga APK y lanza instalador del sistema.
## 2. Package e imports

Package declarado: `com.example.mynotes.update`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.Intent`, `android.net.Uri`, `android.os.Build`, `android.provider.Settings`, `androidx.core.content.FileProvider`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `java.io.BufferedInputStream`, `java.io.BufferedOutputStream`, `java.io.File`, `java.io.FileOutputStream`, `java.io.IOException`, `java.net.HttpURLConnection`, `java.net.URL`

### Terceros / otros

`org.json.JSONObject`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 31 | `object` | `GitHubUpdateManager` | `object GitHubUpdateManager {` |
| 38 | `class` | `Release` | `` |
| 46 | `class` | `CheckResult` | `` |
| 48 | `class` | `UpdateAvailable` | `data class UpdateAvailable(val release: Release, val currentVersion: String) : CheckResult()` |
| 49 | `class` | `UpToDate` | `data class UpToDate(val currentVersion: String, val latestVersion: String) : CheckResult()` |
| 50 | `class` | `NoPublishedRelease` | `data class NoPublishedRelease(val currentVersion: String) : CheckResult()` |
| 51 | `class` | `Failure` | `data class Failure(val reason: String) : CheckResult()` |
| 55 | `fun` | `currentVersionName` | `@Suppress("DEPRECATION")` |
| 128 | `fun` | `isVersionNewer` | `private fun isVersionNewer(remote: String, current: String): Boolean {` |
| 196 | `fun` | `canInstallDownloadedPackages` | `fun canInstallDownloadedPackages(context: Context): Boolean =` |
| 198 | `fun` | `unknownSourcesSettingsIntent` | `` |
| 205 | `fun` | `launchSystemInstaller` | `fun launchSystemInstaller(context: Context, apkFile: File): Boolean = runCatching {` |
| 215 | `fun` | `openReleasePage` | `` |
| 221 | `fun` | `requireTrustedHttpsUrl` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 3 aparición/apariciones.
- **I/O/red:** 14 aparición/apariciones.
- **try/catch:** 3 aparición/apariciones.
- **safe calls:** 2 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
