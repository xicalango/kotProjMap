package xx.projmap.geometry

interface Transform {
    fun srcToDst(src: GeoPoint, dst: MutPoint = MutPoint()) : MutPoint
    fun dstToSrc(src: GeoPoint, dst: MutPoint = MutPoint()) : MutPoint
}

class ProjectionTransform(private val transformation: Transformation) : Transform {

    override fun srcToDst(src: GeoPoint, dst: MutPoint): MutPoint =
            transformation.srcToDst(src, dst)

    override fun dstToSrc(src: GeoPoint, dst: MutPoint): MutPoint =
            transformation.dstToSrc(src, dst)

}

class IdentityTransform : Transform {
    override fun srcToDst(src: GeoPoint, dst: MutPoint): MutPoint {
        dst.updateFrom(src)
        return dst
    }

    override fun dstToSrc(src: GeoPoint, dst: MutPoint): MutPoint {
        dst.updateFrom(src)
        return dst
    }
}