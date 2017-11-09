package xx.projmap.simulation.impl

import xx.projmap.events.Direction
import xx.projmap.events.Event
import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.*
import xx.projmap.scene.Camera
import xx.projmap.scene.PointEntity
import xx.projmap.scene.Scene
import xx.projmap.simulation.api.Script
import xx.projmap.simulation.api.SimulationState
import xx.projmap.simulation.api.SimulationStateManager
import xx.projmap.storeProperties
import java.awt.Color
import java.nio.file.Paths

class CalibrationState(simulationStateManager: SimulationStateManager, scene: Scene, private val calibrationPoints: Array<Point>? = null) : SimulationState(simulationStateManager, scene) {

    private lateinit var calibrationScript: CalibrationPointsScript
    private lateinit var calibrationCamera: Camera

    override val id: String
        get() = "calibration"

    override fun initialize() {
        val viewport = simulationStateManager.mainViewport
        calibrationCamera = Camera(viewport.region.toNormalized(), simulationStateManager.mainViewport, id = "calibration")
        scene.cameras += calibrationCamera

        calibrationScript = CalibrationPointsScript(calibrationCamera, calibrationPoints)

        scene.world.entities += calibrationScript.calibrationPoints

        scripts += ZoomHandler(calibrationCamera)
        scripts += CameraTranslationHandler(calibrationCamera)
        scripts += calibrationScript
    }

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        calibrationScript.reset()
        calibrationScript.showCalibrationPoints()
        scene.hideAllCameras()
        scene.showCamera("calibration")
    }

    override fun onDeactivation() {
        calibrationScript.hideCalibrationPoints()
    }

    override fun update(dt: Double) {
        if (calibrationScript.curPoint == 4) {
            simulationStateManager.changeState("keyEditing", calibrationScript.createTransformation(), calibrationCamera.region)
        }
    }

    override fun handleEvent(event: Event) {
        if (event is KeyEvent) {
            if (event.direction == Direction.RELEASED && event.keyChar == 'p') {
                persistCalibration()
            }
        }
    }

    private fun persistCalibration() {
        storeProperties(Paths.get("calibrationPoints.properties"), {
            calibrationScript.calibrationPoints.forEachIndexed { index, point ->
                setProperty("point" + index, "${point.origin.x},${point.origin.y}")
            }
        })
    }

}

private class CalibrationPointsScript(private val camera: Camera, calibrationPoints: Array<Point>? = null) : Script {

    val calibrationPoints: Array<PointEntity>
    var curPoint = 0
        private set

    init {
        val pointArray = camera.region.toPointArray()
        if (calibrationPoints != null) {
            assert(calibrationPoints.size == 4)
            this.calibrationPoints = calibrationPoints.map { PointEntity(origin = it.toMutable(), tag = "calibrationPoint") }.toTypedArray()
        } else {
            this.calibrationPoints = Array(4, { PointEntity(origin = pointArray[it].toMutable(), tag = "calibrationPoint") })
        }
    }

    fun reset() {
        curPoint = 0
        calibrationPoints[0].color = Color.RED
    }

    fun showCalibrationPoints() {
        calibrationPoints.forEach { it.visible = true }
    }

    fun hideCalibrationPoints() {
        calibrationPoints.forEach { it.visible = false }
    }

    fun createTransformation(): Transformation {
        assert(curPoint == 4)
        val srcQuad = camera.region.toQuad()
        val dstQuad = createQuadFromPoints(calibrationPoints.map(PointEntity::origin).toTypedArray())
        return Transformation(srcQuad, dstQuad)
    }

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is MouseClickEvent -> handleMouseClickEvent(event)
            is KeyEvent -> handleKeyEvent(event)
        }
    }

    private fun handleKeyEvent(event: KeyEvent) {
        if (event.keyChar == ' ' && event.direction == Direction.RELEASED) {
            selectNextPoint()
        }
    }

    private fun handleMouseClickEvent(event: MouseClickEvent) {
        if (curPoint < 4) {
            val worldPoint = camera.viewportToWorld(event.point)
            calibrationPoints[curPoint].origin.set(worldPoint)
            selectNextPoint()
        }
    }

    private fun selectNextPoint() {
        calibrationPoints[curPoint].color = Color.WHITE
        curPoint++
        if (curPoint < 4) {
            calibrationPoints[curPoint].visible = true
            calibrationPoints[curPoint].color = Color.RED
        }
    }
}


