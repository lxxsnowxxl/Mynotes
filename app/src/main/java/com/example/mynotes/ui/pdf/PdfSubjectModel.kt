package com.example.mynotes.ui.pdf

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.OptionalModuleApi
import com.google.android.gms.common.moduleinstall.InstallStatusListener
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Espera el modelo opcional; aceptar la solicitud no equivale a instalarlo. */
internal object PdfSubjectModel {
    class PlayServicesUnavailableException(val statusCode: Int) :
        Exception("Google Play services unavailable: $statusCode")

    class ModelUnavailableException(cause: Throwable? = null) :
        Exception("Subject segmentation model is not ready", cause)

    class NoInternetConnectionException :
        Exception("Internet connection is required to download the subject segmentation model")

    /**
     * Google Play services puede marcar el módulo como instalado antes de que ML Kit
     * termine de enlazarlo. Este estado transitorio se ha observado públicamente en
     * Subject Segmentation y debe reintentarse, no tratarse como un fallo definitivo.
     */
    fun isTemporarilyUnavailable(error: Throwable): Boolean {
        var current: Throwable? = error
        while (current != null) {
            if (current is com.google.mlkit.common.MlKitException &&
                current.errorCode == com.google.mlkit.common.MlKitException.UNAVAILABLE
            ) return true
            val message = current.message.orEmpty().lowercase()
            if (message.contains("waiting for the subject segmentation optional module") ||
                message.contains("optional module to be downloaded") ||
                message.contains("failed to init module subject segmenter")
            ) return true
            current = current.cause
        }
        return false
    }

    /** Devuelve true si hubo que instalar el modelo y renovar el segmentador. */
    suspend fun prepare(
        context: Context,
        api: OptionalModuleApi,
        onDownload: suspend () -> Unit
    ): Boolean {
        val appContext = context.applicationContext
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext)
        if (status != ConnectionResult.SUCCESS) throw PlayServicesUnavailableException(status)

        return try {
            withTimeoutOrNull(90_000L) {
                val client = ModuleInstall.getClient(appContext)
                if (client.areModulesAvailable(api).awaitResult().areModulesAvailable()) {
                    false
                } else {
                    if (!appContext.hasValidatedInternetConnection()) {
                        throw NoInternetConnectionException()
                    }
                    onDownload()
                    awaitInstallation(client, api)
                    // STATE_COMPLETED confirma la instalación del módulo, pero no siempre
                    // que ML Kit ya pueda abrirlo. Esperar una disponibilidad estable
                    // reduce la carrera conocida entre ModuleInstall y SubjectSegmenter.
                    awaitStableAvailability(client, api)
                    true
                }
            } ?: throw ModelUnavailableException()
        } catch (error: CancellationException) {
            throw error
        } catch (error: NoInternetConnectionException) {
            throw error
        } catch (error: Exception) {
            throw ModelUnavailableException(error)
        }
    }

    private fun Context.hasValidatedInternetConnection(): Boolean {
        val manager = getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private suspend fun awaitStableAvailability(client: ModuleInstallClient, api: OptionalModuleApi) {
        repeat(8) { attempt ->
            if (client.areModulesAvailable(api).awaitResult().areModulesAvailable()) {
                // Una pequeña ventana adicional permite que el proceso remoto de ML Kit
                // termine de registrar el módulo recién instalado.
                delay(300L + attempt * 100L)
                return
            }
            delay(400L + attempt * 200L)
        }
        throw ModelUnavailableException()
    }

    private suspend fun awaitInstallation(client: ModuleInstallClient, api: OptionalModuleApi) {
        val completed = CompletableDeferred<Unit>()
        val listener = InstallStatusListener { update ->
            when (update.installState) {
                InstallState.STATE_COMPLETED -> completed.complete(Unit)
                InstallState.STATE_FAILED, InstallState.STATE_CANCELED ->
                    completed.completeExceptionally(
                        IllegalStateException("Model installation failed: ${update.errorCode}")
                    )
                else -> Unit
            }
        }
        try {
            val request = ModuleInstallRequest.newBuilder().addApi(api).setListener(listener).build()
            client.installModules(request)
                .addOnSuccessListener { response ->
                    if (response.areModulesAlreadyInstalled()) completed.complete(Unit)
                    // La aceptación puede llegar después del cierre de la pantalla o del plazo.
                    if (!completed.isActive) client.unregisterListener(listener)
                }
                .addOnFailureListener { completed.completeExceptionally(it) }
                .addOnCanceledListener { completed.completeExceptionally(ModelUnavailableException()) }
            completed.await()
        } finally {
            completed.cancel()
            client.unregisterListener(listener)
        }
    }

    private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (continuation.isActive) {
                if (task.isSuccessful) continuation.resume(task.result)
                else continuation.resumeWithException(task.exception ?: ModelUnavailableException())
            }
        }
    }
}
