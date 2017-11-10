package xx.projmap.scene2

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform
import xx.projmap.graphics.GraphicsAdapter

class PointRenderable : Renderable() {

    override val boundingBox: GeoRect
        get() = entity.position.boundingBox()

    private val transformedPoint = MutPoint()

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform.srcToDst(entity.position, transformedPoint)
        graphicsAdapter.drawPoint(transformedPoint, drawStyle)
    }

}