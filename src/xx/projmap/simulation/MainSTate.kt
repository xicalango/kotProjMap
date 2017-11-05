package xx.projmap.simulation

import xx.projmap.geometry.Transform
import xx.projmap.scene.*

class MainState(simulationManager: SimulationManager, scene: Scene) : SimulationState(simulationManager, scene) {

    private lateinit var calibrationCamera: Camera
    private lateinit var transformCamera: Camera
    private lateinit var debugCamera: Camera

    override val id: String
        get() = "main"

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        assert(previousState.id == "calibration")
        parameters.forEachIndexed { index, any -> println("$index: $any") }
        val transform = parameters.getOrNull(0) as Transform? ?: throw IllegalArgumentException("need transform")
        calibrationCamera = parameters.getOrNull(1) as Camera? ?: throw IllegalArgumentException("need camera")

        scene.cameras.removeIf { it.id == "transform" || it.id == "debug" }
        transformCamera = Camera(calibrationCamera.region, simulationManager.mainViewport, transform, id = "transform")

        val debugViewport = simulationManager.viewports["debug"]

        if (debugViewport != null) {
            debugCamera = Camera(calibrationCamera.region, debugViewport, id = "debug")
        }

        scene.cameras += transformCamera
        scene.cameras += debugCamera
    }

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) = when (event) {
        is KeyEvent -> handleKeyEvent(event)
        else -> Unit
    }

    private fun handleKeyEvent(event: KeyEvent) {
        if (event.direction == Direction.PRESSED) {
            return
        }
        when (event.keyChar) {
            '2' -> debugCamera.visible = !debugCamera.visible
            'c' -> simulationManager.changeState("calibration")
        }
    }


}