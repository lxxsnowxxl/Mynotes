package com.example.mynotes.studio

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files

class PlusCryptoTest {
    @Test fun streamingContainerRoundTripsBinaryData() = runBlocking {
        val source = ByteArray(131173) { (it * 73).toByte() }; val password = "contraseña de respaldo".toCharArray()
        val output = ByteArrayOutputStream(); PlusCrypto.encrypt(ByteArrayInputStream(source), output, password)
        assertTrue(output.toByteArray().copyOfRange(0, 5).contentEquals(PlusCrypto.magic))
        val file = Files.createTempFile("mynotes-roundtrip", ".bin").toFile()
        try { PlusCrypto.decrypt(ByteArrayInputStream(output.toByteArray()), file, password); assertArrayEquals(source, file.readBytes()) } finally { file.delete() }
    }
    @Test fun authenticationFailureNeverLeavesADecryptedStage() = runBlocking {
        val output = ByteArrayOutputStream(); val pass = "long password".toCharArray()
        PlusCrypto.encrypt(ByteArrayInputStream("secret data".toByteArray()), output, pass)
        val bytes = output.toByteArray(); bytes[bytes.lastIndex] = (bytes.last().toInt() xor 1).toByte()
        val file = Files.createTempFile("mynotes-tampered", ".bin").toFile()
        var rejected = false
        try { PlusCrypto.decrypt(ByteArrayInputStream(bytes), file, pass) } catch (_: javax.crypto.AEADBadTagException) { rejected = true }
        assertTrue(rejected); assertFalse(file.exists())
    }
    @Test fun sizeLimitStopsEncryption() = runBlocking {
        var rejected = false
        try { PlusCrypto.encrypt(ByteArrayInputStream(ByteArray(100)), ByteArrayOutputStream(), "long password".toCharArray(), 50) } catch (_: IllegalArgumentException) { rejected = true }
        assertTrue(rejected)
    }
}
