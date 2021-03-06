package xx.projmap.scene2

import xx.projmap.events.Event
import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.IdentityTransform
import xx.projmap.geometry.Transform
import xx.projmap.graphics.GraphicsAdapter
import xx.projmap.graphics.RenderDestination
import xx.projmap.graphics.RenderableScene
import java.util.*

interface SceneFacade {
    val entities: List<Entity>
    val allEntities: List<Entity>

    val simulation: Simulation

    fun <T : Entity> createEntity(constructor: () -> T, parent: Entity? = null, name: String? = null): T
}

inline fun <reified T : Entity> SceneFacade.findEntity(): T? = entities.filterIsInstance<T>().firstOrNull()

inline fun <reified T : Entity> SceneFacade.findEntities(): List<T> = entities.filterIsInstance<T>()

fun SceneFacade.findEntityByTag(tag: String): List<Entity> = entities.filter { it.tag == tag }

fun SceneFacade.getCameras(): List<CameraEntity> {
    return synchronized(entities) {
        entities.filterIsInstance<CameraEntity>()
    }
}

fun SceneFacade.getMainCamera(): CameraEntity = getCameras().find { it.name == "mainCamera" } ?: getCameras().first()

fun SceneFacade.createCamera(region: GeoRect, renderDestination: RenderDestination, transform: Transform = IdentityTransform, name: String = "camera"): CameraEntity =
        createEntity({ CameraEntity(region, renderDestination, transform) }, name = name)

class Scene : SceneFacade, RenderableScene {

    override val entities: MutableList<Entity> = ArrayList()

    private val addEntities: MutableList<Entity> = ArrayList()

    override lateinit var simulation: Simulation
        private set

    override val allEntities: List<Entity>
        get() = entities.flatMap(Entity::allChildren)

    override fun <T : Entity> createEntity(constructor: () -> T, parent: Entity?, name: String?): T {
        val newEntity = constructor()
        if (name != null) {
            newEntity.name
        }

        newEntity.initialize(this)

        if (parent == null) {
            addEntities += newEntity
        } else {
            parent.addChild(newEntity)
        }
        return newEntity
    }

    fun startFrame() {
        if (addEntities.size > 0) {
            synchronized(entities) {
                entities += addEntities
            }
            addEntities.clear()
        }
    }

    fun initialize(simulation: Simulation) {
        this.simulation = simulation
        startFrame()
    }

    fun update(dt: Double) {
        entities.forEach { it.update(dt) }
        val destroyEntities = entities.filter { it.destroy }
        if (destroyEntities.isNotEmpty()) {
            synchronized(entities) {
                entities -= destroyEntities
            }
        }
    }

    fun handleEvents(events: List<Event>) {
        events.forEach { event ->
            entities.forEach { entity ->
                entity.handleEvent(event)
            }
        }
    }

    override fun render(graphicsAdapter: GraphicsAdapter) {
        getCameras().forEach { it.camera.render(graphicsAdapter) }
    }
}