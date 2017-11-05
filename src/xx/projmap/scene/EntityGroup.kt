package xx.projmap.scene

import xx.projmap.geometry.Transform

class EntityGroup(visible: Boolean = true) : Entity(visible = visible) {

    val entities: MutableList<Entity> = ArrayList()

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        entities.forEach { it.render(graphicsAdapter, transform) }
    }
}