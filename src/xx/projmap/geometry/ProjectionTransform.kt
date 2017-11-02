package xx.projmap.geometry

class ProjectionTransform(private val transformation: Transformation) : Transform {

    override fun srcToDst(src: GeoPoint, dst: MutPoint): MutPoint =
            transformation.srcToDst(src, dst)

    override fun dstToSrc(src: GeoPoint, dst: MutPoint): MutPoint =
            transformation.dstToSrc(src, dst)

}