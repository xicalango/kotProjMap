package xx.projmap.app

import xx.projmap.geometry.Rect
import xx.projmap.simulation.api.Simulation
import xx.projmap.simulation.impl.CalibrationState
import xx.projmap.simulation.impl.MainState
import xx.projmap.swing.ProjectionFrame
import javax.swing.JFrame

fun main(args: Array<String>) {

    val simulation = Simulation(listOf(::CalibrationState, ::MainState), "calibration", fpsLimit = 30)
    val frame = ProjectionFrame(simulation.eventQueue)
    val viewport2 = frame.mainViewport.createSubViewport(Rect(0.0, 0.0, 200.0, 150.0))

    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.showFrame()

    simulation.run(frame.mainViewport, mapOf(Pair("debug", viewport2)))

    System.exit(0)
}

