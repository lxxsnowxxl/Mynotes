package com.example.mynotes.studio

import kotlin.math.*
internal data class StudioPoint(val x:Float,val y:Float)
internal object StudioAlgorithms{
 fun path(id:String?,parents:Map<String,String?>):List<String>{val result=mutableListOf<String>()
val seen=mutableSetOf<String>()
var current=id
while(current!=null&&seen.add(current)){result+=current
current=parents[current]}
return result.reversed()}
 fun canParent(id:String,parent:String?,parents:Map<String,String?>)=id!=parent&&id !in path(parent,parents)
 fun words(text:String)=Regex("[\\p{L}\\p{N}]+(?:['’-][\\p{L}\\p{N}]+)*").findAll(text).count()
 fun headings(text:String)=Regex("(?m)^(#{1,6}) +(.+)$").findAll(text).map{Triple(it.groupValues[1].length,it.groupValues[2],it.range.first)}.toList()
 fun review(quality:Int,repetitions:Int,interval:Int,ease:Double):Triple<Int,Int,Double>{val score=quality.coerceIn(0,5)
val next=(ease+.1-(5-score)*(.08+(5-score)*.02)).coerceIn(1.3,3.0)
if(score<3)return Triple(0,1,next)
return Triple(repetitions+1,when(repetitions){0->1
1->6
else->(interval*next).roundToInt().coerceIn(1,3650)},next)}
 fun rotate(p:StudioPoint,degrees:Float,pivot:StudioPoint):StudioPoint{val angle=Math.toRadians(degrees.toDouble())
val x=p.x-pivot.x
val y=p.y-pivot.y
return StudioPoint((pivot.x+x*cos(angle)-y*sin(angle)).toFloat(),(pivot.y+x*sin(angle)+y*cos(angle)).toFloat())}
 fun inside(p:StudioPoint,polygon:List<StudioPoint>):Boolean{var result=false
var j=polygon.lastIndex
for(i in polygon.indices){val a=polygon[i]
val b=polygon[j]
if((a.y>p.y)!=(b.y>p.y)&&p.x<(b.x-a.x)*(p.y-a.y)/(b.y-a.y)+a.x)result=!result
j=i}
return result}
 fun xml(s:String)=s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;")
}
