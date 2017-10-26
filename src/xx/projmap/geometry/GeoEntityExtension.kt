package xx.projmap.geometry

fun GeoEntity<*, *>.toPointArray(): Array<Point> = Array(size, { Point(getX(it), getY(it)) })

fun GeoEntity<*, *>.toQuad(): Quad {
    if (size != 4) {
        throw IllegalArgumentException("expected 4 got $size")
    }

    return Quad(
            getX(0), getY(0),
            getX(1), getY(1),
            getX(2), getY(2),
            getX(3), getY(3)
    )
}
