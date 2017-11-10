package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.*
import xx.projmap.scene2.*
import java.awt.Color
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

class KeyCalibration : Entity("keyCalibration") {
    init {
        addComponent(KeyCalibrationBehavior())
        addComponent(RandomRectAddBehavior())
    }
}

private const val CREATION_DELAY_VARIATION = 0.2
private const val MIN_CREATION_DELAY = 0.5

class RandomRectAddBehavior : Behavior() {
    private val random = Random()

    private lateinit var camera: Camera

    private var counter = random.nextDouble() * CREATION_DELAY_VARIATION

    private fun resetCounter() {
        counter = MIN_CREATION_DELAY + (random.nextDouble() * CREATION_DELAY_VARIATION)
    }

    override fun setup() {
        camera = sceneFacade.getMainCamera().camera
        enabled = false
    }

    override fun update(dt: Double) {
        counter -= dt
        if (counter <= 0) {
            val point = random.randomPointIn(camera.renderRegion).toMutable()
            camera.viewportToWorld(point, point)
            val entity = sceneFacade.createEntity(::KeyEntity, parent = entity)
            entity.findComponent<Renderable>()?.color = Color(random.nextInt())
            entity.origin.set(point)
            resetCounter()
        }
    }
}

class KeyCalibrationBehavior : Behavior() {

    private lateinit var camera: Camera
    private lateinit var cameraCalibration: CameraCalibrationState
    private lateinit var stateManager: StateManagerBehavior

    private var keyPropertiesFile = "keyboard.properties"

    private val keyboardRect = MutRect()
    private val keyRect = MutRect()

    private var currentKey: KeyEntity? = null


    override fun initialize() {
        loadConfig()
        loadKeys()
    }

    override fun setup() {
        camera = sceneFacade.getMainCamera().camera
        val stateManagerEntity = sceneFacade.findEntity<StateManager>()!!
        stateManager = stateManagerEntity.findComponent()!!
        cameraCalibration = stateManagerEntity.findChild()!!
        enabled = false
    }

    private fun loadConfig() {
        keyboardRect.w = sceneFacade.config.getProperty("keyboard.width", "460").toDouble()
        keyboardRect.h = sceneFacade.config.getProperty("keyboard.height", "170").toDouble()

        keyRect.w = config.getProperty("key.defaultWidth", "13").toDouble()
        keyRect.h = config.getProperty("key.defaultHeight", "13").toDouble()

        keyPropertiesFile = config.getProperty("keyboard.properties.file", "keyboard.properties")
    }

    private fun storeConfig() {
        config.setProperty("key.defaultWidth", keyRect.w.toString())
        config.setProperty("key.defaultHeight", keyRect.h.toString())
    }

    override fun onActivation() {
        val calibrationPoints = cameraCalibration.findChildren<CameraCalibrationPoint>().map { it.origin }
        updateTransform(calibrationPoints)
        entity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = true }
    }

    override fun onDeactivation() {
        entity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = false }
    }

    private fun updateTransform(calibrationPoints: List<MutPoint>) {
        assert(calibrationPoints.size == 4)
        val dstQuad = createQuadFromPoints(calibrationPoints.toTypedArray())
        val transformation = Transformation(keyboardRect.toQuad(), dstQuad)
        camera.transform = transformation
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        val worldPoint = camera.viewportToWorld(event.point)

        val keyEntity = entity.findChildren<KeyEntity>().find { it.findComponent<BoxCollider>()?.collidesWith(worldPoint)!! }

        if (keyEntity != null) {
            selectKey(keyEntity)
        } else {
            createNewKey(worldPoint)
        }
    }

    private fun createNewKey(worldPoint: GeoPoint): KeyEntity {
        val entity = sceneFacade.createEntity(::KeyEntity, parent = entity)
        entity.origin.set(worldPoint)

        val rect = entity.findComponent<RectRenderable>()
        rect?.rect?.updateFrom(keyRect)

        selectKey(keyEntity = entity)
        return entity
    }

    private fun selectKey(keyEntity: KeyEntity) {
        currentKey?.findComponent<ActiveColorChanger>()?.active = false

        currentKey = keyEntity

        currentKey?.findComponent<ActiveColorChanger>()?.active = true
    }

    override fun onKeyReleased(event: KeyEvent) {
        when (event.keyChar) {
            'c' -> stateManager.nextState = State.CAMERA_CALIBRATION
            'w' -> moveKey(dy = -1.0)
            's' -> moveKey(dy = 1.0)
            'a' -> moveKey(dx = -1.0)
            'd' -> moveKey(dx = 1.0)
            'W' -> moveKey(dy = -10.0)
            'S' -> moveKey(dy = 10.0)
            'A' -> moveKey(dx = -10.0)
            'D' -> moveKey(dx = 10.0)
            'u' -> scaleKey(dh = -1.0)
            'j' -> scaleKey(dh = 1.0)
            'h' -> scaleKey(dw = -1.0)
            'k' -> scaleKey(dw = 1.0)
            'U' -> scaleKey(dh = -10.0)
            'J' -> scaleKey(dh = 10.0)
            'H' -> scaleKey(dw = -10.0)
            'K' -> scaleKey(dw = 10.0)
            'r' -> removeKey()
            'p' -> {
                storeConfig()
                storeKeys()
            }
        }
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

        (0 until numKeys).forEach { index ->

            point.x = keyProperties.getProperty("key$index.x")?.toDouble()!!
            point.y = keyProperties.getProperty("key$index.y")?.toDouble()!!

            val newKey = createNewKey(point)
            val rect = newKey.findComponent<RectRenderable>()?.rect!!

            rect.w = keyProperties.getProperty("key$index.w")?.toDouble()!!
            rect.h = keyProperties.getProperty("key$index.h")?.toDouble()!!

            newKey.findComponent<KeyBehavior>()?.keyChar = keyProperties.getProperty("key$index.char")?.toCharArray()?.getOrNull(0)
        }

    }

    private fun storeKeys() {
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

    private fun removeKey() {
        val currentKey = this.currentKey
        if (currentKey != null) {
            entity.removeChild(currentKey)
            entity.findChild<KeyEntity>().let {
                if (it != null) {
                    selectKey(it)
                } else {
                    this.currentKey = null
                }
            }
        }
    }

    private fun moveKey(dx: Double = 0.0, dy: Double = 0.0) {
        currentKey?.origin?.move(dx, dy)
    }

    private fun scaleKey(dw: Double = 0.0, dh: Double = 0.0) {
        currentKey?.findComponent<RectRenderable>()?.rect?.resize(dw, dh)
        keyRect.resize(dw, dh)
    }


}