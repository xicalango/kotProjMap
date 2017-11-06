package xx.projmap.simulation.impl

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.Rect
import xx.projmap.geometry.Transform
import xx.projmap.scene.*
import xx.projmap.simulation.api.Script
import xx.projmap.simulation.api.SimulationManager
import xx.projmap.simulation.api.SimulationState
import java.awt.Color

class MainState(simulationManager: SimulationManager, scene: Scene) : SimulationState(simulationManager, scene) {

    private val keyEntityHandler: KeyEntityHandler = KeyEntityHandler()
    private lateinit var transformCamera: Camera
    private lateinit var debugCamera: Camera

    override val id: String
        get() = "main"

    override fun initialize() {
        scene.world.entities += keyEntityHandler.entityGroup
        scripts += keyEntityHandler
    }

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        val transform = parameters.getOrNull(0) as? Transform ?: throw IllegalArgumentException("need transform")
        val calibrationCamera = parameters.getOrNull(1) as? Camera ?: throw IllegalArgumentException("need camera")

        setupCameras(calibrationCamera, transform)

        keyEntityHandler.camera = transformCamera
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

class KeyEntityHandler(keys: List<GeoRect> = emptyList()) : Script {

    val entityGroup = EntityGroup()
    private val keys: MutableList<Entity> = entityGroup.entities

    var camera: Camera? = null

    private var currentKey: RectEntity? = null

    init {
        this.keys += keys.map { it.toEntity() }
    }

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is MouseClickEvent -> handleMouseClick(event)
            is KeyEvent -> handleKeyEvent(event)
        }
    }

    private fun handleKeyEvent(event: KeyEvent) {
        if (event.direction == Direction.RELEASED) {
            when (event.keyChar) {
                'r' -> removeCurrentKey()
            }
        }
    }

    private fun KeyEntityHandler.removeCurrentKey() {
        val key = currentKey ?: return
        removeKey(key)
    }

    private fun removeKey(key: RectEntity) {
        keys.remove(key)

        if (key == currentKey) {
            updateCurrentKey(keys.getOrNull(0) as? RectEntity)
        }
    }

    private fun handleMouseClick(mouseClickEvent: MouseClickEvent) {

        val worldPoint = camera?.viewportToWorld(mouseClickEvent.point) ?: return

        val clickedKey = keys
                .filter { it is RectEntity }
                .map { it as RectEntity }
                .find { key -> worldPoint in key.translatedRect }

        if (clickedKey != null) {
            return updateCurrentKey(clickedKey)
        }

        val newKey = RectEntity(Rect(0.0, 0.0, 100.0, 100.0), origin = worldPoint.copy())
        keys += newKey
        updateCurrentKey(newKey)
    }

    private fun updateCurrentKey(clickedKey: RectEntity?) {
        println("$currentKey, ${currentKey?.color}")
        currentKey?.color = Color.WHITE
        println("$currentKey, ${currentKey?.color}")
        clickedKey?.color = Color.RED
        currentKey = clickedKey
    }
}
