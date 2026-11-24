package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mynotes.ui.components.AppAlertDialog
import org.json.JSONObject

@Composable internal fun StudioMath(formula:String){if(com.example.mynotes.ui.preview.LocalLayoutPreview.current!=null){Text(formula)
return}
AndroidView(factory={context->WebView(context).apply{setBackgroundColor(android.graphics.Color.TRANSPARENT)
settings.javaScriptEnabled=true
settings.blockNetworkLoads=true
settings.allowFileAccess=false
settings.allowContentAccess=false
webViewClient=WebViewClient()}},modifier=Modifier.fillMaxWidth().height(110.dp),update={view->val safe=JSONObject.quote(formula).replace("<","\\u003c").replace(">","\\u003e").replace("&","\\u0026")
val html="""<!doctype html><meta name="viewport" content="width=device-width,initial-scale=1"><link rel="stylesheet" href="file:///android_asset/studio_math/katex.min.css"><div id="m" style="overflow:auto"></div><script src="file:///android_asset/studio_math/katex.min.js"></script><script>katex.render($safe,document.getElementById('m'),{displayMode:true,throwOnError:false,trust:false});</script>"""
if(view.tag!=html){view.tag=html
view.loadDataWithBaseURL("file:///android_asset/studio_math/",html,"text/html","UTF-8",null)}},onRelease={it.stopLoading()
it.destroy()})}

@Composable internal fun StudioRichText(source:String,document:String,style:TextStyle,color:Color,fallback: @Composable (String) -> Unit){
 val lines=remember(source){source.lines()}
var reference by remember{mutableStateOf<String?>(null)}
val definitions=remember(document){Regex("(?m)^\\[\\^([^]]+)]: *(.*)$").findAll(document).associate{it.groupValues[1] to it.groupValues[2]}}
val primary=MaterialTheme.colorScheme.primary
 Column{var index=0
while(index<lines.size){
  if(lines[index].trim()=="$$"){val end=((index+1)until lines.size).firstOrNull{lines[it].trim()=="$$"}
if(end!=null){StudioMath(lines.subList(index+1,end).joinToString("\n"))
index=end+1
continue}}
  if(lines[index].trim().startsWith('|')&&lines.getOrNull(index+1)?.matches(Regex("^[| :\\-]+$"))==true){val rows=mutableListOf(lines[index].trim().trim('|').split('|'))
index+=2
while(index<lines.size&&lines[index].trim().startsWith('|')){rows+=lines[index].trim().trim('|').split('|')
index++}
Column(Modifier.horizontalScroll(rememberScrollState())){rows.forEachIndexed{r,row->Row{row.forEach{cell->Surface(Modifier.width(150.dp),border=androidx.compose.foundation.BorderStroke(.5.dp,MaterialTheme.colorScheme.outlineVariant),color=if(r==0)MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent){Text(cell.trim(),Modifier.padding(10.dp),style=style,color=color)}}}}}
continue}
  val start=index
index++
while(index<lines.size&&lines[index].trim()!="$$"&&!(lines[index].trim().startsWith('|')&&lines.getOrNull(index+1)?.matches(Regex("^[| :\\-]+$"))==true))index++
  val text=lines.subList(start,index).joinToString("\n")
val matches=Regex("\\[\\^([^]]+)]").findAll(text).toList()
if(matches.isEmpty())fallback(text)else Text(buildAnnotatedString{append(text)
matches.forEach{match->val id=match.groupValues[1]
addLink(LinkAnnotation.Clickable("footnote_$id",TextLinkStyles(SpanStyle(color=primary))){reference=id},match.range.first,match.range.last+1)}},style=style,color=color)
 }}
 reference?.let{id->AppAlertDialog(onDismissRequest={reference=null},title={Text("[$id]")},text={Text(definitions[id]?:"Reference not found")},confirmButton={TextButton(onClick={reference=null; UiSoundPlayer.playFallbackClick()}){Text("Close")}})}
}
