package xx.projmap.simulation.api

import xx.projmap.events.Event
import xx.projmap.scene.Scene
import xx.projmap.scene.Viewport

typealias StateConstructor = (SimulationStateManager, Scene) -> SimulationState

class SimulationStateManager(val scene: Scene) {
    private val states: MutableMap<String, SimulationState> = HashMap()

    private var currentState: SimulationState = NoState(this, scene)
    private var nextState: SimulationState? = null
    private var nextStateArgs: Array<out Any>? = null

    val viewports: MutableMap<String, Viewport> = HashMap()

    val mainViewport: Viewport
        get() = viewports["main"]!!

    fun addState(simulationState: SimulationState) {
        states += simulationState.id to simulationState
    }

    fun initialize() {
        states.values.forEach(SimulationState::initialize)
    }

    fun changeState(nextStateId: String, vararg args: Any) = changeState(states[nextStateId]!!, *args)

    fun changeState(nextState: SimulationState, vararg args: Any) {
        this.nextState = nextState
        this.nextStateArgs = args
    }

    fun update(dt: Double) {
        if (nextState != null) {
            val lastState = currentState
            currentState = nextState as SimulationState
            lastState.onDeactivation()
            currentState.onActivation(lastState, nextStateArgs!!)
            nextState = null
            nextStateArgs = null
        }

        currentState.update(dt)
        currentState.scripts.forEach { it.update(dt) }
    }

    fun handleEvent(event: Event) {
        currentState.handleEvent(event)
        currentState.scripts.forEach { it.handleEvent(event) }
    }

    fun render(scene: Scene) {
        viewports.values.forEach { it.render(scene) }
    }

}

operator fun SimulationStateManager.plusAssign(simulationState: SimulationState) = addState(simulationState)
