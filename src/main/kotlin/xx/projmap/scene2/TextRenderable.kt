package xx.projmap.scene2

import xx.projmap.geometry.*
import xx.projmap.graphics.GraphicsAdapter
import xx.projmap.graphics.render4x6ToPoints
import java.awt.Color

class TextRenderable(text: String = "", color: Color = Color.WHITE, ignoreTransform: Boolean = false) : Renderable(color, ignoreTransform) {
    override val boundingBox: GeoRect
        get() = pointArray.boundingBox + entity.position

    var pointArray: Array<Point> = emptyArray()
        private set

    private var dstPointArray: Array<MutPoint> = emptyArray()

    var xPointSpacing: Double = 10.0
        set(value) {
            field = value
            resetPointArray()
        }

    var yPointSpacing: Double = 10.0
        set(value) {
            field = value
            resetPointArray()
        }

    var xLetterSpacingFactor: Double = 4.0
        set(value) {
            field = value
            resetPointArray()
        }

    var text: String = text
        set(value) {
            field = value
            resetPointArray()
            dstPointArray = Array(pointArray.size, { MutPoint() })
        }

    init {
        this.text = text
    }

    fun setSpacing(spacing: Double) {
        xPointSpacing = spacing
        yPointSpacing = spacing
    }

    private fun resetPointArray() {
        pointArray = render4x6ToPoints(text, xPointSpacing = xPointSpacing, yPointSpacing = yPointSpacing, xLetterSpacingFactor = xLetterSpacingFactor).toTypedArray()
    }

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform(transform)
        dstPointArray.forEach { graphicsAdapter.drawPoint(it, drawStyle) }
    }

    private fun transform(transform: Transform) {
        pointArray.forEachIndexed { index, point ->
            transform.srcToDst(point + entity.position, dstPointArray[index])
        }
    }

}