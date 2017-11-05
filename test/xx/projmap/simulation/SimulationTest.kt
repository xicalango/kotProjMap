package xx.projmap.simulation

import org.junit.jupiter.api.Test
import xx.projmap.geometry.Rect
import xx.projmap.simulation.api.Simulation
import xx.projmap.simulation.impl.CalibrationState
import xx.projmap.simulation.impl.MainState
import xx.projmap.swing.ProjectionFrame

internal class SimulationTest {

    @Test
    internal fun testSimulation() {
        val simulation = Simulation(listOf(::CalibrationState, ::MainState), "calibration", fpsLimit = 30)
        val frame = ProjectionFrame(simulation.eventQueue)
        val viewport2 = frame.mainViewport.createSubViewport(Rect(0.0, 0.0, 200.0, 150.0))

        frame.showFrame()

        simulation.run(frame.mainViewport, mapOf(Pair("debug", viewport2)))
    }
}
