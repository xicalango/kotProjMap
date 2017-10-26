package xx.projmap.geometry

interface GeoEntity<ImmutableType, MutableType>
        where ImmutableType : GeoEntity<ImmutableType, MutableType>,
              MutableType : GeoEntity<ImmutableType, MutableType> {
    val size: Int

    fun getX(n: Int): Double
    fun getY(n: Int): Double

    fun toNestedArrays(): Array<DoubleArray> = Array(size, { doubleArrayOf(getX(it), getY(it)) })

    fun toImmutable(): ImmutableType
    fun toMutable(): MutableType
}
