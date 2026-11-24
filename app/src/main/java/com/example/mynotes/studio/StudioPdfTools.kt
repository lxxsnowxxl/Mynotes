package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import android.graphics.RectF
import android.net.Uri
import android.speech.tts.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.pdf.*
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.StyledSettingsSlider
import kotlinx.coroutines.*
import java.io.File
import java.util.*

private data class StudioPdfExport(val mode:Int,val pages:List<PdfPageModel>,val source:Uri?,val starts:List<Int> = emptyList(),val masks:List<StudioRedaction> = emptyList())
@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun StudioPdfTools(projectId:String,pages:List<PdfPageModel>,source:Uri?,pageIndex:Int,request:Int,onConsumed:()->Unit,onPage:(Int)->Unit,onReplace:(List<PdfPageModel>)->Unit,onSource:(Uri,List<PdfPageModel>)->Unit){
 val context=LocalContext.current
val scope=rememberCoroutineScope()
var mode by remember{mutableIntStateOf(0)}
var busy by remember{mutableStateOf(false)}
var failure by remember{mutableStateOf("")}
var pending by remember{mutableStateOf<StudioPdfExport?>(null)}
var starts by remember{mutableStateOf("1")}
var masks by remember{mutableStateOf<List<StudioRedaction>>(emptyList())}
var left by remember{mutableStateOf("10")}
var top by remember{mutableStateOf("10")}
var right by remember{mutableStateOf("90")}
var bottom by remember{mutableStateOf("30")}
var rotate by remember{mutableStateOf(false)}
var fields by remember{mutableStateOf<List<StudioPdfField>>(emptyList())}
var values by remember{mutableStateOf<Map<String,String>>(emptyMap())}
 fun rect():RectF?{val a=left.toFloatOrNull()?:return null
val b=top.toFloatOrNull()?:return null
val c=right.toFloatOrNull()?:return null
val d=bottom.toFloatOrNull()?:return null
return RectF(a/100,b/100,c/100,d/100).takeIf{a>=0&&b>=0&&c<=100&&d<=100&&c-a>1&&d-b>1}}
 LaunchedEffect(request){if(request in 33..40&&request!=35){mode=request
onConsumed()}}
 LaunchedEffect(mode,source){failure=""
if(mode==34||mode==37){busy=true
try{if(mode==34)starts=StudioPdfEngine.chapters(context,source,pages).joinToString(", ")else{fields=StudioPdfEngine.fields(context,source)
values=fields.associate{it.name to it.value}}}catch(error:Exception){failure=error.message.orEmpty()}finally{busy=false}}}
 val exporter=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")){uri->val captured=pending
pending=null
if(uri!=null&&captured!=null)StudioJobs.start(StudioCatalog.all[captured.mode-1].label){job->when(captured.mode){34->StudioPdfEngine.split(context,uri,captured.pages,captured.source,captured.starts,job)
36->StudioPdfEngine.redact(context,uri,captured.pages,captured.source,captured.masks,job)
37->context.contentResolver.openInputStream(captured.source!!)?.use{input->context.contentResolver.openOutputStream(uri,"wt")?.use{input.copyTo(it)}?:error("Cannot write PDF")}?:error("Cannot read PDF")}}}
 val merger=rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()){uris->if(uris.isNotEmpty()){val captured=pages
val original=source
busy=true
scope.launch{val temp=File(context.cacheDir,"merge_current_${UUID.randomUUID()}.pdf")
try{val meaningful=original!=null||captured.size>1||captured.any{it.strokes.isNotEmpty()||it.texts.isNotEmpty()||it.images.isNotEmpty()}
val inputs=if(meaningful){PdfDocumentEngine.exportPdf(context,Uri.fromFile(temp),captured,original)
listOf(Uri.fromFile(temp))+uris}else uris
require(inputs.size >= 2) { "Choose at least two PDF files when the current project is empty." }
val merged=StudioPdfEngine.merge(context,inputs)
onSource(merged,PdfDocumentEngine.loadPdfLazy(context,merged))
mode=0}catch(error:Exception){failure=error.message.orEmpty()}finally{temp.delete()
busy=false}}}}
 // Una sola fila desplazable: la lista de utilidades no agranda el cajón de herramientas.
 // Only expose utility actions that have usable data in this PDF project.
 val hasOverlay = pages.any { it.strokes.isNotEmpty() || it.texts.isNotEmpty() || it.images.isNotEmpty() }
 val hasPageText = pages.any { page -> page.texts.any { it.text.isNotBlank() } }
 val hasImportedPdf = source != null
 val advancedTools = buildList {
     if (hasImportedPdf || hasPageText || pages.any { it.images.isNotEmpty() }) add(40) // Read PDF aloud (including OCR of inserted images)
     add(33)                                    // Merge PDFs
     if (pages.size > 1) add(34)                 // Split PDF chapters
     if (hasImportedPdf || hasOverlay) add(36)  // Permanent PDF redaction
     if (hasImportedPdf) add(37)                 // PDF forms
     if (hasOverlay) add(38)                     // Annotation browser
     if (hasImportedPdf || hasOverlay) add(39)  // Crop and rotate
 }
 Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement=Arrangement.spacedBy(6.dp)) {
     advancedTools.forEach { id ->
         AssistChip(
             onClick = { mode = id; UiSoundPlayer.playFallbackClick() },
             label = { Text(StudioCatalog.all[id - 1].label) },
             colors = AssistChipDefaults.assistChipColors(
                 containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                 labelColor = MaterialTheme.colorScheme.onSurface
             )
         )
     }
 }
 if(mode==40)StudioPdfReader(projectId,source,pages,pageIndex,onPage){mode=0}
 else if(mode>0)AppAlertDialog(onDismissRequest={if(!busy)mode=0},title={Text(StudioCatalog.all[mode-1].label)},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
  if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
if(failure.isNotBlank())Text(failure,color=MaterialTheme.colorScheme.error)
  when(mode){
   33->{Text("Choose PDFs in the order to append them. The current document is included when it contains content. For an empty project, choose at least two PDFs.")
Button(onClick={merger.launch(arrayOf("application/pdf")); UiSoundPlayer.playFallbackClick()},enabled=!busy){Text("Choose PDFs")}}
   34->{Text("Chapter starts are suggested from bookmarks. Export creates one PDF per chapter in a ZIP.")
OutlinedTextField(starts,{starts=it},label={Text("First pages · for example 1, 5, 12")})}
   36,39->{Text("Page ${pageIndex+1} · percentages from top-left")
Row{OutlinedTextField(left,{left=it},Modifier.weight(1f),label={Text("Left %")})
OutlinedTextField(top,{top=it},Modifier.weight(1f),label={Text("Top %")})}
Row{OutlinedTextField(right,{right=it},Modifier.weight(1f),label={Text("Right %")})
OutlinedTextField(bottom,{bottom=it},Modifier.weight(1f),label={Text("Bottom %")})}
StudioPdfPreview(pages[pageIndex],source,rect(),masks.filter{it.page==pageIndex},mode==36)
    if(mode==36){Text("Exported content is permanently removed. The new PDF contains page images only; your original project is retained.")
Button(onClick={val area=rect()
if(area==null)failure="Enter a valid rectangle"else{masks=masks+StudioRedaction(pageIndex,area.left,area.top,area.right,area.bottom)
failure=""}; UiSoundPlayer.playFallbackClick()}){Text("Add redaction · ${masks.size} areas")}
Row{TextButton(onClick={onPage((pageIndex-1).coerceAtLeast(0)); UiSoundPlayer.playFallbackClick()}){Text("‹")}
TextButton(onClick={onPage((pageIndex+1).coerceAtMost(pages.lastIndex)); UiSoundPlayer.playFallbackClick()}){Text("›")}
TextButton(onClick={masks=emptyList(); UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}){Text("Clear areas")}}}else{Row{Text("Rotate 90° clockwise",Modifier.weight(1f))
Switch(rotate,{rotate=it})}
Text("Cropping and rotation flatten this page into an image.")}}
   37->{if(fields.isEmpty()&&!busy)Text("No supported form fields found.")
LazyColumn(Modifier.heightIn(max=400.dp)){items(fields,key={it.name}){field->if(field.checkbox)Row{Text(field.name,Modifier.weight(1f))
Checkbox(values[field.name]!="Off",{check->values=values+(field.name to if(check)field.options.last()else"Off")},enabled=field.editable)}else OutlinedTextField(values[field.name].orEmpty(),{values=values+(field.name to it)},label={Text(field.name)},enabled=field.editable,supportingText={if(field.options.isNotEmpty())Text(field.options.joinToString(" · "))})}}}
   38->LazyColumn(Modifier.heightIn(max=420.dp)){pages.forEachIndexed{index,page->if(page.strokes.isNotEmpty()||page.texts.isNotEmpty()||page.images.isNotEmpty())item(key="page_$index"){TextButton(onClick={onPage(index)
mode=0; UiSoundPlayer.playFallbackClick()}){Text("Page ${index+1} · ${page.strokes.size} strokes · ${page.texts.size} texts · ${page.images.size} images")}}
items(page.texts,key={"${index}_${it.id}"}){text->TextButton(onClick={onPage(index)
mode=0; UiSoundPlayer.playFallbackClick()}){Text(text.text.take(160))}}}}
  }
 }},confirmButton={when(mode){
  34->TextButton(onClick={val parsed=starts.split(',').mapNotNull{it.trim().toIntOrNull()}.distinct().sorted()
if(parsed.firstOrNull()!=1||parsed.any{it !in 1..pages.size})failure="Include page 1 and valid chapter starts"else{pending=StudioPdfExport(34,pages,source,parsed)
exporter.launch("MyNotes_chapters.zip")}; UiSoundPlayer.playFallbackClick()},enabled=!busy){Text("Export ZIP")}
  36->TextButton(onClick={pending=StudioPdfExport(36,pages,source,masks=masks)
exporter.launch("MyNotes_redacted.pdf"); UiSoundPlayer.playFallbackClick()},enabled=masks.isNotEmpty()&&!busy){Text("Export redacted PDF")}
  37->TextButton(onClick={val original=source?:return@TextButton
val captured=pages
busy=true
scope.launch{try{val filled=StudioPdfEngine.fill(context,original,values)
onSource(filled,captured)
pending=StudioPdfExport(37,captured,filled)
exporter.launch("MyNotes_filled_form.pdf")}catch(error:Exception){failure=error.message.orEmpty()}finally{busy=false}}; UiSoundPlayer.playFallbackClick()},enabled=fields.isNotEmpty()&&!busy){Text("Save filled PDF")}
  39->TextButton(onClick={val area=rect()
if(area==null)failure="Enter a valid crop rectangle"else{val captured=pages
val index=pageIndex
busy=true
scope.launch{try{val transformed=StudioPdfEngine.transform(context,captured[index],source,area,rotate)
onReplace(captured.mapIndexed{i,p->if(i==index)transformed else p})
mode=0}catch(error:Exception){failure=error.message.orEmpty()}finally{busy=false}}}; UiSoundPlayer.playFallbackClick()},enabled=!busy){Text("Apply")}
  else->TextButton(onClick={mode=0; UiSoundPlayer.playFallbackClick()},enabled=!busy){Text("Done")}
 }},dismissButton={TextButton(onClick={mode=0; UiSoundPlayer.playFallbackClick()},enabled=!busy){Text("Close")}})
}
@Composable private fun StudioPdfPreview(page:PdfPageModel,source:Uri?,rect:RectF?,masks:List<StudioRedaction>,redaction:Boolean){val context=LocalContext.current
var bitmap by remember{mutableStateOf<android.graphics.Bitmap?>(null)}
LaunchedEffect(page,source){bitmap=runCatching{PdfDocumentEngine.renderFlatPage(context,page,source,640)}.getOrNull()}
bitmap?.let{image->Canvas(Modifier.fillMaxWidth().height(170.dp)){drawImage(image.asImageBitmap(),dstSize=androidx.compose.ui.unit.IntSize(size.width.toInt(),size.height.toInt()))
masks.forEach{r->drawRect(Color.Black,Offset(r.left*size.width,r.top*size.height),Size((r.right-r.left)*size.width,(r.bottom-r.top)*size.height))}
if(rect!=null)drawRect(if(redaction)Color.Black.copy(alpha=.6f)else Color(0xff3366cc).copy(alpha=.3f),Offset(rect.left*size.width,rect.top*size.height),Size(rect.width()*size.width,rect.height()*size.height))}}}
@Composable private fun StudioPdfReader(projectId:String,source:Uri?,pages:List<PdfPageModel>,index:Int,onPage:(Int)->Unit,onClose:()->Unit){
 val context=LocalContext.current
var text by remember{mutableStateOf("")}
var failure by remember{mutableStateOf("")}
var ready by remember{mutableStateOf(false)}
var playing by remember{mutableStateOf(false)}
var range by remember{mutableStateOf(0 to 0)}
var rate by remember{mutableFloatStateOf(1f)}
val defaultLanguage=androidx.compose.ui.platform.LocalConfiguration.current.locales[0].toLanguageTag()
var language by remember(defaultLanguage){mutableStateOf(defaultLanguage)}
var engine by remember{mutableStateOf<TextToSpeech?>(null)}
val main=remember{android.os.Handler(android.os.Looper.getMainLooper())}
val scroll=rememberLazyListState()
 LaunchedEffect(projectId,source,index,pages[index].texts){engine?.stop()
playing=false
range=0 to 0
try{
    val original = StudioPdfEngine.text(context,source,pages[index])
    val ocr = PlusPdfEngine.indexedText(context,projectId,listOf(index))[index].orEmpty()
    text=listOf(original,ocr).filter { it.isNotBlank() }.distinct().joinToString("\n\n")
    failure=if(text.isBlank())"No readable text on this page"else""
}catch(cancelled:CancellationException){throw cancelled}
 catch(error:Exception){failure=error.message.orEmpty()}}
 DisposableEffect(Unit){val tts=TextToSpeech(context){status->main.post{ready=status==TextToSpeech.SUCCESS
if(!ready)failure="Text to speech unavailable"}}
engine=tts
tts.setOnUtteranceProgressListener(object:UtteranceProgressListener(){override fun onStart(id:String?){main.post{playing=true
val offset=id?.toIntOrNull()?:0
range=offset to (offset+3500).coerceAtMost(text.length)}}
override fun onDone(id:String?){main.post{if((id?.toIntOrNull()?:0)+3500>=text.length)playing=false}}
override fun onError(id:String?){main.post{playing=false
failure="Speech unavailable"}}
override fun onRangeStart(id:String?,a:Int,b:Int,frame:Int){main.post{val offset=id?.toIntOrNull()?:0
range=(offset+a).coerceAtMost(text.length) to (offset+b).coerceAtMost(text.length)}}})
onDispose{tts.stop()
tts.shutdown()
main.removeCallbacksAndMessages(null)
engine=null}}
 val chunks=remember(text){text.chunked(3500)}
LaunchedEffect(range.first/3500,chunks){if(chunks.isNotEmpty())scroll.animateScrollToItem((range.first/3500).coerceIn(chunks.indices))}
val highlight=MaterialTheme.colorScheme.primaryContainer
 AppAlertDialog(onDismissRequest=onClose,title={Text("Read aloud · Page ${index+1}")},text={Column{if(failure.isNotBlank())Text(failure)
Row(Modifier.horizontalScroll(rememberScrollState())){listOf("en","es","fr","zh").forEach{tag->FilterChip(language.startsWith(tag),{language=tag},label={Text(tag.uppercase())})}}
StyledSettingsSlider(
    value = rate,
    onValueChange = {
        rate = it
        engine?.setSpeechRate(it)
    },
    valueRange = 0.5f..2f,
    valueLabel = String.format(Locale.getDefault(), "%.1fx", rate)
)
LazyColumn(Modifier.heightIn(max=350.dp),state=scroll){items(chunks.indices.toList()){i->val chunk=chunks[i]
Text(buildAnnotatedString{append(chunk)
val a=(range.first-i*3500).coerceIn(0,chunk.length)
val b=(range.second-i*3500).coerceIn(0,chunk.length)
if(b>a)addStyle(SpanStyle(background=highlight),a,b)})}}
Row{TextButton(onClick={onPage((index-1).coerceAtLeast(0)); UiSoundPlayer.playFallbackClick()}){Text("‹")}
TextButton(onClick={onPage((index+1).coerceAtMost(pages.lastIndex)); UiSoundPlayer.playFallbackClick()}){Text("›")}}}},confirmButton={TextButton(onClick={val tts=engine?:return@TextButton
if(playing){tts.stop()
playing=false}else{tts.language=Locale.forLanguageTag(language)
tts.voices?.firstOrNull{!it.isNetworkConnectionRequired&&it.locale.language==Locale.forLanguageTag(language).language}?.let{tts.voice=it}
tts.setSpeechRate(rate)
chunks.forEachIndexed{i,chunk->tts.speak(chunk,if(i==0)TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,android.os.Bundle(),(i*3500).toString())}}; UiSoundPlayer.playFallbackClick()},enabled=ready&&text.isNotBlank()){Text(if(playing)"Stop"else"Read")}},dismissButton={TextButton(onClick = { onClose(); UiSoundPlayer.playFallbackClick() }){Text("Close")}})
}
