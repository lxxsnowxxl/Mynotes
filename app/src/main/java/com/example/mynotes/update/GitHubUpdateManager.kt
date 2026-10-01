package com.example.mynotes.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Pequeño cliente de actualización para instalaciones distribuidas desde GitHub.
 *
 * MyNotes no instala nada silenciosamente: consulta la última GitHub Release,
 * descarga el APK publicado como asset y entrega ese APK al instalador oficial
 * de Android. La confirmación final siempre pertenece al sistema y al usuario.
 *
 * Para que una actualización pueda instalarse desde aquí, el repositorio debe
 * publicar una Release con una etiqueta de versión superior a la instalada
 * (por ejemplo v1.1) y adjuntar al menos un archivo cuyo nombre termine en .apk.
 */
object GitHubUpdateManager {

    const val REPOSITORY_URL = "https://github.com/lxxsnowxxl/Mynotes"
    const val RELEASES_URL = "$REPOSITORY_URL/releases"
    private const val LATEST_RELEASE_API = "https://api.github.com/repos/lxxsnowxxl/Mynotes/releases/latest"
    private const val USER_AGENT = "MyNotes-Android-Updater"
    private const val UPDATES_CACHE_DIR = "app_updates"

    data class Release(
        val version: String,
        val title: String,
        val notes: String,
        val htmlUrl: String,
        val apkDownloadUrl: String?
    )

    sealed class CheckResult {
        data class UpdateAvailable(val release: Release, val currentVersion: String) : CheckResult()
        data class UpToDate(val currentVersion: String, val latestVersion: String) : CheckResult()
        data class NoPublishedRelease(val currentVersion: String) : CheckResult()
        data class Failure(val reason: String) : CheckResult()
    }

