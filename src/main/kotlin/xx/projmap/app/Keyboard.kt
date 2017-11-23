package xx.projmap.app

import okio.Okio
import xx.projmap.events.KeyEvent
import xx.projmap.geometry.*
import xx.projmap.graphics.DrawStyle
import xx.projmap.moshi
import xx.projmap.scene2.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

data class KeyResource(val x: Double, val y: Double, val w: Double, val h: Double, val char: Char? = null, val code: Int? = null) {
    fun compareToWithDelta(key: KeyResource, delta: Double): Int {
        val dX = Math.abs(x - key.x)
        val dY = Math.abs(y - key.y)
        return if (dY <= delta) {
            if (dX <= delta) {
                0
            } else {
                x.compareTo(key.x)
            }
        } else {
            y.compareTo(key.y)
        }
    }

}

data class KeyboardResource(val width: Double, val height: Double, val keys: List<KeyResource>)

val keyboardResourceAdapter = moshi.adapter(KeyboardResource::class.java).indent("  ")

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
    private lateinit var keyboardFilename: String
    private lateinit var appConfig: AppConfig

    val keyboardRect: GeoRect
        get() = _keyboardRect

    val keyboardQuad: GeoQuad
        get() = _keyboardRect.toQuad()

    override fun initialize() {
    }

    override fun setup() {
        appConfig = scene.findEntity<AppConfigEntity>()?.config!!
        keyboardFilename = appConfig.keyboardFile
        loadKeys()
    }

    private fun toKeyboardResource(): KeyboardResource {
        val keys = entity.findChildren<KeyEntity>()
                .map { key ->
                    val boundingBox = key.findComponent<BoxCollider>()?.boundingBox!!
                    val keyBehavior = key.findComponent<KeyBehavior>()!!

                    KeyResource(boundingBox.x, boundingBox.y, boundingBox.w, boundingBox.h, keyBehavior.keyChar, keyBehavior.keyCode)
                }
                .sortedWith(Comparator { k1, k2 -> -k1.compareToWithDelta(k2, 5.0) })

        return KeyboardResource(_keyboardRect.w, _keyboardRect.h, keys)
    }

    private fun loadFromKeyboardResource(keyboardResource: KeyboardResource) {
        entity.findChildren<KeyEntity>().forEach { it.destroy = true }

        _keyboardRect.w = keyboardResource.width
        _keyboardRect.h = keyboardResource.height

        val point = MutPoint()
        val rect = MutRect()

        keyboardResource.keys.forEach { key ->
            point.x = key.x
            point.y = key.y
            rect.w = key.w
            rect.h = key.h

            val newKey = createNewKey(point, rect)

            val keyBehavior = newKey.findComponent<KeyBehavior>()
            keyBehavior?.keyChar = key.char
            keyBehavior?.keyCode = key.code
        }
    }

    private fun loadKeys() {
        val path = Paths.get(keyboardFilename)
        if (!Files.exists(path)) {
            return
        }

        Okio.buffer(Okio.source(path))
                .use(keyboardResourceAdapter::fromJson)
                ?.let(this::loadFromKeyboardResource)
    }

    fun storeKeys() {
        val keyboardResource = toKeyboardResource()

        val path = Paths.get(keyboardFilename)

        Okio.buffer(Okio.sink(path)).use { sink ->
            keyboardResourceAdapter.toJson(sink, keyboardResource)
        }
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

