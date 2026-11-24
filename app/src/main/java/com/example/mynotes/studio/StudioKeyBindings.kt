package com.example.mynotes.studio


internal object StudioKeys{
 val defaults=linkedMapOf("commands" to "Ctrl+P","new_note" to "Ctrl+N","search" to "Ctrl+Shift+F","close" to "Ctrl+W","save" to "Ctrl+S","undo" to "Ctrl+Z","redo" to "Ctrl+Y","find" to "Ctrl+F")
 fun normalize(raw:String):String?{val parts=raw.split('+').map(String::trim)
if(parts.size<2)return null
val key=parts.last().uppercase()
if(!key.matches(Regex("[A-Z0-9]|F(?:[1-9]|1[0-2])")))return null
val mods=parts.dropLast(1).map{it.lowercase()}.toSet()
if(mods.any{it !in setOf("ctrl","meta","alt","shift")}||mods.none{it in setOf("ctrl","meta","alt")})return null
return listOf("Ctrl" to "ctrl","Meta" to "meta","Alt" to "alt","Shift" to "shift").filter{it.second in mods}.map{it.first}.plus(key).joinToString("+")}
 fun matches(chord:String,code:Int,ctrl:Boolean,meta:Boolean,alt:Boolean,shift:Boolean):Boolean{val parts=normalize(chord)?.split('+')?:return false
val key=parts.last()
val expected=if(key.length==1&&key[0] in 'A'..'Z')29+key[0].code-'A'.code else if(key.length==1)7+key.toInt() else 131+key.drop(1).toInt()-1
return code==expected&&ctrl==("Ctrl" in parts)&&meta==("Meta" in parts)&&alt==("Alt" in parts)&&shift==("Shift" in parts)}
}
