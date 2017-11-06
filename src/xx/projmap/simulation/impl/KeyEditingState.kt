package xx.projmap.simulation.impl

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.MutRect
import xx.projmap.geometry.Transform
import xx.projmap.scene.*
import xx.projmap.simulation.api.Script
import xx.projmap.simulation.api.SimulationState
import xx.projmap.simulation.api.SimulationStateManager
import java.awt.Color

class KeyEditingState(simulationStateManager: SimulationStateManager, scene: Scene, keys: List<GeoRect>? = null) : SimulationState(simulationStateManager, scene) {

    private val keyEntityHandler: KeyEntityHandler = KeyEntityHandler(keys ?: emptyList())
    private lateinit var transformCamera: Camera
    private lateinit var debugCamera: Camera

    override val id: String
        get() = "keyEditing"

    override fun initialize() {
        scene.world.entities += keyEntityHandler.entityGroup
        scripts += keyEntityHandler
    }

    override fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {
        val transform = parameters.getOrNull(0) as? Transform ?: throw IllegalArgumentException("need transform")
        val region = parameters.getOrNull(1) as? GeoRect ?: throw IllegalArgumentException("need srcRegion")

        setupCameras(region, transform)

        keyEntityHandler.camera = transformCamera
        keyEntityHandler.entityGroup.visible = true
    }

    override fun onDeactivation() {
        keyEntityHandler.entityGroup.visible = false
    }

    private fun setupCameras(region: GeoRect, transform: Transform) {
        scene.hideAllCameras()
        scene.cameras.removeIf { it.id == "transform" || it.id == "debug" }
        transformCamera = Camera(region, simulationStateManager.mainViewport, transform, id = "transform")

        val debugViewport = simulationStateManager.viewports["debug"]

        if (debugViewport != null) {
            debugCamera = Camera(region, debugViewport, id = "debug")
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
            'c' -> simulationStateManager.changeState("calibration")
        }
    }

}

class KeyEntityHandler(keys: List<GeoRect> = emptyList()) : Script {

    val entityGroup = EntityGroup()
    private val keys: MutableList<Entity> = entityGroup.entities

    private val defaultRect = MutRect(0.0, 0.0, 10.0, 10.0)

    var camera: Camera? = null

    private var currentKey: RectEntity? = null

    init {
        this.keys += keys.map { it.toTranslatedEntity(tag = "key") }
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
                'u' -> resizeRect(dh = -1.0)
                'j' -> resizeRect(dh = 1.0)
                'h' -> resizeRect(dw = -1.0)
                'k' -> resizeRect(dw = 1.0)
                'U' -> resizeRect(dh = -10.0)
                'J' -> resizeRect(dh = 10.0)
                'H' -> resizeRect(dw = -10.0)
                'K' -> resizeRect(dw = 10.0)
                'w' -> moveCurrentRect(dy = -1.0)
                's' -> moveCurrentRect(dy = 1.0)
                'a' -> moveCurrentRect(dx = -1.0)
                'd' -> moveCurrentRect(dx = 1.0)
                'W' -> moveCurrentRect(dy = -10.0)
                'S' -> moveCurrentRect(dy = 10.0)
                'A' -> moveCurrentRect(dx = -10.0)
                'D' -> moveCurrentRect(dx = 10.0)
            }
        }
    }

    fun moveCurrentRect(dx: Double = 0.0, dy: Double = 0.0) {
        currentKey?.rect?.move(dx, dy)
    }

    private fun resizeRect(dw: Double = 0.0, dh: Double = 0.0) {
        defaultRect.resize(dw, dh)
        currentKey?.rect?.resize(dw, dh)
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
                .filter { it.tag == "key" }
                .map { it as RectEntity }
                .find { key -> worldPoint in key.translatedRect }

        if (clickedKey != null) {
            return updateCurrentKey(clickedKey)
        }

        val newKey = RectEntity(defaultRect.copy(), origin = worldPoint.copy(), tag = "key")
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
