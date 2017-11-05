package xx.projmap.simulation

import xx.projmap.geometry.Transform
import xx.projmap.scene.Camera
import xx.projmap.scene.Event
import xx.projmap.scene.Scene

class MainState(override val simulationManager: SimulationManager, override val scene: Scene) : SimulationState {

    private lateinit var calibrationCamera: Camera
    private lateinit var transformCamera: Camera

    override val id: String
        get() = "main"

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        assert(previousState.id == "calibration")
        parameters.forEachIndexed { index, any -> println("$index: $any") }
        val transform = parameters.getOrNull(0) as Transform? ?: throw IllegalArgumentException("need transform")
        calibrationCamera = parameters.getOrNull(1) as Camera? ?: throw IllegalArgumentException("need camera")

        scene.cameras.removeIf { it.id == "transform" }
        transformCamera = Camera(calibrationCamera.region, calibrationCamera.viewport, transform, "transform")
        scene.cameras += transformCamera
    }

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) {
    }


}