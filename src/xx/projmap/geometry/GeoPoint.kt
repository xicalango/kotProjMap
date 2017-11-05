package xx.projmap.geometry

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

    override fun translated(point: GeoPoint): GeoPoint
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

    fun updateFrom(point: GeoPoint): MutPoint {
        x = point.x
        y = point.y
        return this
    }

    fun move(dx: Double, dy: Double) {
        x += dx
        y += dy
    }
}

