package xx.projmap.simulation.impl

import xx.projmap.geometry.Transform
import xx.projmap.scene.*
import xx.projmap.simulation.api.SimulationManager
import xx.projmap.simulation.api.SimulationState

class MainState(simulationManager: SimulationManager, scene: Scene) : SimulationState(simulationManager, scene) {

    private lateinit var transformCamera: Camera
    private lateinit var debugCamera: Camera

    override val id: String
        get() = "main"

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        val transform = parameters.getOrNull(0) as Transform? ?: throw IllegalArgumentException("need transform")
        val calibrationCamera = parameters.getOrNull(1) as Camera? ?: throw IllegalArgumentException("need camera")

        setupCameras(calibrationCamera, transform)
    }

    private fun setupCameras(calibrationCamera: Camera, transform: Transform) {
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