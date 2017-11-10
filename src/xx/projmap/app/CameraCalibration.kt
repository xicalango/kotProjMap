package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseButton
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.IdentityTransform
import xx.projmap.scene2.*
import java.awt.Color

class CameraCalibrationPoint : Entity("calibrationPoint") {
    init {
        origin.x = 500.0
        origin.y = 500.0
        addComponent(PointRenderable())
        addComponent(ActiveColorChanger())
    }
}

class ActiveColorChanger(private val activeColor: Color = Color.RED, private val inactiveColor: Color = Color.WHITE) : Behavior() {

    private lateinit var renderable: Renderable

    var active: Boolean = false
        set(value) {
            field = value
            if (value) {
                renderable.color = activeColor
            } else {
                renderable.color = inactiveColor
            }
        }

    override fun initialize() {
        renderable = entity.findComponent()!!

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

    override fun initialize() {
        cameraCalibrationPoints = entity.findChildren<CameraCalibrationPoint>().toTypedArray()
    }

    override fun setup() {
        calibrationCamera = sceneFacade.getMainCamera()
        stateManager = sceneFacade.findEntity<StateManager>()?.findComponent()!!
        loadCalibration()
        enabled = true
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        if (event.button != MouseButton.LEFT) {
            return
        }

        val dstPoint = cameraCalibrationPoints[curPoint].origin
        calibrationCamera.camera.viewportToCamera(event.point, dstPoint)
        selectNextKey()
    }

    private fun selectNextKey() {
        getCurrentCalibrationPoint()?.active = false
        curPoint++
        if (curPoint == 4) {
            stateManager.nextState = State.KEY_CALIBRATION
        } else {
            getCurrentCalibrationPoint()?.active = true
        }
    }

    private fun getCurrentCalibrationPoint() = cameraCalibrationPoints[curPoint].findComponent<ActiveColorChanger>()

    override fun onKeyReleased(event: KeyEvent) {
        when (event.keyChar) {
            ' ' -> selectNextKey()
            'p' -> storeCalibration()
        }
    }

    private fun loadCalibration() {
        (0 until 4).forEach { pointIndex ->
            val loadedX = sceneFacade.config.getProperty("calibration.point$pointIndex.x")?.toDouble()
            val loadedY = sceneFacade.config.getProperty("calibration.point$pointIndex.y")?.toDouble()

            if (loadedX != null && loadedY != null) {
                val origin = cameraCalibrationPoints[pointIndex].origin
                origin.x = loadedX
                origin.y = loadedY
            }
        }
    }

    private fun storeCalibration() {
        (0 until 4).forEach { pointIndex ->
            val origin = cameraCalibrationPoints[pointIndex].origin
            sceneFacade.config.setProperty("calibration.point$pointIndex.x", origin.x.toString())
            sceneFacade.config.setProperty("calibration.point$pointIndex.y", origin.y.toString())
        }
    }

    override fun onActivation() {
        curPoint = 0
        getCurrentCalibrationPoint()?.active = true
        entity.findChildren<CameraCalibrationPoint>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = true }
        sceneFacade.findEntity<KeyboardEntity>()?.findChildren<KeyEntity>()?.flatMap { it.findComponents<Renderable>() }?.forEach { it.enabled = false }
        calibrationCamera.camera.transform = IdentityTransform()
    }

    override fun onDeactivation() {
        entity.findChildren<CameraCalibrationPoint>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = false }
    }

}