package xx.projmap.scene2

import xx.projmap.geometry.*
import xx.projmap.graphics.GraphicsAdapter

class RectRenderable(rect: GeoRect) : Renderable() {

    val rect = rect.toMutable()

    override val boundingBox: GeoRect
        get() = (rect + entity.position).boundingBox()

    private val dstPointArray: Array<MutPoint> = Array(4, { MutPoint() })

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transformRect(transform, entity.position)
        graphicsAdapter.drawPointArray(dstPointArray)
    }

    private fun transformRect(transform: Transform, origin: GeoPoint) {
        val srcPointArray = rect.translated(origin).toPointArray()
        srcPointArray.forEachIndexed { index, point ->
            transform.srcToDst(point, dstPointArray[index])
        }
    }
}