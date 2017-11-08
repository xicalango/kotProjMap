package xx.projmap.scene2

import xx.projmap.events.Direction
import xx.projmap.events.Event
import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Point
import xx.projmap.geometry.Transform
import xx.projmap.graphics.GraphicsAdapter

open class Entity(var name: String = "entity", origin: GeoPoint = Point()) {

    lateinit var sceneFacade: SceneFacade

    protected val children: MutableList<Entity> = ArrayList()

    var parent: Entity? = null

    var tag: Tag? = null

    protected val components: MutableList<Component> = ArrayList()

    val origin: MutPoint = MutPoint()

    private var initialized: Boolean = false

    val position: GeoPoint
        get() {
            val par = parent
            return if (par == null) {
                origin
            } else {
                origin + par.position
            }
        }

    val enabledComponents: List<Component>
        get() = components.filter(Component::enabled)

    val allComponents: List<Component>
        get() = components

    val allChildren: List<Entity>
        get() = listOf(this) + children

    init {
        this.origin.updateFrom(origin)
    }

    fun addComponent(component: Component) {
        component.entity = this
        components += component
    }

    fun removeComponent(component: Component) {
        components -= component
    }

    fun addChild(entity: Entity) {
        entity.parent = this
        synchronized(children) {
            // TODO wah
            children += entity
        }
    }

    fun moveChild(child: Entity, destination: Entity?) {
        children -= child

        if (destination == null) {
            child.parent = null
        } else {
            destination.addChild(child)
        }
    }

    fun handleEvent(event: Event) {
        enabledComponents.forEach { component ->
            when (event) {
                is MouseClickEvent -> component.onMouseClicked(event)
                is KeyEvent -> when (event.direction) {
                    Direction.PRESSED -> component.onKeyPressed(event)
                    Direction.RELEASED -> component.onKeyReleased(event)
                }
            }
        }
        children.forEach { it.handleEvent(event) }
    }

    internal inline fun <reified T : Entity> findChild(): T? = children.filterIsInstance<T>().firstOrNull()
    internal inline fun <reified T : Entity> findChildren(): List<T> = children.filterIsInstance<T>()

    internal inline fun <reified T : Component> findComponent(): T? = findComponents<T>().firstOrNull()
    internal inline fun <reified T : Component> findComponents(): List<T> = components.filterIsInstance<T>()

    fun update(dt: Double) {
        if (!initialized) {
            children.forEach { it.sceneFacade = sceneFacade }
            allComponents.forEach { it.setup() }
            initialized = true
        }
        enabledComponents.forEach { it.update(dt) }
        children.forEach { it.update(dt) }
    }

    internal fun render(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        components.forEach { component ->
            if (component.enabled && component is Renderable) {
                component.render(graphicsAdapter, transform)
            }
        }

        synchronized(children) {
            children.forEach { it.render(graphicsAdapter, transform) }
        }
    }

}
