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

    fun transformX(src: Double, dstRect: GeoRect) =
            proj2(x, x + w, dstRect.x, dstRect.x + dstRect.w, src)

    fun transformY(src: Double, dstRect: GeoRect) =
            proj2(y, y + h, dstRect.y, dstRect.y + dstRect.h, src)

    fun scaleW(src: Double, dstRect: GeoRect) =
            scale2(w, dstRect.w, src)

    fun scaleH(src: Double, dstRect: GeoRect) =
            scale2(h, dstRect.h, src)

    fun transformTo(dstRect: GeoRect, src: GeoPoint, dst: MutPoint = MutPoint()): MutPoint {
        dst.x = transformX(src.x, dstRect)
        dst.y = transformY(src.y, dstRect)
        return dst
    }

    fun containsFully(other: GeoRect): Boolean = other.x >= x && other.y >= y && other.w <= w && other.h <= h
}

data class MutRect(override var x: Double, override var y: Double, override var w: Double, override var h: Double) : GeoRect {
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
}

data class Rect(override val x: Double, override val y: Double, override val w: Double, override val h: Double) : GeoRect {
    override fun toImmutable(): Rect = this

    override fun toMutable(): MutRect = MutRect(x, y, w, h)
}

