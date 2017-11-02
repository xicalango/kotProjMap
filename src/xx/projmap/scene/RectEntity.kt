package xx.projmap.scene

import xx.projmap.geometry.*
import java.awt.Color

class RectEntity(origin: MutPoint = MutPoint(), private val w: Double = 0.0, private val h: Double = 0.0) : Entity(origin) {

    private val dstPointArray: Array<MutPoint> = Array(4, { MutPoint() })

    override fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        graphicsAdapter.color = Color.GREEN
        graphicsAdapter.backgroundColor = Color.RED

        transformRect(transform)
        graphicsAdapter.drawPointArray(dstPointArray)
    }

    private fun transformRect(transform: Transform) {
        val srcPointArray = Rect(origin.x, origin.y, w, h).toPointArray()
        srcPointArray.forEachIndexed { index, point ->
            transform.srcToDst(point, dstPointArray[index])
        }
    }

}
