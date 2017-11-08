package xx.projmap.scene2

import xx.projmap.scene.KeyEvent
import xx.projmap.scene.MouseClickEvent

abstract class Component(var enabled: Boolean = true) {

    internal lateinit var entity: Entity


    open fun setup() {

    }

    open fun update(dt: Double) {

    }

    open fun onKeyPressed(event: KeyEvent) {

    }

    open fun onKeyReleased(event: KeyEvent) {

    }

    open fun onMouseClicked(event: MouseClickEvent) {

    }

}