package com.example.mynotes.studio
import com.example.mynotes.data.Note
import org.json.*
import org.junit.Assert.*
import org.junit.Test
class PlusRulesTest{
 private fun clause(field:String,value:String)=JSONObject().put("field",field).put("value",value).put("op","equals")
 @Test fun andOrCombineTagsAndPropertiesWithoutFlatteningTheirMeaning(){
  val note=Note(title="Work",content="Plan");val meta=JSONObject().put("tags",JSONArray(listOf("Ideas","Study"))).put("properties",JSONObject().put("team","Blue"))
  val rule=JSONObject().put("relation","and").put("clauses",JSONArray(listOf(clause("tag","ideas"),clause("property","Red").put("key","team"))))
  assertFalse(PlusRules.matches(note,meta,rule,false));rule.put("relation","or");assertTrue(PlusRules.matches(note,meta,rule,false))
 }
 @Test fun datesAndMediaRequireValidTypedValues(){
  val note=Note(title="",content="",createdAt=1000)
  val rule=JSONObject().put("clauses",JSONArray(listOf(clause("after","999"),clause("before","1001"),clause("media","true"))))
  assertTrue(PlusRules.matches(note,JSONObject(),rule,true));assertFalse(PlusRules.matches(note,JSONObject(),rule,false))
  rule.getJSONArray("clauses").getJSONObject(0).put("value","invalid");assertFalse(PlusRules.matches(note,JSONObject(),rule,true))
 }
}
