package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.*
import xx.projmap.scene2.*
import java.awt.Color

class TextLineEntity(origin: GeoPoint) : Entity("text", origin) {
    init {
        val textRenderable = TextRenderable()
        textRenderable.setSpacing(4.0)
        addComponent(textRenderable)
    }
}

class KeyCalibration : Entity("keyCalibration") {
    init {
        addChild(TextLineEntity(Point(0.0, -300.0)))
        addChild(TextLineEntity(Point(0.0, -275.0)))
        addChild(TextLineEntity(Point(0.0, -250.0)))
        addChild(TextLineEntity(Point(0.0, -225.0)))
        addChild(TextLineEntity(Point(0.0, -200.0)))
        addChild(TextLineEntity(Point(0.0, -175.0)))
        addComponent(KeyCalibrationBehavior())
    }
}

private enum class KeyEnterMode {
    COMMAND,
    KEY_CHAR_ENTER,
    FIND_KEY
}

class KeyCalibrationBehavior : Behavior() {

    private lateinit var camera: Camera
    private lateinit var cameraCalibration: CameraCalibrationState
    private lateinit var stateManager: StateManagerBehavior
    private lateinit var keyboardEntity: KeyboardEntity
    private lateinit var keyboardBehavior: KeyboardBehavior

    private lateinit var textLines: List<TextLineEntity>

    private val keyRect = MutRect()

    private var currentKey: KeyEntity? = null

    private var keyEnterMode = KeyEnterMode.COMMAND


    override fun initialize() {
        loadConfig()

        textLines = entity.findChildren<TextLineEntity>().sortedBy { it.origin.y }
    }

    override fun setup() {
        camera = sceneFacade.getMainCamera().camera
        val stateManagerEntity = sceneFacade.findEntity<StateManager>()!!
        stateManager = stateManagerEntity.findComponent()!!
        cameraCalibration = stateManagerEntity.findChild()!!

        keyboardEntity = sceneFacade.findEntity()!!
        keyboardBehavior = keyboardEntity.findComponent()!!
        enabled = false
    }

    private fun loadConfig() {
        keyRect.w = config.getProperty("key.defaultWidth", "13").toDouble()
        keyRect.h = config.getProperty("key.defaultHeight", "13").toDouble()
    }

    private fun storeConfig() {
        config.setProperty("key.defaultWidth", keyRect.w.toString())
        config.setProperty("key.defaultHeight", keyRect.h.toString())
    }

    override fun onActivation() {
        val calibrationPoints = cameraCalibration.findChildren<CameraCalibrationPoint>().map { it.origin }
        updateTransform(calibrationPoints)
        keyboardEntity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach {
            it.color = Color.WHITE
            it.enabled = true
        }
        textLines.flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = true }
    }

    private fun updateTransform(calibrationPoints: List<MutPoint>) {
        assert(calibrationPoints.size == 4)
        val dstQuad = createQuadFromPoints(calibrationPoints.toTypedArray())
        val transformation = Transformation(keyboardBehavior.keyboardQuad, dstQuad)
        camera.transform = transformation
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        val worldPoint = camera.viewportToWorld(event.point)

        val keyEntity = keyboardEntity.findChildren<KeyEntity>()
                .find { it.findComponent<BoxCollider>()?.collidesWith(worldPoint)!! }
                ?: keyboardBehavior.createNewKey(worldPoint, keyRect)

        selectKey(keyEntity)
    }

    private fun selectKey(keyEntity: KeyEntity) {
        currentKey?.findComponent<ActiveColorChanger>()?.active = false

        currentKey = keyEntity

        currentKey?.findComponent<ActiveColorChanger>()?.active = true

        updateText()
    }

    private fun updateText() {
        currentKey.let { key ->
            if (key == null) {
                return
            }

            val keyRect = key.findComponent<RectRenderable>()?.rect
            val keyBehavior = key.findComponent<KeyBehavior>()

            textLines[0].findComponent<TextRenderable>()?.text = "x: ${key.origin.x}"
            textLines[1].findComponent<TextRenderable>()?.text = "y: ${key.origin.y}"
            textLines[2].findComponent<TextRenderable>()?.text = "w: ${keyRect?.w}"
            textLines[3].findComponent<TextRenderable>()?.text = "h: ${keyRect?.h}"
            textLines[4].findComponent<TextRenderable>()?.text = "key: ${keyBehavior?.keyChar ?: ' '} / ${keyBehavior?.keyCode ?: -1}"
        }
        textLines[5].findComponent<TextRenderable>()?.text = "mode: $keyEnterMode"
    }

    override fun onKeyReleased(event: KeyEvent) {
        when (keyEnterMode) {
            KeyEnterMode.COMMAND -> handleCommand(event)
            KeyEnterMode.KEY_CHAR_ENTER -> handleKeyCharInput(event)
            KeyEnterMode.FIND_KEY -> findKey(event)
        }

        if (event.keyCode == 27) {
            keyEnterMode = KeyEnterMode.COMMAND
            updateText()
            return
        }
    }

    private fun findKey(event: KeyEvent) {
        val key = keyboardBehavior.findEntityByEvent(event)
        if (key != null) {
            selectKey(key)
            updateText()
        }
    }

    private fun handleKeyCharInput(event: KeyEvent) {
        currentKey.let { key ->
            if (key == null) {
                return
            }

            val keyBehavior = key.findComponent<KeyBehavior>()
            keyBehavior?.keyChar = event.keyChar
            keyBehavior?.keyCode = event.keyCode
            updateText()
        }
    }

    private fun handleCommand(event: KeyEvent) {
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
            't' -> {
                keyEnterMode = KeyEnterMode.KEY_CHAR_ENTER
                updateText()
            }
            'f' -> {
                keyEnterMode = KeyEnterMode.FIND_KEY
                updateText()
            }
            'p' -> {
                storeConfig()
                keyboardBehavior.storeKeys()
            }
            ' ' -> {
                duplicateKey()
            }
        }
    }

    private fun duplicateKey() {
        val key = currentKey

        if (key != null) {
            val newKey = keyboardBehavior.createNewKey(key.origin, key.findComponent<RectRenderable>()?.rect!!)
            selectKey(newKey)
        }
    }

    private fun removeKey() {
        val currentKey = this.currentKey
        if (currentKey != null) {
            currentKey.destroy = true
            keyboardEntity.findChild<KeyEntity>().let {
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
        updateText()
    }

    private fun scaleKey(dw: Double = 0.0, dh: Double = 0.0) {
        currentKey?.findComponent<RectRenderable>()?.rect?.resize(dw, dh)
        keyRect.resize(dw, dh)
        updateText()
    }


}