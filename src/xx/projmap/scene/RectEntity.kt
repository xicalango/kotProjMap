package xx.projmap.scene

import xx.projmap.geometry.*
import java.awt.Color

class RectEntity(origin: MutPoint = MutPoint(), private val w: Double = 0.0, private val h: Double = 0.0) : Entity(origin) {

    private val dstPointArray: Array<MutPoint> = Array(4, { MutPoint() })

    override fun renderEntity(graphicsAdapter: GraphicsAdapter) {
        graphicsAdapter.color = Color.GREEN
        graphicsAdapter.backgroundColor = Color.RED
        graphicsAdapter.drawPointArray(dstPointArray)
    }

    override fun updateCache(transform: Transform) {
        val srcPointArray = Rect(origin.x, origin.y, w, h).toPointArray()
        srcPointArray.forEachIndexed { index, point ->
            transform.srcToDst(point, dstPointArray[index])
        }
    }
}
