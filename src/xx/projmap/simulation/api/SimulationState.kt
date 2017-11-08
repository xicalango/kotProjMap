package xx.projmap.simulation.api

import xx.projmap.events.Event
import xx.projmap.scene.Scene

abstract class SimulationState(val simulationStateManager: SimulationStateManager, val scene: Scene) {

    abstract val id: String

    val scripts: MutableList<Script> = ArrayList()

    open fun initialize() {

    }

    open fun onActivation(previousState: SimulationState, parameters: Array<out Any>) {

    }

    open fun onDeactivation() {

    }

    abstract fun update(dt: Double)
    abstract fun handleEvent(event: Event)

}

class NoState(simulationStateManager: SimulationStateManager, scene: Scene) : SimulationState(simulationStateManager, scene) {
    override val id: String
        get() = "__none"

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) {
    }

}