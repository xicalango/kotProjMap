package xx.projmap.app

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.MutRect
import xx.projmap.scene2.Behavior
import xx.projmap.scene2.BoxCollider
import xx.projmap.scene2.Entity
import xx.projmap.scene2.RectRenderable
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


class KeyEntity : Entity("key") {
    init {
        val rectRenderable = RectRenderable(MutRect(0.0, 0.0, 10.0, 10.0))
        addComponent(rectRenderable)
        addComponent(KeyBehavior())
        addComponent(BoxCollider(rectRenderable))
        addComponent(ActiveColorChanger())
    }
}

class KeyBehavior : Behavior() {

    var keyChar: Char? = null

}

class KeyboardEntity : Entity("keyboard") {

    init {
        addComponent(KeyboardBehavior())
    }

}

class KeyboardBehavior : Behavior() {

    private lateinit var keyPropertiesFile: String

    override fun initialize() {
        keyPropertiesFile = config.getProperty("keyboard.properties.file", "keyboard.properties")
        loadKeys()
    }

    private fun loadKeys() {
        val path = Paths.get(keyPropertiesFile)
        if (!Files.exists(path)) {
            return
        }

        val keyProperties = Properties()
        Files.newInputStream(path).use(keyProperties::load)

        val numKeys = keyProperties.getProperty("keys.count", "0").toInt()

        val point = MutPoint()
        val rect = MutRect()

        (0 until numKeys).forEach { index ->

            point.x = keyProperties.getProperty("key$index.x")?.toDouble()!!
            point.y = keyProperties.getProperty("key$index.y")?.toDouble()!!
            rect.w = keyProperties.getProperty("key$index.w")?.toDouble()!!
            rect.h = keyProperties.getProperty("key$index.h")?.toDouble()!!

            val newKey = createNewKey(point, rect)

            newKey.findComponent<KeyBehavior>()?.keyChar = keyProperties.getProperty("key$index.char")?.toCharArray()?.getOrNull(0)
        }

    }

    fun storeKeys() {
        val keyProperties = Properties()

        entity.findChildren<KeyEntity>().forEachIndexed { index, key ->
            val collider = key.findComponent<BoxCollider>()!!
            val boundingBox = collider.boundingBox
            val keyBehavior = key.findComponent<KeyBehavior>()!!

            keyProperties.setProperty("key$index.x", boundingBox.x.toString())
            keyProperties.setProperty("key$index.y", boundingBox.y.toString())
            keyProperties.setProperty("key$index.w", boundingBox.w.toString())
            keyProperties.setProperty("key$index.h", boundingBox.h.toString())
            if (keyBehavior.keyChar != null) {
                keyProperties.setProperty("key$index.char", keyBehavior.keyChar?.toString())
            }
        }
        keyProperties.setProperty("keys.count", entity.findChildren<KeyEntity>().size.toString())

        val path = Paths.get(keyPropertiesFile)

        Files.newOutputStream(path).use { keyProperties.store(it, keyPropertiesFile) }
    }

    fun createNewKey(worldPoint: GeoPoint, keyRect: GeoRect): KeyEntity {
        val entity = sceneFacade.createEntity(::KeyEntity, parent = entity)
        entity.origin.set(worldPoint)

        val rect = entity.findComponent<RectRenderable>()
        rect?.rect?.updateFrom(keyRect)

        return entity
    }


}

