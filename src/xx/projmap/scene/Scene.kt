package xx.projmap.scene

class Scene(val world: World = World()) {

    val cameras: MutableList<Camera> = ArrayList()

    fun render() {
        cameras.forEach { it.render(world) }
    }

}