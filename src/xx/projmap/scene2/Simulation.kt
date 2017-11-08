package xx.projmap.scene2

import xx.projmap.events.Event
import xx.projmap.events.EventQueue
import xx.projmap.events.KeyEvent
import xx.projmap.events.QuitEvent
import xx.projmap.graphics.Renderer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Simulation(private val graphicsFpsLimit: Int? = 60, private val simulationFpsLimit: Int? = 100) {

    val eventQueue: EventQueue = EventQueue()
    val scene: Scene = Scene()

    private var frameCounter = 0
    private var lastFrameCounter = 0
    private var last = System.currentTimeMillis()

    private var running: Boolean = true

    fun run(renderer: Renderer) {

        var lastTimestamp = System.nanoTime()

        thread {
            while (running) {
                renderer.render(scene)
                if (graphicsFpsLimit != null) {
                    Thread.sleep(1000 / graphicsFpsLimit.toLong())
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

    private fun handleInternalEvents(events: List<Event>) {
        if (events.filterIsInstance<KeyEvent>().any { it.keyChar == 'q' } || events.filterIsInstance<QuitEvent>().any()) {
            running = false
        }
    }


}