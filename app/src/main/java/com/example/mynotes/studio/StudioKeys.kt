package com.example.mynotes.studio

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext

@Composable internal fun studioKeys(actions:Map<String,()->Unit>):Modifier{
 val context=LocalContext.current
val store=remember{StudioStore.get(context)}
val latest by rememberUpdatedState(actions)
 return Modifier.onPreviewKeyEvent{event->
  if(event.type!=KeyEventType.KeyDown)false else{
   val overrides=store.state.value.get("preferences","keys")
   val hit=StudioKeys.defaults.entries.firstOrNull{(action,default)->val chord=overrides.optString(action,default)
action in latest&&(StudioKeys.matches(chord,event.nativeKeyEvent.keyCode,event.isCtrlPressed,event.isMetaPressed,event.isAltPressed,event.isShiftPressed)||(!overrides.has(action)&&event.isMetaPressed&&!event.isCtrlPressed&&StudioKeys.matches(chord.replace("Ctrl+","Meta+"),event.nativeKeyEvent.keyCode,false,true,event.isAltPressed,event.isShiftPressed)))}?.key
   val action=hit?:"redo".takeIf{it in latest&&!overrides.has(it)&&event.key==Key.Z&&event.isShiftPressed&&(event.isCtrlPressed||event.isMetaPressed)&&!event.isAltPressed}
   if(action!=null){latest[action]?.invoke()
true}else false
  }
 }
}
