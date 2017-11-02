package xx.projmap.geometry

fun GeoRect.toQuad(): Quad {
    return Quad(
            getX(0), getY(0),
            getX(1), getY(1),
            getX(2), getY(2),
            getX(3), getY(3)
    )
}

