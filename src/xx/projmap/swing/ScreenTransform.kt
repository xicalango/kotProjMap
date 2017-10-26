package xx.projmap.swing

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.MutRect
import xx.projmap.scene.Transform

class ScreenTransform(private val cameraRect: MutRect, private val screenRect: MutRect) : Transform {

    override fun srcToDst(src: GeoPoint, dst: MutPoint) = cameraRect.transformTo(src, dst, screenRect)
    override fun dstToSrc(src: GeoPoint, dst: MutPoint) = screenRect.transformTo(src, dst, cameraRect)

}

