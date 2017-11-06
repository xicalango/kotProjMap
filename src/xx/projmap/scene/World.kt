package xx.projmap.scene

class World {
    val entities: MutableList<Entity> = ArrayList()

    operator fun get(tag: String): List<Entity> = entities.flatMap { it.childEntities }.filter { it.tag == tag }

    inline fun <reified T : Entity> get(): List<T> = entities.flatMap { it.childEntities }.filterIsInstance<T>()
}