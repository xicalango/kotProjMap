package xx.projmap.geometry

fun createQuadFromArrays(points: Array<DoubleArray>) = Quad(
        points[0][0], points[0][1],
        points[1][0], points[1][1],
        points[2][0], points[2][1],
        points[3][0], points[3][1]
)

fun createQuadFromPoints(points: Array<out GeoPoint>) = Quad(
        points[0].x, points[0].y,
        points[1].x, points[1].y,
        points[2].x, points[2].y,
        points[3].x, points[3].y
)

interface GeoQuad : GeoEntity<Quad, MutQuad> {

    val x1: Double
    val y1: Double
    val x2: Double
    val y2: Double
    val x3: Double
    val y3: Double
    val x4: Double
    val y4: Double

    override val size: Int
        get() = 4

    override fun getX(n: Int) = when (n) {
        0 -> x1
        1 -> x2
        2 -> x3
        3 -> x4
        else -> throw IllegalArgumentException("$n")
    }

    override fun getY(n: Int) = when (n) {
        0 -> y1
        1 -> y2
        2 -> y3
        3 -> y4
        else -> throw IllegalArgumentException("$n")
    }

    override fun translated(point: GeoPoint): GeoQuad
}

data class Quad(override val x1: Double, override val y1: Double, override val x2: Double, override val y2: Double, override val x3: Double, override val y3: Double, override val x4: Double, override val y4: Double) : GeoQuad {
    override fun translated(point: GeoPoint): Quad = Quad(
            x1 + point.x,
            y1 + point.y,
            x2 + point.x,
            y2 + point.y,
            x3 + point.x,
            y3 + point.y,
            x4 + point.x,
            y4 + point.y
    )

    override fun toImmutable(): Quad = this

    override fun toMutable(): MutQuad = MutQuad(x1, y1, x2, y2, x3, y3, x4, y4)
}

data class MutQuad(override var x1: Double, override var y1: Double, override var x2: Double, override var y2: Double, override var x3: Double, override var y3: Double, override var x4: Double, override var y4: Double) : GeoQuad {
    override fun translated(point: GeoPoint): MutQuad = MutQuad(
            x1 + point.x,
            y1 + point.y,
            x2 + point.x,
            y2 + point.y,
            x3 + point.x,
            y3 + point.y,
            x4 + point.x,
            y4 + point.y
    )

    override fun toImmutable(): Quad = Quad(x1, y1, x2, y2, x3, y3, x4, y4)

    override fun toMutable(): MutQuad = this
}


