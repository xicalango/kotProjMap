package xx.projmap.scene2

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Point

open class Entity(val name: String = "entity", origin: GeoPoint = Point()) {

    lateinit var sceneFacade: SceneFacade
    private val children: MutableList<Entity> = ArrayList()
    var parent: Entity? = null
    var tag: Tag? = null
    protected val components: MutableList<Component> = ArrayList()
    val origin: MutPoint = MutPoint()
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
        children += entity
    }

    fun moveChild(child: Entity, destination: Entity?) {
        children -= child

        if (destination == null) {
            child.parent = null
        } else {
            destination.addChild(child)
        }
    }

    fun initialize() {
        components.forEach { it.initialize() }
    }

    internal inline fun <reified T : Component> getComponentsByType(): List<T> = components.filterIsInstance<T>()
    internal inline fun <reified T : Component> getComponentByType(): T? = getComponentsByType<T>().firstOrNull()

    fun update(dt: Double) {
        enabledComponents.forEach { it.update(dt) }
    }

}

fun <T : Component> Collection<T>.isEnabled() = filter { it.enabled }
