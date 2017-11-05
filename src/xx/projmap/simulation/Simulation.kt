package xx.projmap.simulation

import xx.projmap.scene.*
import java.util.concurrent.TimeUnit

class Simulation(mainFrameConstructor: (EventQueue) -> MainFrame, states: List<StateConstructor>, private val startState: String? = null, private val fpsLimit: Long? = 30) : Runnable {
    private val eventQueue: EventQueue = EventQueue()

    val scene: Scene = Scene(eventQueue)

    private val mainFrame: MainFrame = mainFrameConstructor(eventQueue)
    val mainViewport = mainFrame.mainViewport

    private val simulationManager: SimulationManager = SimulationManager(scene, states)

    private var running: Boolean = true

    override fun run() {

        simulationManager.initialize()

        if (startState != null) {
            simulationManager.changeState(startState)
        }

        mainFrame.showFrame()

        var lastTimestamp = System.nanoTime()

        while (running) {
            val now = System.nanoTime()
            val dt = (now - lastTimestamp) / TimeUnit.SECONDS.toNanos(1).toDouble()
            lastTimestamp = now

            eventQueue.getCurrentEvents().forEach { event ->
                if (event is QuitEvent) {
                    running = false
                }
                if (event is KeyEvent) {
                    if (event.keyChar == 'q') {
                        eventQueue.addEvent(QuitEvent())
                    }
                }
                simulationManager.handleEvent(event)
            }

            simulationManager.update(dt)
            scene.render()
            mainViewport.render()

            if (fpsLimit != null) {
                Thread.sleep(1000 / fpsLimit)
            }
        }
    }


}