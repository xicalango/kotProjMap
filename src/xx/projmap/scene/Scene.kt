package xx.projmap.scene

class Scene(private val world: World) {

    val cameras: MutableList<Camera> = ArrayList()

    fun render() {
        cameras.forEach { it.render(world) }
    }

}