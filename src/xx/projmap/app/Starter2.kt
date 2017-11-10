package xx.projmap.app

import xx.projmap.geometry.Rect
import xx.projmap.graphics.createSubRenderDestination
import xx.projmap.scene2.Simulation
import xx.projmap.scene2.createCamera
import xx.projmap.swing.ProjectionFrame
import javax.swing.JFrame

fun main(args: Array<String>) {
    val simulation = Simulation()

    val frame = ProjectionFrame(simulation.eventQueue)

    simulation.scene.createEntity(::StateManager)
    val unitRect = frame.projectionPanel.region.toNormalized()
    simulation.scene.createCamera(unitRect, frame.projectionPanel, name = "mainCamera")
    val subRenderDestination = frame.projectionPanel.createSubRenderDestination(Rect(0.0, 0.0, 100.0, 75.0))
    simulation.scene.createCamera(unitRect, subRenderDestination, name = "debugCamera")

    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.isVisible = true

    simulation.run(frame.projectionPanel)

    System.exit(0)
}
