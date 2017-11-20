package xx.projmap.geometry

interface Transform {
    fun srcToDst(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint
    fun dstToSrc(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint
}

object IdentityTransform : Transform {
    override fun srcToDst(srcPoint: GeoPoint, dstPoint: MutPoint): MutPoint = dstPoint.set(srcPoint)
    override fun dstToSrc(srcPoint: GeoPoint, dstPoint: MutPoint): MutPoint = dstPoint.set(srcPoint)
}