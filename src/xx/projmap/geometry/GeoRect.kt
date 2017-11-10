package xx.projmap.geometry

const val NORMALIZATION_VALUE = 1000.0

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

    override fun boundingBox(): GeoRect = Rect(x, y, w, h)

    fun transformX(src: Double, dstRect: GeoRect) =
            proj2(x, x + w, dstRect.x, dstRect.x + dstRect.w, src)

    fun transformY(src: Double, dstRect: GeoRect) =
            proj2(y, y + h, dstRect.y, dstRect.y + dstRect.h, src)

    fun transformTo(dstRect: GeoRect, src: GeoPoint, dst: MutPoint = MutPoint()): MutPoint {
        dst.x = transformX(src.x, dstRect)
        dst.y = transformY(src.y, dstRect)
        return dst
    }

    fun toNormalized(normalizationValue: Double = NORMALIZATION_VALUE): GeoRect = Rect(0.0, 0.0, normalizationValue, normalizationValue * (h / w))

    operator fun contains(other: GeoRect): Boolean = other.x >= x && other.y >= y && other.w <= w && other.h <= h

    operator fun contains(point: GeoPoint): Boolean = point.x >= x && point.y >= y && point.x <= x + w && point.y <= y + h

    override fun translated(point: GeoPoint): GeoRect

    operator fun plus(point: GeoPoint): GeoRect = translated(point)
}

data class MutRect(override var x: Double, override var y: Double, override var w: Double, override var h: Double) : GeoRect {
    override fun translated(point: GeoPoint): MutRect = MutRect(x + point.x, y + point.y, w, h)

    override fun toImmutable(): Rect = Rect(x, y, w, h)

    override fun toMutable(): MutRect = this

    fun scale(factor: Double) {
        w *= factor
        h *= factor
    }

    fun move(dx: Double = 0.0, dy: Double = 0.0) {
        x += dx
        y += dy
    }

    fun resize(dw: Double = 0.0, dh: Double = 0.0) {
        w += dw
        h += dh
    }

    fun updateFrom(other: GeoRect) {
        x = other.x
        y = other.y
        w = other.w
        h = other.h
    }
}

data class Rect(override val x: Double, override val y: Double, override val w: Double, override val h: Double) : GeoRect {
    override fun translated(point: GeoPoint): Rect = Rect(x + point.x, y + point.y, w, h)

    override fun toImmutable(): Rect = this

    override fun toMutable(): MutRect = MutRect(x, y, w, h)
}


fun <T> ((Double, Double, Double, Double) -> T).callWithRect(rect: GeoRect): T {
    return this(rect.x, rect.y, rect.w, rect.h)
}

