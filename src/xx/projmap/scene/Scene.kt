package xx.projmap.scene

import xx.projmap.events.EventQueue

class Scene(val world: World = World()) {

    val cameras: MutableList<Camera> = ArrayList()

    val eventQueue = EventQueue()

    fun render() {
        cameras.forEach { it.render(world) }
    }

}