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
}

data class Point(override val x: Double = 0.0, override val y: Double = 0.0) : GeoPoint {
    override fun toImmutable(): Point = this

    override fun toMutable() = MutPoint(x, y)
}

data class MutPoint(override var x: Double = 0.0, override var y: Double = 0.0) : GeoPoint {
    override fun toMutable(): MutPoint = this

    override fun toImmutable() = Point(x, y)

    fun updateFrom(point: GeoPoint) {
        x = point.x
        y = point.y
    }
}
