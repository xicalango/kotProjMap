package xx.projmap.scene

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform
import java.awt.Color

open class Entity(val origin: MutPoint = MutPoint(), val visible: Boolean = true) {

    var color: Color = Color.WHITE

    fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        if (!visible) {
            return
        }
        graphicsAdapter.color = color
        renderInternal(graphicsAdapter, transform)
    }

    protected open fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
    }

    fun move(dx: Double = 0.0, dy: Double = 0.0) {
        origin.x += dx
        origin.y += dy
    }
}
