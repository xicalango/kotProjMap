package xx.projmap.geometry

interface SimpleGeoEntity {
    val size: Int
    fun getX(n: Int): Double
    fun getY(n: Int): Double

    fun get(n: Int, component: Int): Double = when (component) {
        0 -> getX(n)
        1 -> getY(n)
        else -> throw IllegalArgumentException("$component")
    }

    fun translated(point: GeoPoint): SimpleGeoEntity

    fun boundingBox(): GeoRect {
        if (size == 0) {
            return Rect(0.0, 0.0, 0.0, 0.0)
        }
        val xs = DoubleArray(size, this::getX)
        val ys = DoubleArray(size, this::getY)

        val minX = xs.min()!!
        val maxX = xs.max()!!
        val minY = ys.min()!!
        val maxY = ys.max()!!

        return Rect(minX, minY, maxX - minX, maxY - minY)
    }

}

interface GeoEntity<ImmutableType, MutableType> : SimpleGeoEntity
        where ImmutableType : GeoEntity<ImmutableType, MutableType>,
              MutableType : GeoEntity<ImmutableType, MutableType> {

    fun toImmutable(): ImmutableType
    fun toMutable(): MutableType
}
