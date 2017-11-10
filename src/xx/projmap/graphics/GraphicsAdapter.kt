package xx.projmap.graphics

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.SimpleGeoEntity
import xx.projmap.geometry.toPointArray
import java.awt.Color

enum class DrawStyle {
    LINE,
    FILL
}

interface GraphicsAdapter {

    fun clear(x: Double, y: Double, w: Double, h: Double)
    fun clip(x: Double, y: Double, w: Double, h: Double)
    fun resetClip()

    fun drawPoint(point: GeoPoint, drawStyle: DrawStyle = DrawStyle.LINE)
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

inline fun GraphicsAdapter.withColor(color: Color, block: GraphicsAdapter.() -> Unit) {
    val currentColor = this.color
    this.color = color
    block(this)
    this.color = currentColor
}
