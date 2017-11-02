package xx.projmap.scene

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform

class PointEntity(origin: MutPoint = MutPoint()) : Entity(origin) {

    private val transformedPoint = MutPoint()

    override fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform.srcToDst(origin, transformedPoint)
        graphicsAdapter.drawPoint(transformedPoint)
    }
}