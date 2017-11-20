package xx.projmap.geometry

fun SimpleGeoEntity.toPointArray(): Array<Point> = Array(size, { Point(getX(it), getY(it)) })

fun SimpleGeoEntity.toNestedArrays(): Array<DoubleArray> = Array(size, { doubleArrayOf(getX(it), getY(it)) })

val Array<out GeoPoint>.boundingBox: GeoRect
    get() {
        if (size == 0) {
            return Rect(0.0, 0.0, 0.0, 0.0)
        }
        val minX = minBy { it.x }?.x!!
        val maxX = maxBy { it.x }?.x!!
        val minY = minBy { it.y }?.y!!
        val maxY = maxBy { it.y }?.y!!

        return Rect(minX, minY, maxX - minX, maxY - minY)
    }
