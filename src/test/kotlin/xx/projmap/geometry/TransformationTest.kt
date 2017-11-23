package xx.projmap.geometry

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

internal class TransformationTest {

    @Test
    internal fun testZeroTransformation() {
        val quad = Quad(0.0, 0.0, 23.7, 0.0, 23.7, 9.0, 0.0, 9.0)

        val transformation = Transformation(quad, quad)

        val srcPoint = Point(1.0, 1.0)
        val mutPoint = MutPoint()

        transformation.srcToDst(srcPoint, mutPoint)

        assertEquals(1.0, mutPoint.x, 0.000001)
        assertEquals(1.0, mutPoint.y, 0.000001)
    }

    @Test
    internal fun benchConversion() {
        val quad = Quad(0.0, 0.0, 23.7, 0.0, 23.7, 9.0, 0.0, 9.0)

        val transformation = Transformation(quad, quad)

        val srcPoint = Point(1.0, 1.0)
        val mutPoint = MutPoint()

        val start = System.nanoTime()
        (1..1_000_000).forEach {
            transformation.srcToDst(srcPoint, mutPoint)
        }
        val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)

        println("Transformations per millisecond: ${1_000_000/durationMs}")
    }

    @Test
    internal fun testStretchTransformation() {
        val srcQuad = Quad(0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0)
        val dstQuad = Quad(0.0, 0.0, 2.0, 0.0, 2.0, 2.0, 0.0, 2.0)

        val transformation = Transformation(srcQuad, dstQuad)

        val transformQuad = transformation.transformQuad(srcQuad)
        assertEquals(dstQuad, transformQuad)

        val transformQuad1 = transformation.transformQuad(dstQuad, TransformationDirection.DST_TO_SRC)
        assertEquals(srcQuad, transformQuad1)
    }
}

