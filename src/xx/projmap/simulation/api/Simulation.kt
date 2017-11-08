package xx.projmap.simulation.api

import xx.projmap.events.EventQueue
import xx.projmap.events.KeyEvent
import xx.projmap.events.QuitEvent
import xx.projmap.scene.Scene
import xx.projmap.scene.Viewport
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Simulation(val simulationStateManager: SimulationStateManager, private val startState: String? = null, private val graphicsFpsLimit: Int? = 60, private val simulationFpsLimit: Int? = 100) {
    val eventQueue: EventQueue = simulationStateManager.scene.eventQueue
    val scene: Scene = simulationStateManager.scene

    private var frameCounter = 0
    private var lastFrameCounter = 0
    private var last = System.currentTimeMillis()

    private var running: Boolean = true

    fun run(mainViewport: Viewport, additionalViewports: Map<String, Viewport> = emptyMap()) {

        simulationStateManager.viewports.clear()
        simulationStateManager.viewports += additionalViewports
        simulationStateManager.viewports["main"] = mainViewport

        simulationStateManager.initialize()

        if (startState != null) {
            simulationStateManager.changeState(startState)
        }

        var lastTimestamp = System.nanoTime()

        thread {
            while (running) {
                simulationStateManager.render(scene)
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
                simulationStateManager.handleEvent(event)
            }

            simulationStateManager.update(dt)
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