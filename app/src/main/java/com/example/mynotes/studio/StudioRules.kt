package com.example.mynotes.studio

import com.example.mynotes.data.Note
import org.json.JSONObject

internal object StudioRules{
 fun matches(note:Note,meta:JSONObject,rule:JSONObject,media:Boolean):Boolean{
  if(rule.rows("clauses").isNotEmpty()) return PlusRules.matches(note,meta,rule,media)
  if(rule.optString("query").split(Regex("\\s+")).filter{it.isNotBlank()}.any{!note.title.contains(it,true)&&!note.content.contains(it,true)})return false
  val folder=rule.optString("folder")
if(folder.isNotEmpty()&&note.folderId!=folder)return false
  val stage=rule.optString("stage")
if(stage.isNotEmpty()&&meta.optString("stage","pending")!=stage)return false
  if(rule.optBoolean("media")&&!media)return false
  if(rule.optLong("after")>note.createdAt||rule.optLong("before").let{it>0&&note.createdAt>it})return false
  val key=rule.optString("property")
if(key.isNotBlank()&&(meta.optJSONObject("properties")?.has(key)!=true||!meta.getJSONObject("properties").optString(key).contains(rule.optString("value"),true)))return false
  return true
 }
}
