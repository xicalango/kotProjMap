package xx.projmap.scene

class World {
    val entities: MutableList<Entity> = ArrayList()

    operator fun get(tag: String): List<Entity> = entities.flatMap { it.childEntities }.filter { it.tag == tag }
}