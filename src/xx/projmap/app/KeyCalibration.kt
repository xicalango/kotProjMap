package xx.projmap.app

import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.*
import xx.projmap.scene2.*
import java.awt.Color
import java.util.*

class KeyEntity : Entity("key") {
    init {
        val rectRenderable = RectRenderable(MutRect(0.0, 0.0, 100.0, 100.0))
        rectRenderable.color = Color.RED
        addComponent(rectRenderable)
        addComponent(KeyBehavior())
    }
}

class KeyBehavior : Behavior() {

    var keyChar: Char? = null

}

class KeyCalibration : Entity("keyCalibration") {
    init {
        addComponent(KeyCalibrationBehavior())
    }
}

private const val CREATION_DELAY_VARIATION = 0.2
private const val MIN_CREATION_DELAY = 0.5

class KeyCalibrationBehavior : Behavior() {

    private val random = Random()

    private lateinit var camera: Camera
    private lateinit var cameraCalibration: CameraCalibrationState

    private var counter = random.nextDouble() * CREATION_DELAY_VARIATION

    private fun resetCounter() {
        counter = MIN_CREATION_DELAY + (random.nextDouble() * CREATION_DELAY_VARIATION)
    }

    override fun setup() {
        camera = sceneFacade.getMainCamera().camera
        cameraCalibration = sceneFacade.findEntity<StateManager>()?.findChild()!!
        enabled = false
    }

    override fun onActivation() {
        val calibrationPoints = cameraCalibration.findChildren<CameraCalibrationPoint>().map { it.origin }
        updateTransform(calibrationPoints)
        entity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = true }
    }

    override fun onDeactivation() {
        entity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = false }
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

    private fun updateTransform(calibrationPoints: List<MutPoint>) {
        assert(calibrationPoints.size == 4)
        val srcQuad = camera.region.toQuad()
        val dstQuad = createQuadFromPoints(calibrationPoints.toTypedArray())
        val transformation = Transformation(srcQuad, dstQuad)
        camera.transform = transformation
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        val entity = sceneFacade.createEntity(::KeyEntity, parent = entity)
        entity.findComponent<Renderable>()?.color = Color(random.nextInt())
        camera.viewportToWorld(event.point, entity.origin)
    }


}