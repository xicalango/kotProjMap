package xx.projmap.geometry

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

internal class QuadTest {

    @Test
    internal fun testCreation() {
        val quad = Quad(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0)
        println("$quad")

        val quad2 = createQuadFromArrays(arrayOf(
                doubleArrayOf(0.0, 1.0),
                doubleArrayOf(2.0, 3.0),
                doubleArrayOf(4.0, 5.0),
                doubleArrayOf(6.0, 7.0)
        ))
        println("$quad2")

        assertEquals(quad, quad2)
    }

    @Test
    internal fun testToArray() {
        val points = arrayOf(
                doubleArrayOf(0.0, 1.0),
                doubleArrayOf(2.0, 3.0),
                doubleArrayOf(4.0, 5.0),
                doubleArrayOf(6.0, 7.0)
        )
        val quad = createQuadFromArrays(points)
        assertArrayEquals(points, quad.toNestedArrays())
    }

    @Test
    internal fun testGetXY() {
        val quad = Quad(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0)

        assertEquals(0.0, quad.getX(0), 0.000001)
        assertEquals(1.0, quad.getY(0), 0.000001)
        assertEquals(2.0, quad.getX(1), 0.000001)
        assertEquals(3.0, quad.getY(1), 0.000001)
        assertEquals(4.0, quad.getX(2), 0.000001)
        assertEquals(5.0, quad.getY(2), 0.000001)
        assertEquals(6.0, quad.getX(3), 0.000001)
        assertEquals(7.0, quad.getY(3), 0.000001)
    }

    @Test
    internal fun testProjMatrix() {
        val quad = Quad(0.0, 0.0, 23.7, 0.0, 23.7, 9.0, 0.0, 9.0)

        val projectionMatrix = quad.toProjectionMatrix()

        println("$projectionMatrix")
    }
}