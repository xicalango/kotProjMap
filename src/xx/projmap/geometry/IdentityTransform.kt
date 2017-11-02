package xx.projmap.geometry

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