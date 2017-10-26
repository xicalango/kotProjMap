package xx.projmap.scene

import xx.projmap.geometry.MutPoint

class PointEntity(origin: MutPoint = MutPoint()) : Entity(origin) {

    private val transformedPoint = MutPoint()

    override fun renderEntity(graphicsAdapter: GraphicsAdapter) {
        graphicsAdapter.drawPoint(transformedPoint)
    }

    override fun updateCache(transform: Transform?) {
        if (transform != null) {
            transform.srcToDst(origin, transformedPoint)
        } else {
            transformedPoint.updateFrom(origin)
        }
    }
}