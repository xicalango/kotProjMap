package xx.projmap.simulation

import org.junit.jupiter.api.Test
import xx.projmap.events.EventQueue
import xx.projmap.geometry.Rect
import xx.projmap.scene.Scene
import xx.projmap.simulation.api.Simulation
import xx.projmap.simulation.api.SimulationStateManager
import xx.projmap.simulation.impl.CalibrationState
import xx.projmap.simulation.impl.KeyEditingState
import xx.projmap.swing.ProjectionFrame

internal class SimulationTest {

    @Test
    internal fun testSimulation() {
        val eventQueue = EventQueue()
        val scene = Scene(eventQueue)

        val stateManager = SimulationStateManager(scene)
        val calibrationState = CalibrationState(stateManager, scene)
        val keyEditingState = KeyEditingState(stateManager, scene)

        stateManager.addState(calibrationState)
        stateManager.addState(keyEditingState)

        val simulation = Simulation(stateManager, "calibration")
        val frame = ProjectionFrame(simulation.eventQueue)
        val viewport2 = frame.mainViewport.createSubViewport(Rect(0.0, 0.0, 200.0, 150.0))

        frame.showFrame()

        simulation.run(frame.mainViewport, mapOf(Pair("debug", viewport2)))
    }
}
