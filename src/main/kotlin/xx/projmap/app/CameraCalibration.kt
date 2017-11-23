package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseButton
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.Transformation
import xx.projmap.geometry.createQuadFromPoints
import xx.projmap.geometry.toPointArray
import xx.projmap.scene2.*

class CameraCalibrationPoint : Entity("calibrationPoint") {

    val activeColorChanger = ActiveColorChanger()

    init {
        addComponent(PointRenderable(ignoreTransform = true))
        addComponent(activeColorChanger)
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
    private lateinit var camera: Camera
    private lateinit var stateManager: StateManagerBehavior
    private lateinit var keyboardBehavior: KeyboardBehavior
    private lateinit var appConfig: AppConfig

    private var curPoint = 0

    override fun initialize() {
        cameraCalibrationPoints = entity.findChildren<CameraCalibrationPoint>().toTypedArray()
    }

    override fun setup() {
        appConfig = scene.findEntity<AppConfigEntity>()?.config!!
        camera = scene.getMainCamera().camera
        stateManager = scene.findEntity<StateManager>()?.findComponent()!!
        keyboardBehavior = scene.findEntity<KeyboardEntity>()?.keyboardBehavior!!

        loadCalibration()
        updateTransformation()
        enabled = true
    }

    override fun onMouseClicked(event: MouseClickEvent) {
        if (event.button !in setOf(MouseButton.LEFT, MouseButton.RIGHT)) {
            return
        }

        val dstPoint = cameraCalibrationPoints[curPoint].origin
        camera.viewportToCamera(event.point, dstPoint)
        updateTransformation()

        if (event.button == MouseButton.LEFT) {
            selectNextKey()
        }
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
            'w' -> moveCurrentPoint(dy = -1.0)
            's' -> moveCurrentPoint(dy = +1.0)
            'a' -> moveCurrentPoint(dx = -1.0)
            'd' -> moveCurrentPoint(dx = +1.0)
            'W' -> moveCurrentPoint(dy = -10.0)
            'S' -> moveCurrentPoint(dy = +10.0)
            'A' -> moveCurrentPoint(dx = -10.0)
            'D' -> moveCurrentPoint(dx = +10.0)
        }
    }

    private fun moveCurrentPoint(dx: Double = 0.0, dy: Double = 0.0) {
        cameraCalibrationPoints[curPoint].origin.move(dx, dy)
        updateTransformation()
    }

    private fun updateTransformation() {
        val calibrationPoints = cameraCalibrationPoints.map { it.origin }
        assert(calibrationPoints.size == 4)
        val dstQuad = createQuadFromPoints(calibrationPoints.toTypedArray())
        val transformation = Transformation(keyboardBehavior.keyboardQuad, dstQuad)
        camera.transform = transformation
    }

    private fun loadCalibration() {
        val renderRegion = camera.region.copy()
        renderRegion.scale(.9)
        renderRegion.move(10.0, 10.0)
        val defaultCalibrationPoints = renderRegion.toPointArray()

        (0 until 4).forEach { pointIndex ->
            val point = appConfig.calibrationPoints.getOrElse(pointIndex, defaultCalibrationPoints::get)
            cameraCalibrationPoints[pointIndex].origin.set(point)
        }
    }

    private fun storeCalibration() {
        cameraCalibrationPoints.forEachIndexed { index, cameraCalibrationPoint ->
            appConfig.calibrationPoints[index].set(cameraCalibrationPoint.origin)
        }
    }

    override fun onActivation() {
        curPoint = 0
        getCurrentCalibrationPoint()?.active = true
        cameraCalibrationPoints
                .flatMap { it.findComponents<Renderable>() }
                .forEach { it.enabled = true }

        scene.findEntity<KeyboardEntity>()
                ?.findChildren<KeyEntity>()
                ?.flatMap { it.findComponents<Renderable>() }
                ?.forEach { it.enabled = true }
    }

    override fun onDeactivation() {
        cameraCalibrationPoints.flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = false }
        cameraCalibrationPoints.forEach { it.activeColorChanger.active = false }
    }

}