package xx.projmap.scene2

import xx.projmap.events.Event
import xx.projmap.scene.GraphicsAdapter

interface RenderableScene {
    fun render(graphicsAdapter: GraphicsAdapter)
}

interface SceneFacade {
    val entities: List<Entity>
    val allEntities: List<Entity>

    val cameras: List<CameraEntity>
        get() = entities.filterIsInstance<CameraEntity>()

    fun addEntity(entity: Entity)
}

class Scene : SceneFacade, RenderableScene {
    override val entities: MutableList<Entity> = ArrayList()

    private val addEntities: MutableList<Entity> = ArrayList()

    override val allEntities: List<Entity>
        get() = entities.flatMap(Entity::allChildren)

    override fun addEntity(entity: Entity) {
        entity.sceneFacade = this
        addEntities += entity
    }

    fun startFrame() {
        entities += addEntities
        addEntities.clear()
    }

    fun update(dt: Double) {
        allEntities.forEach { it.update(dt) }
    }

    fun handleEvents(events: List<Event>) {
        events.forEach { event ->
            allEntities.forEach { entity ->
                entity.handleEvent(event)
            }
        }
    }

    override fun render(graphicsAdapter: GraphicsAdapter) {
        allEntities
                .flatMap { it.getComponentsByType<Camera>() }
                .forEach { it.render(graphicsAdapter) }
    }
}