package xx.projmap.simulation

import xx.projmap.scene.Event
import xx.projmap.scene.Scene

typealias StateConstructor = (SimulationManager, Scene) -> SimulationState

class SimulationManager(val scene: Scene, stateConstructors: List<StateConstructor>) {

    private val states: MutableMap<String, SimulationState> = stateConstructors.fold(HashMap(), { acc, simulationStateConstructor ->
        val simulationState = simulationStateConstructor(this, scene)
        acc.put(simulationState.id, simulationState)
        acc
    })

    private var currentState: SimulationState = NoState(this, scene)
    private var nextState: SimulationState? = null
    private var nextStateArgs: Array<out Any>? = null

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
            currentState.onActivation(lastState, nextStateArgs!!)
            nextState = null
            nextStateArgs = null
        }

        currentState.update(dt)
    }

    fun handleEvent(event: Event) {
        currentState.handleEvent(event)
    }

}