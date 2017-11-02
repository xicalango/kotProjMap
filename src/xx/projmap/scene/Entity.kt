package xx.projmap.scene

import xx.projmap.geometry.MutPoint

open class Entity(protected val origin: MutPoint = MutPoint()) {
    private var updated: Boolean = true

    fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        if (updated) {
            println("updating cache")
            updateCache(transform)
            updated = false
        }

        renderEntity(graphicsAdapter)
    }

    fun move(dx: Double = 0.0, dy: Double = 0.0) {
        origin.x += dx
        origin.y += dy
        updated = true
    }

    open fun renderEntity(graphicsAdapter: GraphicsAdapter) {
    }

    open fun updateCache(transform: Transform) {

    }
}
