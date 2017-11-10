package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.*
import xx.projmap.scene2.*
import java.awt.Color
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

    private val keyboardRect = MutRect()
    private val keyRect = MutRect()

    private var currentKey: KeyEntity? = null

    override fun initialize() {
        loadConfig()
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

        return if (keyEntity != null) {
            selectEntity(keyEntity)
        } else {
            createNewKey(worldPoint)
        }
    }

    private fun createNewKey(worldPoint: MutPoint) {
        val entity = sceneFacade.createEntity(::KeyEntity, parent = entity)
        entity.origin.set(worldPoint)

        val rect = entity.findComponent<RectRenderable>()
        rect?.rect?.updateFrom(keyRect)

        selectEntity(keyEntity = entity)
    }

    private fun selectEntity(keyEntity: KeyEntity) {
        currentKey?.findComponent<ActiveColorChanger>()?.active = false

        currentKey = keyEntity

        currentKey?.findComponent<ActiveColorChanger>()?.active = true
    }

    override fun onKeyReleased(event: KeyEvent) {
        when (event.keyChar) {
            'c' -> stateManager.nextState = State.CAMERA_CALIBRATION
        }
    }

}