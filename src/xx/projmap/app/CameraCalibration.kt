package xx.projmap.app

import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.IdentityTransform
import xx.projmap.scene2.*
import java.awt.Color

class CameraCalibrationPoint : Entity("calibrationPoint") {
    init {
        origin.x = 500.0
        origin.y = 500.0
        addComponent(PointRenderable())
        addComponent(CameraCalibrationPointBehavior())
    }
}

class CameraCalibrationPointBehavior : Behavior() {

    private lateinit var renderable: Renderable

    override fun setup() {
        renderable = entity.findComponent()!!
    }

    var active: Boolean = false
        set(value) {
            field = value
            if (value) {
                renderable.color = Color.RED
            } else {
                renderable.color = Color.WHITE
            }
        }
}

class CameraCalibrationState : Entity("cameraCalibrationState") {
    init {
        addComponent(CameraCalibrationBehavior())
        addChild(CameraCalibrationPoint())
        addChild(CameraCalibrationPoint())
        addChild(CameraCalibrationPoint())
        addChild(CameraCalibrationPoint())
    }
}

class CameraCalibrationBehavior : Behavior() {

    private lateinit var cameraCalibrationPoints: Array<CameraCalibrationPoint>
    private lateinit var calibrationCamera: CameraEntity
    private lateinit var stateManager: StateManagerBehavior
    private var curPoint = 0

    override fun setup() {
        cameraCalibrationPoints = entity.findChildren<CameraCalibrationPoint>().toTypedArray()
        calibrationCamera = sceneFacade.getMainCamera()
        stateManager = sceneFacade.findEntity<StateManager>()?.findComponent()!!
        enabled = true
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        val dstPoint = cameraCalibrationPoints[curPoint].origin
        calibrationCamera.camera.viewportToCamera(event.point, dstPoint)
        cameraCalibrationPoints[curPoint].findComponent<CameraCalibrationPointBehavior>()?.active = false
        curPoint++
        if (curPoint == 4) {
            stateManager.nextState = State.KEY_CALIBRATION
        } else {
            cameraCalibrationPoints[curPoint].findComponent<CameraCalibrationPointBehavior>()?.active = true
        }
    }

    override fun onActivation() {
        entity.findChildren<CameraCalibrationPoint>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = true }
        calibrationCamera.camera.transform = IdentityTransform()
    }

    override fun onDeactivation() {
        entity.findChildren<CameraCalibrationPoint>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = false }
    }

}