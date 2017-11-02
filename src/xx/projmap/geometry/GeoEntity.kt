package xx.projmap.geometry

interface SimpleGeoEntity {
    val size: Int
    fun getX(n: Int): Double
    fun getY(n: Int): Double
    fun toNestedArrays(): Array<DoubleArray> = Array(size, { doubleArrayOf(getX(it), getY(it)) })
}

interface GeoEntity<ImmutableType, MutableType> : SimpleGeoEntity
        where ImmutableType : GeoEntity<ImmutableType, MutableType>,
              MutableType : GeoEntity<ImmutableType, MutableType> {

    fun toImmutable(): ImmutableType
    fun toMutable(): MutableType
}
