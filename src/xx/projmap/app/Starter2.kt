package xx.projmap.app

import xx.projmap.geometry.Rect
import xx.projmap.graphics.createSubRenderDestination
import xx.projmap.scene2.Simulation
import xx.projmap.scene2.createCamera
import xx.projmap.swing.ProjectionFrame

fun main(args: Array<String>) {
    val simulation = Simulation()

    val frame = ProjectionFrame(simulation.eventQueue)

    simulation.scene.createEntity(::StateManager)
    simulation.scene.createCamera(frame.projectionPanel.region.toNormalized(), frame.projectionPanel, name = "mainCamera")
    val subRenderDestination = frame.projectionPanel.createSubRenderDestination(Rect(0.0, 0.0, 100.0, 75.0))
    simulation.scene.createCamera(frame.projectionPanel.region.toNormalized(), subRenderDestination, name = "debugCamera")

    frame.isVisible = true

    simulation.run(frame.projectionPanel)

    System.exit(0)
}
