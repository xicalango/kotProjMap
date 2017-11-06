package xx.projmap.scene

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform

class PointEntity(origin: MutPoint = MutPoint(), visible: Boolean = true, tag: String? = null) : Entity(origin, visible, tag) {

    private val transformedPoint = MutPoint()

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform.srcToDst(origin, transformedPoint)
        graphicsAdapter.drawPoint(transformedPoint)
    }
}