package xx.projmap.scene

import xx.projmap.geometry.*
import java.awt.Color

class RectEntity(origin: MutPoint = MutPoint(), val w: Double = 0.0, val h: Double = 0.0) : Entity(origin) {

    private var dstPointArray: Array<GeoPoint>? = null

    override fun renderEntity(graphicsAdapter: GraphicsAdapter) {
        graphicsAdapter.color = Color.GREEN
        graphicsAdapter.backgroundColor = Color.RED
        graphicsAdapter.drawPointArray(dstPointArray!!)
    }

    override fun updateCache(transform: Transform?) {
        val srcPointArray = Rect(origin.x, origin.y, w, h).toQuad().toPointArray()
        dstPointArray = srcPointArray.map { transform?.srcToDst(it) ?: it }.toTypedArray()
    }
}
