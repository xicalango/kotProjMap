package xx.projmap.geometry

interface Transform {
    fun srcToDst(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint
    fun dstToSrc(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint
}

class IdentityTransform : Transform {
    override fun srcToDst(srcPoint: GeoPoint, dstPoint: MutPoint): MutPoint = dstPoint.updateFrom(srcPoint)
    override fun dstToSrc(srcPoint: GeoPoint, dstPoint: MutPoint): MutPoint = dstPoint.updateFrom(srcPoint)
}