package xx.projmap.simulation.api

import xx.projmap.scene.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Simulation(states: List<StateConstructor>, private val startState: String? = null, private val graphicsFpsLimit: Int? = 60, private val simulationFpsLimit: Int? = 100) {
    val eventQueue: EventQueue = EventQueue()

    val scene: Scene = Scene(eventQueue)

    private var frameCounter = 0
    private var lastFrameCounter = 0
    private var last = System.currentTimeMillis()

    private val simulationManager: SimulationManager = SimulationManager(scene, states)

    private var running: Boolean = true

    fun run(mainViewport: Viewport, additionalViewports: Map<String, Viewport> = emptyMap()) {

        simulationManager.viewports.clear()
        simulationManager.viewports += additionalViewports
        simulationManager.viewports["main"] = mainViewport

        simulationManager.initialize()

        if (startState != null) {
            simulationManager.changeState(startState)
        }

        var lastTimestamp = System.nanoTime()

        thread {
            while (running) {
                simulationManager.render(scene)
                if (graphicsFpsLimit != null) {
                    Thread.sleep(1000 / graphicsFpsLimit.toLong())
                }
            }
        }

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
                        eventQueue.addEvent(QuitEvent)
                    }
                }
                simulationManager.handleEvent(event)
            }

            simulationManager.update(dt)
            if (simulationFpsLimit != null) {
                Thread.sleep(1000 / simulationFpsLimit.toLong())
            }

            frameCounter++
            if (System.currentTimeMillis() - last >= 1000) {
                println("[simulation] FPS: ${frameCounter - lastFrameCounter}, totalFrames: $frameCounter, dt: $dt")
                last = System.currentTimeMillis()
                lastFrameCounter = frameCounter
            }
        }
    }


}