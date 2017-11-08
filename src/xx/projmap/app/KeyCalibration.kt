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

class KeyCalibrationBehavior : Behavior() {

    private lateinit var camera: CameraEntity
    private lateinit var cameraCalibration: CameraCalibrationState

    override fun setup() {
        camera = sceneFacade.getMainCamera()
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

    private fun updateTransform(calibrationPoints: List<MutPoint>) {
        assert(calibrationPoints.size == 4)
        val srcQuad = camera.camera.region.toQuad()
        val dstQuad = createQuadFromPoints(calibrationPoints.toTypedArray())
        val transformation = Transformation(srcQuad, dstQuad)
        camera.camera.transform = transformation
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        val entity = sceneFacade.createEntity(::KeyEntity, parent = entity)
        entity.findComponent<Renderable>()?.color = Color(Random().nextInt())
        camera.camera.viewportToWorld(event.point, entity.origin)
    }


}