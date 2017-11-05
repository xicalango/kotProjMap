package xx.projmap.simulation

import xx.projmap.geometry.Transformation
import xx.projmap.geometry.createQuadFromPoints
import xx.projmap.geometry.toQuad
import xx.projmap.scene.*
import java.awt.Color

class CalibrationState(simulationManager: SimulationManager, scene: Scene) : SimulationState(simulationManager, scene) {

    private lateinit var calibrationScript: CalibrationPointsScript
    private lateinit var calibrationCamera: Camera

    override val id: String
        get() = "calibration"

    override fun initialize() {
        val viewport = simulationManager.mainViewport
        calibrationCamera = Camera(viewport.region, simulationManager.mainViewport, id = "calibration")
        scene.cameras += calibrationCamera

        calibrationScript = CalibrationPointsScript(calibrationCamera)

        scene.world.entities += calibrationScript.calibrationPoints

        scripts += ZoomHandler(calibrationCamera)
        scripts += CameraTranslationScript(calibrationCamera)
        scripts += calibrationScript
    }

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        calibrationScript.reset()
        scene.hideAllCameras()
        scene.showCamera("calibration")
    }

    override fun onDeactivation() {
        calibrationScript.hideCalibrationPoints()
    }

    override fun update(dt: Double) {
        if (calibrationScript.curPoint == 4) {
            simulationManager.changeState("main", calibrationScript.createTransformation(), calibrationCamera)
        }
    }

    override fun handleEvent(event: Event) {
    }

}

class CalibrationPointsScript(private val camera: Camera) : Script {

    val calibrationPoints: Array<PointEntity> = Array(4, { PointEntity() })
    var curPoint = 0
        private set

    fun reset() {
        curPoint = 0
        calibrationPoints[0].color = Color.RED
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
            calibrationPoints[curPoint].origin.updateFrom(worldPoint)
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


