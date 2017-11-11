package xx.projmap.scene2

import xx.projmap.geometry.*
import xx.projmap.graphics.GraphicsAdapter
import java.awt.Color

class RectRenderable(rect: GeoRect = MutRect(0.0, 0.0, 1.0, 1.0), color: Color = Color.WHITE, ignoreTransform: Boolean = false) : Renderable(color, ignoreTransform) {

    val rect = rect.toMutable()

    override val boundingBox: GeoRect
        get() = (rect + entity.position).boundingBox()

    private val dstPointArray: Array<MutPoint> = Array(4, { MutPoint() })

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transformRect(transform, entity.position)
        graphicsAdapter.drawPointArray(dstPointArray, drawStyle)
    }

    private fun transformRect(transform: Transform, origin: GeoPoint) {
        val srcPointArray = rect.translated(origin).toPointArray()
        srcPointArray.forEachIndexed { index, point ->
            transform.srcToDst(point, dstPointArray[index])
        }
    }
}