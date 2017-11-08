package xx.projmap.scene

import xx.projmap.events.EventQueue

class Scene(val eventQueue: EventQueue, val world: World = World()) {

    val cameras: MutableList<Camera> = ArrayList()

    fun hideAllCameras() {
        cameras.forEach { it.visible = false }
    }

    fun showCamera(id: String) {
        cameras.filter { it.id == id }.forEach { it.visible = true }
    }

    fun render() {
        cameras.filter { it.visible }.forEach { it.render(world) }
    }

}