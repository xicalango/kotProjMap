package xx.projmap.scene2

import xx.projmap.scene.GraphicsAdapter

abstract class Component(var enabled: Boolean = true) {

    internal lateinit var entity: Entity

    open fun setup() {

    }

    open fun teardown() {

    }

    open fun update(dt: Double) {

    }

    open fun render(graphicsAdapter: GraphicsAdapter) {

    }

}