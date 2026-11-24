package com.example.mynotes.studio

import com.example.mynotes.data.workspace.*
import kotlin.math.*

internal object StudioDrawingGeometry{
 fun closed(points:List<DrawingPoint>,tolerance:Float=18f)=points.size>=4&&hypot(points.first().x-points.last().x,points.first().y-points.last().y)<=tolerance
 fun export(source:DrawingDocument):DrawingDocument{val document=PlusDrawingGeometry.visible(source)
if(!document.infinite||document.strokes.isEmpty())return document
val points=document.strokes.flatMap{it.points}
val padding=document.strokes.maxOf{it.width}+16
val x=points.minOf{it.x}-padding
val y=points.minOf{it.y}-padding
return document.copy(width=ceil(points.maxOf{it.x}-x+padding).toInt().coerceAtLeast(1),height=ceil(points.maxOf{it.y}-y+padding).toInt().coerceAtLeast(1),strokes=document.strokes.map{DrawingGeometry.moved(it,-x,-y)},infinite=false)}
 fun stamp(kind:String,x:Float,y:Float,color:Int,width:Float)=DrawingStroke(points=when(kind){"arrow"->listOf(DrawingPoint(x-45,y),DrawingPoint(x+45,y),DrawingPoint(x+20,y-25),DrawingPoint(x+45,y),DrawingPoint(x+20,y+25))
"check"->listOf(DrawingPoint(x-30,y),DrawingPoint(x-5,y+25),DrawingPoint(x+40,y-30))
else->(0..10).map{i->val angle=-PI/2+i*PI/5
val radius=if(i%2==0)45 else 20
DrawingPoint(x+cos(angle).toFloat()*radius,y+sin(angle).toFloat()*radius)}},colorArgb=color,width=width)
 fun svg(document:DrawingDocument):String{val doc=export(PlusDrawingGeometry.visible(document))
fun n(value:Float)=String.format(java.util.Locale.US,"%.3f",value)
fun color(argb:Int)="#%06x".format(argb and 0xffffff)
fun alpha(argb:Int)=n((argb ushr 24)/255f)
  return buildString{append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 ${doc.width} ${doc.height}\"><title>${StudioAlgorithms.xml(doc.name)}</title><rect width=\"100%\" height=\"100%\" fill=\"${color(doc.paperArgb)}\"/>")
   if(doc.pattern!="plain")append("<defs><pattern id=\"grid\" width=\"28\" height=\"28\" patternUnits=\"userSpaceOnUse\"><path d=\"M0 28 H28 ${if(doc.pattern=="grid")"M28 0 V28"else""}\" fill=\"none\" stroke=\"#888888\" stroke-opacity=\"0.2\" stroke-width=\"1\"/></pattern></defs><rect width=\"100%\" height=\"100%\" fill=\"url(#grid)\"/>")
   doc.strokes.forEach{stroke->if(stroke.points.isEmpty())return@forEach
val path=stroke.points.mapIndexed{i,p->(if(i==0)"M"else"L")+n(p.x)+" "+n(p.y)}.joinToString(" ")
stroke.fillArgb?.let{fill->append("<path d=\"$path Z\" fill=\"${color(fill)}\" fill-opacity=\"${alpha(fill)}\"/>")}
    if(stroke.points.size==1){val p=stroke.points.first()
append("<circle cx=\"${n(p.x)}\" cy=\"${n(p.y)}\" r=\"${n(stroke.width*p.pressure/2)}\" fill=\"${color(stroke.colorArgb)}\" fill-opacity=\"${alpha(stroke.colorArgb)}\"/>")}
    else if(stroke.points.all{it.pressure==1f})append("<path d=\"$path\" fill=\"none\" stroke=\"${color(stroke.colorArgb)}\" stroke-opacity=\"${alpha(stroke.colorArgb)}\" stroke-width=\"${n(stroke.width)}\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/>")
    else stroke.points.zipWithNext().forEach{(a,b)->append("<path d=\"M${n(a.x)} ${n(a.y)} L${n(b.x)} ${n(b.y)}\" fill=\"none\" stroke=\"${color(stroke.colorArgb)}\" stroke-opacity=\"${alpha(stroke.colorArgb)}\" stroke-width=\"${n(stroke.width*(a.pressure+b.pressure)/2)}\" stroke-linecap=\"round\"/>")}
   }
append("</svg>")}
 }
}
