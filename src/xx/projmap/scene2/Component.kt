package xx.projmap.scene2

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import java.util.*

abstract class Component(enabled: Boolean = true) {

    internal lateinit var entity: Entity

    var enabled: Boolean = enabled
        set(value) {
            field = value
            if (value) {
                onActivation()
            } else {
                onDeactivation()
            }
        }

    val scene: SceneFacade
        get() = entity.scene

    val config: Properties
        get() = scene.config

    open fun initialize() = Unit

    open fun setup() = Unit

    open fun update(dt: Double) = Unit

    open fun onKeyPressed(event: KeyEvent) = Unit

    open fun onKeyReleased(event: KeyEvent) = Unit

    open fun onMouseClicked(event: MouseClickEvent) = Unit

    open fun onActivation() = Unit

    open fun onDeactivation() = Unit

}