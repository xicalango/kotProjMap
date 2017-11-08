package xx.projmap.app

import xx.projmap.scene2.Simulation
import xx.projmap.scene2.createCamera
import xx.projmap.swing.ProjectionFrame

fun main(args: Array<String>) {
    val simulation = Simulation()

    val frame = ProjectionFrame(simulation.eventQueue)

    simulation.scene.createEntity(::StateManager)
    simulation.scene.createCamera(frame.projectionPanel.region.toNormalized(), frame.projectionPanel)

    frame.isVisible = true

    simulation.run(frame.projectionPanel)

    System.exit(0)
}
