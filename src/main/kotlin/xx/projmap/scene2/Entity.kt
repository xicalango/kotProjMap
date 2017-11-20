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

    lateinit var scene: SceneFacade

    protected val children: MutableList<Entity> = ArrayList()

    var parent: Entity? = null

    var tag: String? = null

    protected val components: MutableList<Component> = ArrayList()

    val origin: MutPoint = MutPoint()

    private var initialized: Boolean = false

    var destroy: Boolean = false

    val position: GeoPoint
        get() {
            return parent?.let { origin + it.position } ?: origin
        }

    val enabledComponents: List<Component>
        get() = components.filter(Component::enabled)

    val allComponents: List<Component>
        get() = components

    val allChildren: List<Entity>
        get() = listOf(this) + children

    init {
        this.origin.set(origin)
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

    fun moveChild(child: Entity, destination: Entity) {
        synchronized(children) {
            // TODO wah
            children -= child
        }

        destination.addChild(child)
    }

    fun handleEvent(event: Event) {
        enabledComponents.forEach { component ->
            when (event) {
                is MouseClickEvent -> component.onMouseClicked(event)
                is KeyEvent -> when (event.direction) {
                    Direction.PRESSED -> component.onKeyPressed(event)
                    Direction.RELEASED -> component.onKeyReleased(event)
                }
                else -> Unit
            }
        }
        children.forEach { it.handleEvent(event) }
    }

    internal inline fun <reified T : Entity> findChild(): T? = findChildren<T>().firstOrNull()
    internal inline fun <reified T : Entity> findChildren(): List<T> = children.filterIsInstance<T>()

    internal inline fun <reified T : Component> findComponent(): T? = findComponents<T>().firstOrNull()
    internal inline fun <reified T : Component> findComponents(): List<T> = components.filterIsInstance<T>()

    fun initialize(scene: SceneFacade) {
        this.scene = scene
        allComponents.forEach { it.initialize() }
        children.forEach { it.initialize(scene) }
    }

    fun update(dt: Double) {
        if (!initialized) {
            allComponents.forEach { it.setup() }
            initialized = true
        }
        enabledComponents.forEach { it.update(dt) }
        children.forEach {
            it.update(dt)
        }
        val destroyChildren = children.filter { it.destroy }
        if (destroyChildren.isNotEmpty()) {
            synchronized(children) {
                children -= destroyChildren
            }
        }
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
