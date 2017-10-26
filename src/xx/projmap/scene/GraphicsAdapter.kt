package xx.projmap.scene

import xx.projmap.geometry.*

interface GraphicsAdapter {
    fun drawPoint(point: GeoPoint)
    fun clear(x: Double, y: Double, w: Double, h: Double)

    fun drawQuad(quad: Quad) {
        drawPointArray(quad.toPointArray())
    }

    fun drawRect(rect: GeoRect) {
        drawQuad(rect.toQuad())
    }

    fun drawPointArray(pointArray: Array<out GeoPoint>)

    fun push()
    fun translate(x: Double, y: Double)
    fun scale(x: Double, y: Double)
    fun pop()
}

fun GraphicsAdapter.createViewport(region: GeoRect) = Viewport(region.toMutable(), this)