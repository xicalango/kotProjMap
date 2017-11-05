package xx.projmap.simulation.api

import xx.projmap.scene.*
import java.util.concurrent.TimeUnit

class Simulation(states: List<StateConstructor>, private val startState: String? = null, private val fpsLimit: Long? = 30) {
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
            simulationManager.render()

            if (fpsLimit != null) {
                Thread.sleep(1000 / fpsLimit)
            }

            frameCounter++
            if (System.currentTimeMillis() - last >= 1000) {
                println("[simulation] FPS: ${frameCounter - lastFrameCounter}")
                last = System.currentTimeMillis()
                frameCounter = lastFrameCounter
            }
        }
    }


}