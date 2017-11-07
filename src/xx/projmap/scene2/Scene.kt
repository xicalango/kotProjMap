package xx.projmap.scene2

import xx.projmap.scene.GraphicsAdapter
import xx.projmap.scene.Viewport

interface RenderFacade {
    fun render(graphicsAdapter: GraphicsAdapter)
}

interface SceneFacade {
    val entities: List<Entity>
    val viewports: MutableMap<String, Viewport>
    val tags: Map<String, Tag>

    val allEntities: List<Entity>

    fun addEntity(entity: Entity)

    fun getTag(name: String): Tag
}

class Scene : SceneFacade, RenderFacade {
    override val tags: MutableMap<String, Tag> = HashMap()
    override val viewports: MutableMap<String, Viewport> = HashMap()
    override val entities: MutableList<Entity> = ArrayList()

    override val allEntities: List<Entity>
        get() = entities.flatMap(Entity::allChildren)

    override fun addEntity(entity: Entity) {
        entity.sceneFacade = this
        entities += entity
    }

    override fun getTag(name: String): Tag {
        return tags.computeIfAbsent(name, ::Tag)
    }

    override fun render(graphicsAdapter: GraphicsAdapter) {
        allEntities
                .flatMap { it.getComponentsByType<Camera>() }
                .forEach { it.render(graphicsAdapter) }
    }
}