package xx.projmap.simulation

import xx.projmap.geometry.Transformation
import xx.projmap.geometry.createQuadFromPoints
import xx.projmap.geometry.toQuad
import xx.projmap.scene.*

class CalibrationState(override val simulationManager: SimulationManager, override val scene: Scene) : SimulationState {

    private val calibrationPoints: MutableList<PointEntity> = ArrayList()
    private lateinit var calibrationCamera: Camera

    override val id: String
        get() = "calibration"

    override fun initialize() {
        calibrationCamera = scene.cameras.find { it.id == "calibration" } ?: throw IllegalArgumentException("no calibration cam found")
    }

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        calibrationPoints.clear()
        scene.hideAllCameras()
        scene.showCamera("calibration")
    }

    override fun update(dt: Double) {
        if (calibrationPoints.size == 4) {
            val srcQuad = calibrationCamera.region.toQuad()
            val dstQuad = createQuadFromPoints(calibrationPoints.map(PointEntity::origin).toTypedArray())
            val transformation = Transformation(srcQuad, dstQuad)
            simulationManager.changeState("main", transformation, calibrationCamera)
        }
    }

    override fun handleEvent(event: Event) = when (event) {
        is MouseClickEvent -> handleMouseClickEvent(event)
        else -> Unit
    }

    private fun handleMouseClickEvent(event: MouseClickEvent) {
        if (calibrationPoints.size < 4) {
            val worldPoint = calibrationCamera.viewportToWorld(event.point)
            println("screen: ${event.point}, world: $worldPoint")
            val pointEntity = PointEntity(worldPoint)
            scene.world.entities += pointEntity
            calibrationPoints += pointEntity
        }
    }

}