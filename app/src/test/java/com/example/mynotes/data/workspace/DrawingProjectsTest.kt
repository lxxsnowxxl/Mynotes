package com.example.mynotes.data.workspace
import org.junit.Assert.*
import org.junit.Test
class DrawingProjectsTest {
    private val stroke = DrawingStroke("stroke", listOf(DrawingPoint(0f, 0f, .3f), DrawingPoint(10f, 10f, 1.4f)), -16777216, 4f)
    @Test fun editableProjectRoundTripKeepsPressurePaperAndIdentity() {
        val doc = DrawingDocument("project-1", "Sketch", 1000, 800, -1, "grid", listOf(stroke), 99)
        assertEquals(doc, DrawingProjects.decode(DrawingProjects.encode(doc)))
    }
    @Test fun lineHitTestsSegmentsRatherThanOnlySamplePoints() { assertTrue(DrawingGeometry.hit(stroke, 5f, 5f, 1f)); assertFalse(DrawingGeometry.hit(stroke, 50f, 50f, 1f)) }
    @Test fun movingStrokeRetainsPressureWidthAndStableId() { val moved = DrawingGeometry.moved(stroke, 20f, -5f); assertEquals(stroke.id, moved.id); assertEquals(1.4f, moved.points.last().pressure, 0f); assertEquals(30f, moved.points.last().x, 0f) }
    @Test fun selectionCannotDisappearBeyondPaperWhenDraggedTooFar() {
        val moved = DrawingGeometry.movedWithin(stroke, 1000f, -1000f, 100f, 100f)
        assertEquals(100f, moved.points.last().x, 0f); assertEquals(0f, moved.points.first().y, 0f)
        assertEquals(stroke.points.map { it.pressure }, moved.points.map { it.pressure })
    }
    @Test fun rectangleAndEllipseCloseExactlyAndUseBothDragDirections() {
        val a = DrawingPoint(50f, 40f); val b = DrawingPoint(10f, 20f)
        val rectangle = DrawingGeometry.shape("rectangle", a, b); assertEquals(5, rectangle.size); assertEquals(rectangle.first(), rectangle.last())
        val ellipse = DrawingGeometry.shape("ellipse", a, b); assertEquals(65, ellipse.size); assertEquals(ellipse.first().x, ellipse.last().x, .0001f); assertEquals(ellipse.first().y, ellipse.last().y, .0001f)
    }
}
