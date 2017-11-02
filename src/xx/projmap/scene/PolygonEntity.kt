package xx.projmap.scene

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint

class PolygonEntity(origin: MutPoint, vararg points: GeoPoint) : Entity(origin) {

    val path = points.toList()



    override fun renderEntity(graphicsAdapter: GraphicsAdapter) {
        super.renderEntity(graphicsAdapter)
    }

    override fun updateCache(transform: Transform) {
        super.updateCache(transform)
    }
}