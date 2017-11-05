package xx.projmap.simulation

import xx.projmap.scene.Event

interface Script {

    fun update(dt: Double)
    fun handleEvent(event: Event)

}