package xx.projmap.scene

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform

class PointEntity(origin: MutPoint = MutPoint(), visible: Boolean = true) : Entity(origin, visible) {

    private val transformedPoint = MutPoint()

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform.srcToDst(origin, transformedPoint)
        graphicsAdapter.drawPoint(transformedPoint)
    }
}