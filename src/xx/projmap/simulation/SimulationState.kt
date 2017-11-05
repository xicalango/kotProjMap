package xx.projmap.simulation

import xx.projmap.scene.Event
import xx.projmap.scene.Scene

interface SimulationState {

    val id: String
    val simulationManager: SimulationManager
    val scene: Scene

    fun initialize() {

    }

    fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {

    }

    fun update(dt: Double)
    fun handleEvent(event: Event)

}

class NoState(override val simulationManager: SimulationManager, override val scene: Scene) : SimulationState {
    override val id: String
        get() = "__none"

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) {
    }

}