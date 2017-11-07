package xx.projmap.scene2

import xx.projmap.geometry.Transform
import xx.projmap.scene.GraphicsAdapter
import xx.projmap.scene.withColor
import java.awt.Color

abstract class Renderable(val color: Color = Color.WHITE) : Component() {

    fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        graphicsAdapter.withColor(color, {
            renderInternal(graphicsAdapter, transform)
        })
    }

    abstract fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform)

}