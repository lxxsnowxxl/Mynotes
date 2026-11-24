package com.example.mynotes.studio

import com.example.mynotes.data.workspace.*
import org.junit.Assert.*
import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream

class StudioDrawingGeometryTest {
    private fun point(x:Float,y:Float)=DrawingPoint(x,y)
    @Test fun infiniteCanvasExportIncludesNegativeCoordinatesAndStrokeWidth() {
        val stroke=DrawingStroke(points=listOf(point(-300f,-200f),point(900f,500f)),colorArgb=-1,width=20f)
        val result=StudioDrawingGeometry.export(DrawingDocument(strokes=listOf(stroke),infinite=true))
        assertFalse(result.infinite);assertEquals(1272,result.width);assertEquals(772,result.height)
        assertEquals(point(36f,36f),result.strokes.single().points.first())
    }
    @Test fun svgIsValidXmlAndPreservesEscapedTitleAlphaPressureAndFill() {
        val stroke=DrawingStroke(points=listOf(DrawingPoint(0f,0f,.5f),DrawingPoint(50f,50f,1.5f)),colorArgb=0x80332211.toInt(),width=4f,fillArgb=0x40778899)
        val svg=StudioDrawingGeometry.svg(DrawingDocument(name="A < B & C",width=100,height=100,pattern="grid",strokes=listOf(stroke)))
        val parsed=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ByteArrayInputStream(svg.toByteArray()))
        assertEquals("A < B & C",parsed.getElementsByTagName("title").item(0).textContent)
        assertTrue(svg.contains("stroke-opacity=\"0.502\""));assertTrue(svg.contains("fill-opacity=\"0.251\""))
        assertTrue(svg.contains("stroke-width=\"4.000\""));assertTrue(svg.contains("url(#grid)"))
    }
    @Test fun editableDrawingRoundTripIncludesNewFields() {
        val doc=DrawingDocument(infinite=true,rotation=37f,strokes=listOf(DrawingStroke(points=listOf(point(-3f,7f)),colorArgb=-1,width=2f,fillArgb=0x12345678)))
        assertEquals(doc,DrawingProjects.decode(DrawingProjects.encode(doc)))
    }
}
