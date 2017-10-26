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

    fun transformTo(src: GeoPoint, dst: MutPoint, dstRect: GeoRect) {
        transformTo(src, dstRect, { updatedX, updatedY ->
            dst.x = updatedX
            dst.y = updatedY
        })
    }

    fun transformTo(src: GeoPoint, dstRect: GeoRect, pointConsumer: (x: Double, y: Double) -> Unit) {
        val xProj = transformX(src.x, dstRect)
        val yProj = transformY(src.y, dstRect)
        pointConsumer(xProj, yProj)
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

fun mutRectFrom(point: GeoPoint, w: Double, h: Double) = MutRect(point.x, point.y, w, h)
fun mutRectFrom(geoRect: GeoRect) = MutRect(geoRect.x, geoRect.y, geoRect.w, geoRect.h)
