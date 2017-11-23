package xx.projmap.scene2

import xx.projmap.events.Event
import xx.projmap.events.EventQueue
import xx.projmap.events.KeyEvent
import xx.projmap.events.QuitEvent
import xx.projmap.graphics.Renderer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Simulation() {

    val eventQueue: EventQueue = EventQueue()
    val scene: Scene = Scene()

    private val simulationConfig: SimulationConfig = loadSimulationConfig()

    var frameCounter = 0
        private set

    private var lastFrameCounter = 0
    private var last = System.currentTimeMillis()

    private var running: Boolean = true

    fun run(renderer: Renderer) {

        scene.initialize(this)

        var lastTimestamp = System.nanoTime()

        thread {
            while (running) {
                renderer.render(scene)
                if (simulationConfig.graphicsFpsLimit != null) {
                    Thread.sleep(1000 / simulationConfig.graphicsFpsLimit.toLong())
                }
            }
        }


        while (running) {
            val now = System.nanoTime()
            val dt = (now - lastTimestamp) / TimeUnit.SECONDS.toNanos(1).toDouble()
            lastTimestamp = now

            scene.startFrame()
            scene.update(dt)
            val events = eventQueue.currentEvents
            handleInternalEvents(events)
            scene.handleEvents(events)

            if (simulationConfig.simulationFpsLimit != null) {
                Thread.sleep(1000 / simulationConfig.simulationFpsLimit.toLong())
            }

            frameCounter++
            if (System.currentTimeMillis() - last >= 1000) {
                println("[simulation] FPS: ${frameCounter - lastFrameCounter}, totalFrames: $frameCounter, dt: $dt")
                last = System.currentTimeMillis()
                lastFrameCounter = frameCounter
            }
        }
    }

    private fun handleInternalEvents(events: List<Event>) {
        if (events.filterIsInstance<QuitEvent>().any() || events.filterIsInstance<KeyEvent>().any { it.keyCode == 123 }) {
            running = false
        }
    }


}