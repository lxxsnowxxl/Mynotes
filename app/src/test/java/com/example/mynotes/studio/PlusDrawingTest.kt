package com.example.mynotes.studio

import com.example.mynotes.data.workspace.*
import org.junit.Assert.*
import org.junit.Test

class PlusDrawingTest {
    private fun stroke(id: String, layer: String = "base", from: DrawingPoint = DrawingPoint(10f, 10f), to: DrawingPoint = DrawingPoint(20f, 20f)) = DrawingStroke(id, listOf(from, to), 0xff123456.toInt(), 3f, layer = layer)
    @Test fun renderingHonorsLayerOrderVisibilityAndOpacity() {
        val doc = DrawingDocument(strokes = listOf(stroke("upper", "upper"), stroke("lower"), stroke("hidden", "hidden")), layers = listOf(DrawingLayer(opacity = .5f), DrawingLayer("upper", "Upper"), DrawingLayer("hidden", "Hidden", visible = false)))
        val flattened = PlusDrawingGeometry.visible(doc)
        assertEquals(listOf("lower", "upper"), flattened.strokes.map { it.id }); assertEquals(128, flattened.strokes.first().colorArgb ushr 24)
        assertEquals(listOf(DrawingLayer()), flattened.layers); assertTrue(flattened.strokes.all { it.layer == "base" })
        assertEquals(flattened, PlusDrawingGeometry.visible(flattened))
    }
    @Test fun lassoFindsSegmentsCrossingThePolygonWithoutASampledPointInside() {
        val shape = listOf(DrawingPoint(0f, 0f), DrawingPoint(10f, 0f), DrawingPoint(10f, 10f), DrawingPoint(0f, 10f))
        assertTrue(PlusDrawingGeometry.inLasso(stroke("cross", from = DrawingPoint(-5f, 5f), to = DrawingPoint(15f, 5f)), shape))
        assertFalse(PlusDrawingGeometry.inLasso(stroke("away", from = DrawingPoint(-5f, 20f), to = DrawingPoint(15f, 20f)), shape))
    }
    @Test fun movingAGroupClampsTheGroupWithoutDistortingRelativePositions() {
        val a = stroke("a"); val b = stroke("b", from = DrawingPoint(70f, 70f), to = DrawingPoint(90f, 90f))
        val result = PlusDrawingGeometry.moved(listOf(a, b), setOf("a", "b"), 100f, -100f, 100f, 100f, false)
        assertEquals(100f, result[1].points.last().x, .001f); assertEquals(0f, result[0].points.first().y, .001f)
        assertEquals(60f, result[1].points.first().x - result[0].points.first().x, .001f)
    }
    @Test fun hiddenAndLockedLayersCannotBeSelectedForEditing() {
        assertFalse(PlusDrawingGeometry.editable(stroke("x"), listOf(DrawingLayer(locked = true))))
        assertFalse(PlusDrawingGeometry.editable(stroke("x"), listOf(DrawingLayer(visible = false))))
    }
    @Test fun editableLayerMetadataSurvivesTheProjectCodec() {
        val doc = DrawingDocument(id = "project", name = "Layers", width = 100, height = 100, strokes = listOf(stroke("top", "top")), layers = listOf(DrawingLayer(), DrawingLayer("top", "Ink", locked = true, opacity = .3f)), activeLayer = "top")
        assertEquals(doc, DrawingProjects.decode(DrawingProjects.encode(doc)))
        val legacy = DrawingProjects.encode(doc); legacy.remove("layers"); legacy.remove("activeLayer")
        val restored = DrawingProjects.decode(legacy)
        assertEquals(listOf(DrawingLayer()), restored.layers); assertEquals("base", restored.activeLayer)
    }
    @Test fun threePointAngleAndGridSnapHandleDegenerateInput() {
        assertEquals(90.0, PlusAlgorithms.angle(StudioPoint(1f, 0f), StudioPoint(0f, 0f), StudioPoint(0f, 1f)), .0001)
        assertEquals(0.0, PlusAlgorithms.angle(StudioPoint(0f, 0f), StudioPoint(0f, 0f), StudioPoint(1f, 0f)), .0001)
        assertEquals(48f, PlusAlgorithms.snap(50f, 24f), 0f)
    }
}
