package xx.projmap.swing

import xx.projmap.geometry.GeoPoint
import xx.projmap.scene.GraphicsAdapter
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.util.*

class Graphics2DImpl(var graphics2D: Graphics2D) : GraphicsAdapter {

    private val matrixStack: Stack<AffineTransform> = Stack()

    override fun push() {
        matrixStack.push(graphics2D.transform)
    }

    override fun translate(x: Double, y: Double) {
        graphics2D.translate(x, y)
    }

    override fun scale(x: Double, y: Double) {
        graphics2D.scale(x, y)
    }

    override fun pop() {
        if (matrixStack.empty()) {
            return
        }

        val transform = matrixStack.pop()
        graphics2D.transform = transform
    }

    override fun clear(x: Double, y: Double, w: Double, h: Double) {
        graphics2D.clearRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun drawPoint(point: GeoPoint) {
        graphics2D.drawOval(point.x.toInt(), point.y.toInt(), 5, 5)
    }

    override fun drawPointArray(pointArray: Array<out GeoPoint>) {
        val xs = pointArray.map(GeoPoint::x).map(Number::toInt).toIntArray()
        val ys = pointArray.map(GeoPoint::y).map(Number::toInt).toIntArray()
        graphics2D.drawPolygon(xs, ys, 4)
    }

}

