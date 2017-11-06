package xx.projmap.scene

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Point
import xx.projmap.geometry.Transform

class TextEntity(text: String, origin: MutPoint = MutPoint(), visible: Boolean = true, tag: String? = null) : Entity(origin, visible, tag) {

    private var pointArray: Array<Point> = emptyArray()
    private var dstPointArray: Array<MutPoint> = emptyArray()

    var xPointSpacing: Double = 5.0
        set(value) {
            field = value
            resetPointArray()
        }

    var yPointSpacing: Double = 5.0
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

    private fun resetPointArray() {
        pointArray = render4x6ToPoints(text, xPointSpacing = xPointSpacing, yPointSpacing = yPointSpacing, xLetterSpacingFactor = xLetterSpacingFactor).toTypedArray()
    }

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform(transform)
        dstPointArray.forEach(graphicsAdapter::drawPoint)
    }

    private fun transform(transform: Transform) {
        pointArray.forEachIndexed { index, point ->
            transform.srcToDst(point.translated(origin), dstPointArray[index])
        }
    }
}
