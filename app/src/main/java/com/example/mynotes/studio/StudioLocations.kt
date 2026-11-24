package com.example.mynotes.studio

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.mynotes.reminders.*
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun <T> com.google.android.gms.tasks.Task<T>.studioAwait():T=suspendCancellableCoroutine{c->addOnSuccessListener{if(c.isActive)c.resume(it)}
addOnFailureListener{if(c.isActive)c.resumeWithException(it)}
addOnCanceledListener{c.cancel()}}
internal object StudioGeofences{
 private fun operation(context:Context)=PendingIntent.getBroadcast(context,9174,Intent(context,StudioGeofenceReceiver::class.java).setAction("com.example.mynotes.STUDIO_GEOFENCE"),PendingIntent.FLAG_UPDATE_CURRENT or if(Build.VERSION.SDK_INT>=31)PendingIntent.FLAG_MUTABLE else 0)
 fun allowed(context:Context)=ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED&&(Build.VERSION.SDK_INT<29||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED)
 @SuppressLint("MissingPermission") suspend fun register(context:Context,row:JSONObject){check(allowed(context)){"Allow precise location and background location in app permissions before enabling this reminder"}
val fence=Geofence.Builder().setRequestId(row.getString("id")).setCircularRegion(row.getDouble("lat"),row.getDouble("lon"),row.optInt("radius",200).coerceIn(100,10000).toFloat()).setTransitionTypes(if(row.optBoolean("exit"))Geofence.GEOFENCE_TRANSITION_EXIT else Geofence.GEOFENCE_TRANSITION_ENTER).setExpirationDuration(Geofence.NEVER_EXPIRE).setNotificationResponsiveness(60000).build()
withTimeout(8000){LocationServices.getGeofencingClient(context).addGeofences(GeofencingRequest.Builder().setInitialTrigger(0).addGeofence(fence).build(),operation(context)).studioAwait()}}
 suspend fun remove(context:Context,id:String){withTimeout(8000){LocationServices.getGeofencingClient(context).removeGeofences(listOf(id)).studioAwait()}}
 fun restore(context:Context,done:()->Unit){CoroutineScope(SupervisorJob()+Dispatchers.IO).launch{try{if(allowed(context)){val store=StudioStore.get(context)
store.load()
withTimeoutOrNull(8000){LocationServices.getGeofencingClient(context).removeGeofences(operation(context)).studioAwait()
store.state.value.all("locations").filter{it.optBoolean("enabled")}.take(100).forEach{row->runCatching{register(context,row)}.onFailure{StudioJobs.failure("Location reminders",it)}}}}}catch(error:Exception){StudioJobs.failure("Location reminders",error)}finally{done()}}}
}
class StudioGeofenceReceiver:BroadcastReceiver(){override fun onReceive(context:Context,intent:Intent){val event=GeofencingEvent.fromIntent(intent)?:return
if(event.hasError())return
val pending=goAsync()
CoroutineScope(SupervisorJob()+Dispatchers.IO).launch{try{val store=StudioStore.get(context)
store.load()
val repository=ReminderRepository.getInstance(context)
event.triggeringGeofences.orEmpty().forEach{fence->val row=store.state.value.get("locations",fence.requestId)
val expected=if(row.optBoolean("exit"))Geofence.GEOFENCE_TRANSITION_EXIT else Geofence.GEOFENCE_TRANSITION_ENTER
if(row.optBoolean("enabled")&&event.geofenceTransition==expected&&System.currentTimeMillis()-row.optLong("last")>600000){val id=row.optLong("reminderId")
val r=repository.getReminder(id)
if(r!=null&&r.enabled){repository.snooze(id,1)
store.edit("locations",fence.requestId){it.put("last",System.currentTimeMillis())}
repository.recordEvent(r,"location")}}}}finally{pending.finish()}}}}
