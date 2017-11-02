package xx.projmap.scene

import xx.projmap.geometry.MutPoint

open class Entity(protected val origin: MutPoint = MutPoint()) {

    open fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
    }

    fun move(dx: Double = 0.0, dy: Double = 0.0) {
        origin.x += dx
        origin.y += dy
    }
}
