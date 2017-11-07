package xx.projmap.scene2

abstract class Component(var enabled: Boolean = true) {

    internal lateinit var entity: Entity

    private var initialized: Boolean = false

    fun initializeComponent() {
        if (initialized) {
            return
        }
        initialize()
        initialized = true
    }

    open fun initialize() {

    }

    open fun update(dt: Double) {

    }

}