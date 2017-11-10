package xx.projmap.geometry

import java.util.*

interface GeoPoint : GeoEntity<Point, MutPoint> {

    val x: Double
    val y: Double

    override val size: Int
        get() = 1

    override fun getX(n: Int): Double = when (n) {
        0 -> x
        else -> throw IllegalArgumentException("$n")
    }

    override fun getY(n: Int): Double = when (n) {
        0 -> y
        else -> throw IllegalArgumentException("$n")
    }

    override fun boundingBox(): GeoRect = Rect(x, y, 0.0, 0.0)

    override fun translated(point: GeoPoint): GeoPoint

    operator fun plus(point: GeoPoint) = translated(point)
}

data class Point(override val x: Double = 0.0, override val y: Double = 0.0) : GeoPoint {
    override fun translated(point: GeoPoint): Point = Point(x + point.x, y + point.y)

    override fun toImmutable(): Point = this

    override fun toMutable() = MutPoint(x, y)
}

data class MutPoint(override var x: Double = 0.0, override var y: Double = 0.0) : GeoPoint {
    override fun translated(point: GeoPoint): MutPoint = MutPoint(x + point.x, y + point.y)

    override fun toMutable(): MutPoint = this

    override fun toImmutable() = Point(x, y)

    fun set(point: GeoPoint): MutPoint {
        x = point.x
        y = point.y
        return this
    }

    fun move(dx: Double = 0.0, dy: Double = 0.0) {
        x += dx
        y += dy
    }
}

fun Random.randomPoint(maxX: Double, maxY: Double): GeoPoint = Point(nextDouble() * maxX, nextDouble() * maxY)
fun Random.randomPointIn(rect: GeoRect): GeoPoint {
    val x = (nextDouble() * rect.w) + rect.x
    val y = (nextDouble() * rect.h) + rect.y
    return Point(x, y)
}

