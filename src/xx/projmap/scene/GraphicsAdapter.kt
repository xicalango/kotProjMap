package xx.projmap.scene

import xx.projmap.geometry.*
import java.awt.Color

enum class DrawStyle {
    LINE,
    FILL
}

interface GraphicsAdapter {
    fun clear(x: Double, y: Double, w: Double, h: Double)

    fun drawPoint(point: GeoPoint)
    fun drawPointArray(pointArray: Array<out GeoPoint>, drawStyle: DrawStyle = DrawStyle.FILL)

    fun drawGeoEntity(geoEntity: SimpleGeoEntity, drawStyle: DrawStyle = DrawStyle.FILL) =
            drawPointArray(geoEntity.toPointArray(), drawStyle)

    fun push()
    fun translate(x: Double, y: Double)
    fun scale(x: Double, y: Double)
    fun pop()

    var color: Color
    var backgroundColor: Color
}

fun GraphicsAdapter.createViewport(region: GeoRect) = Viewport(region.toMutable(), this)