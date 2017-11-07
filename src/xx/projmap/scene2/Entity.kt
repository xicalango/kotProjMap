package xx.projmap.scene2

import xx.projmap.scene.GraphicsAdapter

open class Entity(val name: String = "entity") {

    lateinit var sceneFacade: SceneFacade
    val children: MutableList<Entity> = ArrayList()
    var tag: Tag? = null
    protected val components: MutableList<Component> = ArrayList()
    val origin: Origin = Origin()

    val enabledComponents: List<Component>
        get() = components.filter(Component::enabled)

    val allComponents: List<Component>
        get() = components

    val allChildren: List<Entity>
        get() = listOf(this) + children

    init {
        addComponent(origin)
    }

    fun addComponent(component: Component) {
        component.entity = this
        components += component
        component.setup()
    }

    fun removeComponent(component: Component) {
        components -= component
        component.teardown()
    }

    internal inline fun <reified T : Component> getComponentsByType(): List<T> = components.filterIsInstance<T>()
    internal inline fun <reified T : Component> getComponentByType(): T? = getComponentsByType<T>().firstOrNull()

    fun update(dt: Double) {
        enabledComponents.forEach { it.update(dt) }
    }

    fun render(graphicsAdapter: GraphicsAdapter) {
        enabledComponents.forEach { it.render(graphicsAdapter) }
    }

}

fun <T : Component> Collection<T>.isEnabled() = filter { it.enabled }