    /** Devuelve el versionName instalado sin depender directamente de BuildConfig. */
    @Suppress("DEPRECATION")
    fun currentVersionName(context: Context): String = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
    }.getOrDefault("?")

    /**
     * Consulta /releases/latest. GitHub excluye borradores y prereleases de este
     * endpoint, por lo que el botón usa únicamente una Release pública estable.
     */
    suspend fun checkForUpdate(context: Context): CheckResult = withContext(Dispatchers.IO) {
        val current = currentVersionName(context)
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(LATEST_RELEASE_API).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 12_000
                readTimeout = 15_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            }

            when (val responseCode = connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val payload = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    val json = JSONObject(payload)
                    val remoteVersion = json.optString("tag_name").ifBlank { json.optString("name") }
                    val releaseUrl = json.optString("html_url").ifBlank { RELEASES_URL }
                    val title = json.optString("name").ifBlank { remoteVersion }
                    val notes = json.optString("body")
                    val assets = json.optJSONArray("assets")
                    var apkUrl: String? = null
                    if (assets != null) {
                        for (index in 0 until assets.length()) {
                            val asset = assets.optJSONObject(index) ?: continue
                            val name = asset.optString("name")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                apkUrl = asset.optString("browser_download_url").takeIf { it.isNotBlank() }
                                if (apkUrl != null) break
                            }
                        }
                    }

                    val release = Release(
                        version = remoteVersion.ifBlank { "?" },
                        title = title,
                        notes = notes,
                        htmlUrl = releaseUrl,
                        apkDownloadUrl = apkUrl
                    )

                    if (isVersionNewer(remoteVersion, current)) {
                        CheckResult.UpdateAvailable(release, current)
                    } else {
                        CheckResult.UpToDate(current, release.version)
                    }
                }

                HttpURLConnection.HTTP_NOT_FOUND -> CheckResult.NoPublishedRelease(current)
                else -> CheckResult.Failure("GitHub HTTP $responseCode")
            }
        } catch (error: Exception) {
            CheckResult.Failure(error.message ?: error.javaClass.simpleName)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Compara etiquetas como 1.0, v1.2.3 o release-2.0.1 por sus componentes
     * numéricos. Si ninguna etiqueta contiene números, se considera distinta
     * sólo cuando su texto normalizado difiere.
     */
    private fun isVersionNewer(remote: String, current: String): Boolean {
        val remoteParts = Regex("\\d+").findAll(remote).map { it.value.toLongOrNull() ?: 0L }.toList()
        val currentParts = Regex("\\d+").findAll(current).map { it.value.toLongOrNull() ?: 0L }.toList()
        if (remoteParts.isEmpty() && currentParts.isEmpty()) {
            return remote.trim().removePrefix("v") != current.trim().removePrefix("v")
        }
        val count = maxOf(remoteParts.size, currentParts.size)
        for (index in 0 until count) {
            val remotePart = remoteParts.getOrElse(index) { 0L }
            val currentPart = currentParts.getOrElse(index) { 0L }
            if (remotePart != currentPart) return remotePart > currentPart
        }
        return false
    }

    /** Descarga el asset APK a cacheDir/app_updates sin cargarlo entero en RAM. */
    suspend fun downloadApk(context: Context, release: Release): File = withContext(Dispatchers.IO) {
        val downloadUrl = release.apkDownloadUrl ?: throw IOException("The GitHub Release has no APK asset")
        requireTrustedHttpsUrl(downloadUrl)

        val updateDirectory = File(context.cacheDir, UPDATES_CACHE_DIR).apply {
            if (!exists() && !mkdirs()) throw IOException("Unable to create update cache directory")
        }
        val safeVersion = release.version.replace(Regex("[^A-Za-z0-9._-]"), "_").ifBlank { "latest" }
        val destination = File(updateDirectory, "MyNotes-$safeVersion.apk")
        val temporary = File(updateDirectory, "MyNotes-$safeVersion.download")
        if (temporary.exists()) temporary.delete()

        var connection: HttpURLConnection? = null
        try {
            connection = (URL(downloadUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                instanceFollowRedirects = true
                connectTimeout = 15_000
                readTimeout = 30_000
                setRequestProperty("Accept", "application/octet-stream")
                setRequestProperty("User-Agent", USER_AGENT)
            }
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) throw IOException("APK download failed: HTTP $responseCode")

            BufferedInputStream(connection.inputStream, 64 * 1024).use { input ->
                BufferedOutputStream(FileOutputStream(temporary), 64 * 1024).use { output ->
                    input.copyTo(output, 64 * 1024)
                }
            }
            if (temporary.length() < 1024L) throw IOException("Downloaded APK is unexpectedly small")

            // Un APK es un contenedor ZIP; PK evita entregar al instalador una página HTML de error.
            temporary.inputStream().use { stream ->
                if (stream.read() != 'P'.code || stream.read() != 'K'.code) {
                    throw IOException("Downloaded file is not a valid APK/ZIP container")
                }
            }

            if (destination.exists()) destination.delete()
            if (!temporary.renameTo(destination)) {
                temporary.copyTo(destination, overwrite = true)
                temporary.delete()
            }
            destination
        } finally {
            connection?.disconnect()
            if (temporary.exists()) temporary.delete()
        }
    }

    /** Android 8+ exige que el usuario autorice explícitamente a MyNotes como origen de APK. */
    fun canInstallDownloadedPackages(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()

    fun unknownSourcesSettingsIntent(context: Context): Intent = Intent(
        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
        Uri.parse("package:${context.packageName}")
    )

    /** Abre el instalador del sistema. Nunca confirma la instalación automáticamente. */
    fun launchSystemInstaller(context: Context, apkFile: File): Boolean = runCatching {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        true
    }.getOrDefault(false)

    fun openReleasePage(context: Context, url: String = RELEASES_URL): Boolean = runCatching {
        val safeUrl = url.takeIf { it.startsWith("https://github.com/") } ?: RELEASES_URL
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    }.getOrDefault(false)

    private fun requireTrustedHttpsUrl(value: String) {
        val uri = Uri.parse(value)
        val host = uri.host.orEmpty().lowercase()
        val trustedHost = host == "github.com" || host.endsWith(".github.com") || host.endsWith(".githubusercontent.com")
        if (uri.scheme != "https" || !trustedHost) throw IOException("Untrusted update URL")
    }
}
