package xx.projmap.scene

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint

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