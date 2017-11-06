package xx.projmap.scene

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform
import java.awt.Color

open class Entity(val origin: MutPoint = MutPoint(), var visible: Boolean = true, val tag: String? = null) {

    var color: Color = Color.WHITE

    open val childEntities: List<Entity>
        get() = listOf(this)

    fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        assert(visible)
        graphicsAdapter.withColor(color, { adapter ->
            renderInternal(adapter, transform)
        })
    }

    protected open fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
    }

    fun move(dx: Double = 0.0, dy: Double = 0.0) {
        origin.move(dx, dy)
    }
}

