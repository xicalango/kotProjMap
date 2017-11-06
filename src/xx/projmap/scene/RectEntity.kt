package xx.projmap.scene

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform
import xx.projmap.geometry.toPointArray

class RectEntity(rect: GeoRect, origin: MutPoint = MutPoint(), visible: Boolean = true, tag: String? = null) : Entity(origin, visible, tag) {

    val rect = rect.toMutable()

    val translatedRect: GeoRect
        get() = rect.translated(origin)

    private val dstPointArray: Array<MutPoint> = Array(4, { MutPoint() })

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transformRect(transform)
        graphicsAdapter.drawPointArray(dstPointArray)
    }

    private fun transformRect(transform: Transform) {
        val srcPointArray = rect.translated(origin).toPointArray()
        srcPointArray.forEachIndexed { index, point ->
            transform.srcToDst(point, dstPointArray[index])
        }
    }
}

fun GeoRect.toEntity() = RectEntity(this)
