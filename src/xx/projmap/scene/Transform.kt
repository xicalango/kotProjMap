package xx.projmap.scene

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint

interface Transform {
    fun srcToDst(src: GeoPoint, dst: MutPoint)
    fun dstToSrc(src: GeoPoint, dst: MutPoint)
}
