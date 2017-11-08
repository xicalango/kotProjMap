package xx.projmap.simulation.api

import xx.projmap.events.Event

interface Script {

    fun update(dt: Double)
    fun handleEvent(event: Event)

}