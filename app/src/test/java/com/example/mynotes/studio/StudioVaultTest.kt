package com.example.mynotes.studio

import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class StudioVaultTest {
    @Test fun vaultRoundTripSupportsUnicodeAndBinaryContent() {
        val content="José · 私密 · 🔒\u0000".toByteArray();val password="contraseña segura".toCharArray()
        assertArrayEquals(content,StudioVault.decrypt(StudioVault.encrypt(content,password),password))
    }
    @Test fun vaultUsesFreshSaltAndNonceForEverySave() {
        val pass="strong password".toCharArray();val bytes="same content".toByteArray()
        assertFalse(StudioVault.encrypt(bytes,pass).contentEquals(StudioVault.encrypt(bytes,pass)))
    }
    @Test fun wrongPasswordAndTamperedCiphertextFailAuthentication() {
        val bytes=StudioVault.encrypt("secret".toByteArray(),"password one".toCharArray())
        assertThrows(javax.crypto.AEADBadTagException::class.java){StudioVault.decrypt(bytes,"password two".toCharArray())}
        bytes[bytes.lastIndex]=(bytes.last().toInt() xor 1).toByte()
        assertThrows(javax.crypto.AEADBadTagException::class.java){StudioVault.decrypt(bytes,"password one".toCharArray())}
    }
    @Test fun shortPasswordAndUnrecognizedVaultHeaderAreRejected() {
        assertThrows(IllegalArgumentException::class.java){StudioVault.encrypt(byteArrayOf(),"short".toCharArray())}
        assertThrows(IllegalArgumentException::class.java){StudioVault.decrypt(ByteArray(49),"password".toCharArray())}
    }
}
