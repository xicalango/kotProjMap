package xx.projmap.simulation

import org.junit.jupiter.api.Test
import xx.projmap.scene.Camera
import xx.projmap.swing.ProjectionFrame

internal class SimulationTest {

    @Test
    internal fun testSimulation() {
        val simulation = Simulation(::ProjectionFrame, listOf(::CalibrationState, ::MainState), "calibration")

        simulation.scene.cameras += Camera(simulation.mainViewport.region, simulation.mainViewport, id = "calibration")

        simulation.run()
    }
}
