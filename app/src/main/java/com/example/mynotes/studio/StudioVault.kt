package com.example.mynotes.studio

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.*

internal object StudioVault{
 private val magic=byteArrayOf(77,89,78,86,1)
private val lock=Mutex()
 internal fun key(password:CharArray,salt:ByteArray):SecretKeySpec{val spec=PBEKeySpec(password,salt,210000,256)
try{val bytes=try{SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded}catch(_:java.security.NoSuchAlgorithmException){val pass=String(password).toByteArray(Charsets.UTF_8)
try{val mac=Mac.getInstance("HmacSHA256")
mac.init(SecretKeySpec(pass,"HmacSHA256"))
var u=mac.doFinal(salt+byteArrayOf(0,0,0,1))
val result=u.copyOf()
repeat(209999){u=mac.doFinal(u)
for(i in result.indices)result[i]=(result[i].toInt() xor u[i].toInt()).toByte()}
u.fill(0)
result}finally{pass.fill(0)}}
return try{SecretKeySpec(bytes,"AES")}finally{bytes.fill(0)}}finally{spec.clearPassword()}}
 fun encrypt(plain:ByteArray,password:CharArray):ByteArray{require(password.size>=8)
val salt=ByteArray(16).also{SecureRandom().nextBytes(it)}
val iv=ByteArray(12).also{SecureRandom().nextBytes(it)}
val cipher=Cipher.getInstance("AES/GCM/NoPadding")
cipher.init(Cipher.ENCRYPT_MODE,key(password,salt),GCMParameterSpec(128,iv))
cipher.updateAAD(magic)
return magic+salt+iv+cipher.doFinal(plain)}
 fun decrypt(bytes:ByteArray,password:CharArray):ByteArray{require(bytes.size>=49&&bytes.copyOfRange(0,5).contentEquals(magic))
val cipher=Cipher.getInstance("AES/GCM/NoPadding")
cipher.init(Cipher.DECRYPT_MODE,key(password,bytes.copyOfRange(5,21)),GCMParameterSpec(128,bytes.copyOfRange(21,33)))
cipher.updateAAD(magic)
return cipher.doFinal(bytes,33,bytes.size-33)}
 private fun file(context:Context,id:String):File{require(id.matches(Regex("[a-zA-Z0-9-]{1,80}")))
return File(context.filesDir,"studio_vault/$id.vault").apply{parentFile!!.mkdirs()}}
 suspend fun ids(context:Context)=withContext(Dispatchers.IO){StudioStore.get(context).load();File(context.filesDir,"studio_vault").listFiles().orEmpty().filter{it.extension=="vault"&&PlusProfiles.ownsFile(context,"vault",it.nameWithoutExtension)}.map{it.nameWithoutExtension}}
 suspend fun save(context:Context,id:String,password:CharArray,value:JSONObject)=withContext(Dispatchers.IO+NonCancellable){com.example.mynotes.studio.StudioFiles.write {lock.withLock{PlusProfiles.claimFile(context,"vault",id);val plain=value.toString().toByteArray()
val encrypted=try{encrypt(plain,password)}finally{plain.fill(0)}
val atomic=AtomicFile(file(context,id))
val output=atomic.startWrite()
try{output.write(encrypted)
atomic.finishWrite(output)}catch(error:Throwable){atomic.failWrite(output)
throw error}}}}
 suspend fun open(context:Context,id:String,password:CharArray)=withContext(Dispatchers.IO){lock.withLock{require(PlusProfiles.ownsFile(context,"vault",id));val source=file(context,id)
require(source.length()<16*1024*1024)
val plain=decrypt(AtomicFile(source).openRead().use{it.readBytes()},password)
try{JSONObject(plain.toString(Charsets.UTF_8))}finally{plain.fill(0)}}}
 suspend fun delete(context:Context,id:String)=withContext(Dispatchers.IO){StudioFiles.write { lock.withLock{AtomicFile(file(context,id)).delete();File(context.filesDir,"studio_vault/assets/$id").deleteRecursively();PlusBiometrics.disable(context,id)} }}
}
