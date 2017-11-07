package xx.projmap.scene2

import xx.projmap.scene.GraphicsAdapter

interface RenderFacade {
    fun render(graphicsAdapter: GraphicsAdapter)
}

interface SceneFacade {
    val entities: List<Entity>
    val allEntities: List<Entity>

    fun addEntity(entity: Entity)
}

class Scene : SceneFacade, RenderFacade {
    override val entities: MutableList<Entity> = ArrayList()

    override val allEntities: List<Entity>
        get() = entities.flatMap(Entity::allChildren)

    override fun addEntity(entity: Entity) {
        entity.sceneFacade = this
        entities += entity
    }

    fun initialize() {
        entities.forEach { it.initialize() }
    }

    fun update(dt: Double) {
        allEntities.forEach { it.update(dt) }
    }

    override fun render(graphicsAdapter: GraphicsAdapter) {
        allEntities
                .flatMap { it.getComponentsByType<Camera>() }
                .forEach { it.render(graphicsAdapter) }
    }
}