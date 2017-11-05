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

    fun toNestedArrays(): Array<DoubleArray> = Array(size, { doubleArrayOf(getX(it), getY(it)) })
}

interface GeoEntity<ImmutableType, MutableType> : SimpleGeoEntity
        where ImmutableType : GeoEntity<ImmutableType, MutableType>,
              MutableType : GeoEntity<ImmutableType, MutableType> {

    fun toImmutable(): ImmutableType
    fun toMutable(): MutableType
}
