package xx.projmap.geometry

interface GeoRect : GeoEntity<Rect, MutRect> {

    val x: Double
    val y: Double
    val w: Double
    val h: Double

    override val size: Int
        get() = 4

    override fun getX(n: Int): Double = when (n) {
        0, 3 -> x
        1, 2 -> x + w
        else -> throw IllegalArgumentException("$n")
    }

    override fun getY(n: Int): Double = when (n) {
        0, 1 -> y
        2, 3 -> y + h
        else -> throw IllegalArgumentException("$n")
    }

}

data class MutRect(override var x: Double, override var y: Double, override var w: Double, override var h: Double) : GeoRect {
    override fun toImmutable(): Rect = Rect(x, y, w, h)

    override fun toMutable(): MutRect = this
}

data class Rect(override val x: Double, override val y: Double, override val w: Double, override val h: Double) : GeoRect {
    override fun toImmutable(): Rect = this

    override fun toMutable(): MutRect = MutRect(x, y, w, h)
}

