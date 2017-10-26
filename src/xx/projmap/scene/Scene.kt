package xx.projmap.scene

class Scene(val world: World) {

    val cameras: MutableList<Camera> = ArrayList()

    fun render() {
        cameras.forEach { it.render(world) }
    }

}