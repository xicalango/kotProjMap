package xx.projmap.scene2

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.Transform
import xx.projmap.graphics.DrawStyle
import xx.projmap.graphics.GraphicsAdapter
import xx.projmap.graphics.withColor
import java.awt.Color

abstract class Renderable(var color: Color = Color.WHITE) : Component() {

    var drawStyle: DrawStyle = DrawStyle.FILL

    fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        graphicsAdapter.withColor(color, {
            renderInternal(graphicsAdapter, transform)
        })
    }

    abstract val boundingBox: GeoRect

    abstract fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform)

}