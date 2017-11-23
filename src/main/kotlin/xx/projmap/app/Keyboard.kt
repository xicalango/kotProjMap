package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.geometry.*
import xx.projmap.graphics.DrawStyle
import xx.projmap.scene2.Behavior
import xx.projmap.scene2.BoxCollider
import xx.projmap.scene2.Entity
import xx.projmap.scene2.RectRenderable
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


class KeyEntity : Entity("key") {
    val keyBehavior = KeyBehavior()

    val rectRenderable = RectRenderable(MutRect(0.0, 0.0, 10.0, 10.0))

    init {
        addComponent(rectRenderable)
        rectRenderable.drawStyle = DrawStyle.LINE
        addComponent(keyBehavior)
        addComponent(BoxCollider(rectRenderable))
        addComponent(ActiveColorChanger())
    }
}

class KeyBehavior : Behavior() {

    var keyChar: Char? = null
    var keyCode: Int? = null

}

class KeyboardEntity : Entity("keyboard") {

    val keyboardBehavior = KeyboardBehavior()

    init {
        addComponent(keyboardBehavior)
    }

}

class KeyboardBehavior : Behavior() {

    private val _keyboardRect: MutRect = MutRect()
    private lateinit var keyPropertiesFile: String

    val keyboardRect: GeoRect
        get() = _keyboardRect

    val keyboardQuad: GeoQuad
        get() = _keyboardRect.toQuad()

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

        _keyboardRect.w = keyProperties.getProperty("keyboard.width", "460").toDouble()
        _keyboardRect.h = keyProperties.getProperty("keyboard.height", "170").toDouble()

        val numKeys = keyProperties.getProperty("keys.count", "0").toInt()

        val point = MutPoint()
        val rect = MutRect()

        (0 until numKeys).forEach { index ->

            point.x = keyProperties.getProperty("key$index.x")?.toDouble()!!
            point.y = keyProperties.getProperty("key$index.y")?.toDouble()!!
            rect.w = keyProperties.getProperty("key$index.w")?.toDouble()!!
            rect.h = keyProperties.getProperty("key$index.h")?.toDouble()!!

            val newKey = createNewKey(point, rect)

            val keyBehavior = newKey.findComponent<KeyBehavior>()
            keyBehavior?.keyChar = keyProperties.getProperty("key$index.char")?.toCharArray()?.getOrNull(0)
            keyBehavior?.keyCode = keyProperties.getProperty("key$index.code")?.toInt()
        }

    }

    fun storeKeys() {
        val keyProperties = Properties()

        val children = entity.findChildren<KeyEntity>()

        keyProperties.setProperty("keyboard.width", _keyboardRect.w.toString())
        keyProperties.setProperty("keyboard.height", _keyboardRect.h.toString())

        keyProperties.setProperty("keys.count", children.size.toString())
        children.sortedWith(Comparator { k1, k2 -> -k1.origin.compareToWithDelta(k2.origin, 5.0) })
                .forEachIndexed { index, key ->
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
                    if (keyBehavior.keyCode != null) {
                        keyProperties.setProperty("key$index.code", keyBehavior.keyCode?.toString())
                    }
                }

        val path = Paths.get(keyPropertiesFile)

        Files.newOutputStream(path).use { keyProperties.store(it, keyPropertiesFile) }
    }

    fun createNewKey(worldPoint: GeoPoint, keyRect: GeoRect): KeyEntity {
        val entity = scene.createEntity(::KeyEntity, parent = entity)
        entity.origin.set(worldPoint)

        val rect = entity.findComponent<RectRenderable>()
        rect?.rect?.updateFrom(keyRect)

        return entity
    }

    fun findKeyByEvent(event: KeyEvent) = entity.findChildren<KeyEntity>().find {
        val keyBehavior = it.findComponent<KeyBehavior>()
        keyBehavior?.keyCode == event.keyCode
    }

}
