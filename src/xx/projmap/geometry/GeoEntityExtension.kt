package xx.projmap.geometry

fun SimpleGeoEntity.toPointArray(): Array<Point> = Array(size, { Point(getX(it), getY(it)) })
