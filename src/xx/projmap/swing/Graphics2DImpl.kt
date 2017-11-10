package xx.projmap.swing

import xx.projmap.geometry.GeoPoint
import xx.projmap.graphics.DrawStyle
import xx.projmap.graphics.GraphicsAdapter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.util.*

class Graphics2DImpl(var graphics2D: Graphics2D) : GraphicsAdapter {

    private val matrixStack: Stack<AffineTransform> = Stack()

    override var color: Color
        get() = graphics2D.color
        set(value) {
            graphics2D.color = value
        }

    override var backgroundColor: Color
        get() = graphics2D.background
        set(value) {
            graphics2D.color = value
        }

    override fun resetClip() {
        graphics2D.clip = null
    }

    override fun clip(x: Double, y: Double, w: Double, h: Double) {
        graphics2D.clipRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

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

    override fun drawPoint(point: GeoPoint, drawStyle: DrawStyle) {
        when (drawStyle) {
            DrawStyle.LINE ->
                graphics2D.drawOval(point.x.toInt() - 2, point.y.toInt() - 2, 4, 4)
            DrawStyle.FILL ->
                graphics2D.fillOval(point.x.toInt() - 2, point.y.toInt() - 2, 4, 4)
        }
    }

    override fun drawPointArray(pointArray: Array<out GeoPoint>, drawStyle: DrawStyle) {
        val xs = pointArray.map(GeoPoint::x).map(Number::toInt).toIntArray()
        val ys = pointArray.map(GeoPoint::y).map(Number::toInt).toIntArray()
        val nPoints = Math.min(xs.size, ys.size)
        when (drawStyle) {
            DrawStyle.LINE -> graphics2D.drawPolygon(xs, ys, nPoints)
            DrawStyle.FILL -> graphics2D.fillPolygon(xs, ys, nPoints)
        }
    }

}

