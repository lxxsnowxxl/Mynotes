package com.example.mynotes.studio

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.example.mynotes.ui.pdf.*
import com.example.mynotes.util.openUriStream
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.*
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.*
import java.io.File
import java.util.UUID
import java.util.zip.*
import kotlin.math.roundToInt

internal data class StudioPdfField(val name:String,val value:String,val options:List<String>,val checkbox:Boolean,val editable:Boolean)
internal data class StudioRedaction(val page:Int,val left:Float,val top:Float,val right:Float,val bottom:Float)
internal object StudioPdfEngine{
 private fun memory(context:Context)=MemoryUsageSetting.setupTempFileOnly().setTempDir(context.cacheDir)
 private fun output(context:Context)=File(context.filesDir,"studio_pdf/${UUID.randomUUID()}.pdf").apply{parentFile!!.mkdirs()}
 suspend fun merge(context:Context,uris:List<Uri>):Uri=withContext(Dispatchers.IO){com.example.mynotes.studio.StudioFiles.write {require(uris.size in 2..100)
PDFBoxResourceLoader.init(context)
val target=output(context)
val files=mutableListOf<File>()
try{val merger=PDFMergerUtility()
merger.destinationFileName=target.path
var total=0L
  uris.forEach{uri->currentCoroutineContext().ensureActive()
val file=File(context.cacheDir,"merge_${UUID.randomUUID()}.pdf")
files+=file
context.openUriStream(uri)?.use{input->file.outputStream().use{out->val b=ByteArray(65536)
while(true){currentCoroutineContext().ensureActive()
val n=input.read(b)
if(n<0)break
total+=n
require(total<=512L*1024*1024){"PDF inputs exceed 512 MB"}
out.write(b,0,n)}}}?:error("Cannot read PDF")
merger.addSource(file)}
merger.mergeDocuments(memory(context))
Uri.fromFile(target)
 }catch(error:Throwable){target.delete()
throw error}finally{files.forEach{it.delete()}}}}
 suspend fun chapters(context:Context,source:Uri?,pages:List<PdfPageModel>):List<Int> =withContext(Dispatchers.IO){val starts=mutableSetOf(1)
pages.forEachIndexed{i,p->if(p.bookmarked)starts+=i+1}
if(source!=null){PDFBoxResourceLoader.init(context)
context.openUriStream(source)?.use{input->PDDocument.load(input,memory(context)).use{doc->var item=doc.documentCatalog.documentOutline?.firstChild
var count=0
while(item!=null&&count++<10000){val current=item
val destination=runCatching{current.findDestinationPage(doc)}.getOrNull()
if(destination!=null){val original=doc.pages.indexOf(destination)
val index=pages.indexOfFirst{it.sourcePdfPageIndex==original}
if(index>=0)starts+=index+1}
item=current.nextSibling}}}}
starts.sorted()}
 suspend fun split(context:Context,destination:Uri,pages:List<PdfPageModel>,source:Uri?,starts:List<Int>,job:String)=withContext(Dispatchers.IO){require(starts.isNotEmpty()&&starts.first()==1&&starts==starts.distinct().sorted()&&starts.all{it in 1..pages.size})
context.contentResolver.openOutputStream(destination,"wt")?.use{raw->ZipOutputStream(raw.buffered()).use{zip->starts.forEachIndexed{i,start->currentCoroutineContext().ensureActive()
val end=starts.getOrNull(i+1)?.minus(1)?:pages.size
val temp=File(context.cacheDir,"split_${UUID.randomUUID()}.pdf")
try{PdfDocumentEngine.exportPdf(context,Uri.fromFile(temp),pages.subList(start-1,end),source)
zip.putNextEntry(ZipEntry("Chapter_${i+1}_pages_${start}_$end.pdf"))
temp.inputStream().use{it.copyTo(zip)}
zip.closeEntry()}finally{temp.delete()}
StudioJobs.progress(job,(i+1f)/starts.size)}}}?:error("Cannot write ZIP")}
 suspend fun fields(context:Context,source:Uri?):List<StudioPdfField> =withContext(Dispatchers.IO){require(source!=null){"Open a PDF with form fields first"}
PDFBoxResourceLoader.init(context)
context.openUriStream(source)?.use{input->PDDocument.load(input,memory(context)).use{doc->doc.documentCatalog.acroForm?.fieldTree?.map{field->StudioPdfField(field.fullyQualifiedName,field.valueAsString,when(field){is PDCheckBox->listOf("Off",field.onValue)
is PDChoice->field.options
else->emptyList()},field is PDCheckBox,!field.isReadOnly&&field !is PDSignatureField)}?.take(500).orEmpty()}}?:error("Cannot read form")}
 suspend fun fill(context:Context,source:Uri,values:Map<String,String>):Uri=withContext(Dispatchers.IO){com.example.mynotes.studio.StudioFiles.write {PDFBoxResourceLoader.init(context)
val target=output(context)
try{context.openUriStream(source)?.use{input->PDDocument.load(input,memory(context)).use{doc->val form=doc.documentCatalog.acroForm?:error("No form fields")
values.forEach{(name,value)->val field=form.getField(name)?:return@forEach
if(!field.isReadOnly&&field !is PDSignatureField)field.setValue(value)}
form.needAppearances=false
form.refreshAppearances()
doc.save(target)}}?:error("Cannot read form")
Uri.fromFile(target)}catch(error:Throwable){target.delete()
throw error}}}
 suspend fun text(context:Context,source:Uri?,page:PdfPageModel):String=withContext(Dispatchers.IO){val extra=page.texts.joinToString("\n"){it.text}
val index=page.sourcePdfPageIndex
if(source==null||index==null)return@withContext extra
PDFBoxResourceLoader.init(context)
context.openUriStream(source)?.use{input->PDDocument.load(input,memory(context)).use{doc->val stripper=PDFTextStripper()
stripper.startPage=index+1
stripper.endPage=index+1
(stripper.getText(doc)+"\n"+extra).take(1000000)}}?:extra}
 suspend fun transform(context:Context,page:PdfPageModel,source:Uri?,crop:RectF,rotate:Boolean):PdfPageModel=withContext(Dispatchers.IO){require(crop.left>=0&&crop.top>=0&&crop.right<=1&&crop.bottom<=1&&crop.width()>.01f&&crop.height()>.01f)
val full=PdfDocumentEngine.renderFlatPage(context,page,source)
var cropped:Bitmap?=null
  try{val left=(crop.left*full.width).roundToInt().coerceIn(0,full.width-1)
val top=(crop.top*full.height).roundToInt().coerceIn(0,full.height-1)
val width=(crop.width()*full.width).roundToInt().coerceIn(1,full.width-left)
val height=(crop.height()*full.height).roundToInt().coerceIn(1,full.height-top)
cropped=Bitmap.createBitmap(full,left,top,width,height)
val result=if(rotate)Bitmap.createBitmap(cropped,0,0,cropped.width,cropped.height,Matrix().apply{postRotate(90f)},true)else cropped
if(result!==cropped&&cropped!==full)cropped.recycle()
val w=if(rotate)page.heightPt*crop.height()else page.widthPt*crop.width()
val h=if(rotate)page.widthPt*crop.width()else page.heightPt*crop.height()
if(result!==full)full.recycle()
page.copy(widthPt=w.roundToInt().coerceAtLeast(1),heightPt=h.roundToInt().coerceAtLeast(1),background=result,sourcePdfPageIndex=null,strokes=emptyList(),texts=emptyList(),images=emptyList())}catch(error:Throwable){cropped?.takeIf{it!==full}?.recycle()
full.recycle()
throw error}}
 /** The output contains only newly rendered images; source text, forms, links and annotations are absent. */
 suspend fun redact(context:Context,destination:Uri,pages:List<PdfPageModel>,source:Uri?,masks:List<StudioRedaction>,job:String)=withContext(Dispatchers.IO){require(masks.isNotEmpty())
val doc=android.graphics.pdf.PdfDocument()
try{pages.forEachIndexed{i,page->currentCoroutineContext().ensureActive()
val bitmap=PdfDocumentEngine.renderFlatPage(context,page,source)
try{val paint=Paint().apply{color=Color.BLACK
style=Paint.Style.FILL}
val canvas=Canvas(bitmap)
masks.filter{it.page==i}.forEach{r->canvas.drawRect(r.left*bitmap.width,r.top*bitmap.height,r.right*bitmap.width,r.bottom*bitmap.height,paint)}
val output=doc.startPage(android.graphics.pdf.PdfDocument.PageInfo.Builder(page.widthPt,page.heightPt,i+1).create())
output.canvas.drawBitmap(bitmap,null,Rect(0,0,page.widthPt,page.heightPt),null)
doc.finishPage(output)}finally{bitmap.recycle()}
StudioJobs.progress(job,(i+1f)/pages.size)}
currentCoroutineContext().ensureActive()
context.contentResolver.openOutputStream(destination,"wt")?.use{doc.writeTo(it)}?:error("Cannot write PDF")}finally{doc.close()}}
}
