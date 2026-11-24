package com.example.mynotes.studio

import android.content.Context
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.*
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/** Authenticated streaming container. Decryption only publishes after GCM verifies the final tag. */
internal object PlusCrypto {
    val magic = byteArrayOf(77, 89, 78, 69, 2)
    suspend fun encrypt(input: InputStream, output: OutputStream, password: CharArray, maximum: Long = 2L * 1024 * 1024 * 1024) {
        require(password.size >= 8)
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, StudioVault.key(password, salt), GCMParameterSpec(128, iv))
        cipher.updateAAD(magic)
        output.write(magic); output.write(salt); output.write(iv)
        val buffer = ByteArray(65536); var size = 0L
        try {
            while (true) {
                currentCoroutineContext().ensureActive()
                val count = input.read(buffer); if (count < 0) break
                size += count; require(size <= maximum) { "Encrypted file exceeds size limit" }
                cipher.update(buffer, 0, count)?.let { output.write(it) }
            }
            output.write(cipher.doFinal())
        } finally { buffer.fill(0) }
    }
    suspend fun decrypt(input: InputStream, target: File, password: CharArray, maximum: Long = 2L * 1024 * 1024 * 1024) {
        val source = DataInputStream(input)
        val header = ByteArray(5); source.readFully(header); require(header.contentEquals(magic)) { "Unsupported encrypted file" }
        val salt = ByteArray(16); source.readFully(salt)
        val iv = ByteArray(12); source.readFully(iv)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, StudioVault.key(password, salt), GCMParameterSpec(128, iv)); cipher.updateAAD(magic)
        target.parentFile?.mkdirs()
        val buffer = ByteArray(65536); var read = 0L; var written = 0L
        try {
            target.outputStream().use { output ->
                while (true) {
                    currentCoroutineContext().ensureActive()
                    val count = source.read(buffer); if (count < 0) break
                    read += count; require(read <= maximum + 64) { "Encrypted file exceeds size limit" }
                    cipher.update(buffer, 0, count)?.let { written += it.size; require(written <= maximum); output.write(it); it.fill(0) }
                }
                val tail = cipher.doFinal()
                try { written += tail.size; require(written <= maximum); output.write(tail) } finally { tail.fill(0) }
            }
        } catch (e: Throwable) { target.delete(); throw e } finally { buffer.fill(0) }
    }
}

internal object PlusBiometrics {
    private fun alias(id: String): String { require(id.matches(Regex("[a-zA-Z0-9-]{1,80}"))); return "mynotes.v316.vault.$id" }
    private fun preferences(context: Context) = context.getSharedPreferences("studio_biometrics", Context.MODE_PRIVATE)
    fun disable(context: Context, id: String) { preferences(context).edit().remove("$id.data").remove("$id.iv").commit(); runCatching { KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(alias(id)) } }
}
