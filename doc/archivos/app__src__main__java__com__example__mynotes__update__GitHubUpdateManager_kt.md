# GitHubUpdateManager.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/update/GitHubUpdateManager.kt`  
**SHA-256:** `28439ad2edaaa453050ade9c88515f1bfc0895b9d5502b965e368c87dfd79b30`  
**Líneas:** 229  
**Package:** `com.example.mynotes.update`

## 1. Para qué existe este archivo

Comprueba GitHub Releases, compara versiones, descarga APK y abre el instalador oficial.

## 2. Tipos/clases declarados

- Línea **31** — `object GitHubUpdateManager`.
- Línea **39** — `data  class Release`.
- Línea **47** — `sealed  class CheckResult`.
- Línea **48** — `data  class UpdateAvailable`.
- Línea **49** — `data  class UpToDate`.
- Línea **50** — `data  class NoPublishedRelease`.
- Línea **51** — `data  class Failure`.

## 3. Estado, constantes y valores importantes

- **`REPOSITORY_URL`** (línea 32) inicia con `"https://github.com/lxxsnowxxl/Mynotes"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`RELEASES_URL`** (línea 34) inicia con `"$REPOSITORY_URL/releases"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`LATEST_RELEASE_API`** (línea 35) inicia con `"https://api.github.com/repos/lxxsnowxxl/Mynotes/releases/latest"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`USER_AGENT`** (línea 36) inicia con `"MyNotes-Android-Updater"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`UPDATES_CACHE_DIR`** (línea 37) inicia con `"app_updates"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`current`** (línea 65) inicia con `currentVersionName(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`connection`** (línea 66) inicia con `null`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`payload`** (línea 79) inicia con `connection.inputStream.bufferedReader(Charsets.UTF_8`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`json`** (línea 80) inicia con `JSONObject(payload`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`remoteVersion`** (línea 81) inicia con `json.optString("tag_name"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`releaseUrl`** (línea 82) inicia con `json.optString("html_url"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`title`** (línea 83) inicia con `json.optString("name"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`notes`** (línea 84) inicia con `json.optString("body"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`assets`** (línea 85) inicia con `json.optJSONArray("assets"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`apkUrl`** (línea 86) inicia con `null`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`asset`** (línea 89) inicia con `assets.optJSONObject(index`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`name`** (línea 90) inicia con `asset.optString("name"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`release`** (línea 97) inicia con `Release(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`remoteParts`** (línea 129) inicia con `Regex("\\d+"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`currentParts`** (línea 130) inicia con `Regex("\\d+"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`count`** (línea 134) inicia con `maxOf(remoteParts.size`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`remotePart`** (línea 136) inicia con `remoteParts.getOrElse(index`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`currentPart`** (línea 137) inicia con `currentParts.getOrElse(index`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`downloadUrl`** (línea 145) inicia con `release.apkDownloadUrl ?: throw IOException("The GitHub Release has no APK asset"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`updateDirectory`** (línea 147) inicia con `File(context.cacheDir`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`safeVersion`** (línea 151) inicia con `release.version.replace(Regex("[^A-Za-z0-9._-]"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`destination`** (línea 152) inicia con `File(updateDirectory`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`temporary`** (línea 153) inicia con `File(updateDirectory`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`responseCode`** (línea 166) inicia con `connection.responseCode`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`uri`** (línea 206) inicia con `FileProvider.getUriForFile(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`intent`** (línea 207) inicia con `Intent(Intent.ACTION_VIEW`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`safeUrl`** (línea 217) inicia con `url.takeIf { it.startsWith("https://github.com/"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`host`** (línea 224) inicia con `uri.host.orEmpty(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`trustedHost`** (línea 225) inicia con `host == "github.com" || host.endsWith(".github.com"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `currentVersionName` — líneas 56–56

**Firma:** `fun currentVersionName(context: Context): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.

### `checkForUpdate` — líneas 64–64

**Firma:** `suspend fun checkForUpdate(context: Context): CheckResult`

Consulta la API de la última Release estable, extrae tag/notas/asset APK, compara con versionName instalado y devuelve un resultado tipado.

**Entradas:**
- `context: Context`

**Salida:** CheckResult.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.

**Operaciones/funciones que coordina:** `withContext`.

### `isVersionNewer` — líneas 128–141

**Firma:** `private fun isVersionNewer(remote: String, current: String): Boolean`

Evalúa una condición y devuelve un booleano utilizado para decidir una ruta posterior del flujo.

**Entradas:**
- `remote: String`
- `current: String`

**Salida:** Boolean.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `Regex`, `findAll`, `toLongOrNull`, `toList`, `isEmpty`, `trim`, `removePrefix`, `maxOf`, `getOrElse`.

### `downloadApk` — líneas 144–144

**Firma:** `suspend fun downloadApk(context: Context, release: Release): File`

Descarga el APK por streaming a cacheDir/app_updates usando archivo temporal antes de publicar el destino final.

**Entradas:**
- `context: Context`
- `release: Release`

**Salida:** File.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.

**Operaciones/funciones que coordina:** `withContext`.

### `canInstallDownloadedPackages` — líneas 196–196

**Firma:** `fun canInstallDownloadedPackages(context: Context): Boolean`

Evalúa una condición y devuelve un booleano utilizado para decidir una ruta posterior del flujo.

**Entradas:**
- `context: Context`

**Salida:** Boolean.

### `unknownSourcesSettingsIntent` — líneas 199–199

**Firma:** `fun unknownSourcesSettingsIntent(context: Context): Intent`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`

**Salida:** Intent.

**Efectos/APIs observados en el cuerpo:**
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`.

### `launchSystemInstaller` — líneas 205–205

**Firma:** `fun launchSystemInstaller(context: Context, apkFile: File): Boolean`

Expone el APK mediante FileProvider y abre ACTION_VIEW al instalador oficial; la app no instala silenciosamente.

**Entradas:**
- `context: Context`
- `apkFile: File`

**Salida:** Boolean.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.

### `openReleasePage` — líneas 216–216

**Firma:** `fun openReleasePage(context: Context, url: String = RELEASES_URL): Boolean`

Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.

**Entradas:**
- `context: Context`
- `url: String = RELEASES_URL`

**Salida:** Boolean.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.

### `requireTrustedHttpsUrl` — líneas 222–227

**Firma:** `private fun requireTrustedHttpsUrl(value: String)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `value: String`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `parse`, `orEmpty`, `lowercase`, `endsWith`, `IOException`.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Ejecuta trabajo de I/O fuera del hilo principal.
- Realiza acceso de red HTTP.
- Accede al sistema de archivos interno/cache.
- Inicia o prepara navegación/acción mediante Intent.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `currentVersionName` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
2. `checkForUpdate` — Consulta la API de la última Release estable, extrae tag/notas/asset APK, compara con versionName instalado y devuelve un resultado tipado.
3. `downloadApk` — Descarga el APK por streaming a cacheDir/app_updates usando archivo temporal antes de publicar el destino final.
4. `canInstallDownloadedPackages` — Evalúa una condición y devuelve un booleano utilizado para decidir una ruta posterior del flujo.
5. `unknownSourcesSettingsIntent` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `launchSystemInstaller` — Expone el APK mediante FileProvider y abre ACTION_VIEW al instalador oficial; la app no instala silenciosamente.
7. `openReleasePage` — Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- Mantener timeouts, límites de descarga, cierre de conexiones y fallback ante sitios que bloqueen scraping.
- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.

## 10. Resumen en lenguaje sencillo

En términos simples: Comprueba GitHub Releases, compara versiones, descarga APK y abre el instalador oficial. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
