package xx.projmap.geometry

import xx.projmap.scene.Transform

class ProjectionTransform(private val transformation: Transformation) : Transform {

    override fun srcToDst(src: GeoPoint, dst: MutPoint) {
        transformation.srcToDst(src, dst)
    }

    override fun dstToSrc(src: GeoPoint, dst: MutPoint) {
        transformation.dstToSrc(src, dst)
    }

}