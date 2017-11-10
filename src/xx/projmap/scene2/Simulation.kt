package xx.projmap.scene2

import xx.projmap.events.*
import xx.projmap.graphics.Renderer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Simulation(config: Properties = Properties()) {

    val eventQueue: EventQueue = EventQueue()
    val scene: Scene = Scene(config = config)

    private val simulationConfig: SimulationConfig = simulationConfigFromProperties(config)

    private var frameCounter = 0
    private var lastFrameCounter = 0
    private var last = System.currentTimeMillis()

    private var running: Boolean = true

    private var escPressedTimer: Double = 1.0
    private var escPressed: Boolean = false

    fun run(renderer: Renderer) {

        scene.initialize()

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

            if (escPressed) {
                escPressedTimer -= dt
                if (escPressedTimer <= 0) {
                    running = false
                }
            }

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
        events.filterIsInstance<KeyEvent>().forEach {
            if (it.keyCode == 27) {
                if (it.direction == Direction.PRESSED && !escPressed) {
                    escPressed = true
                    escPressedTimer = 1.0
                } else if (it.direction == Direction.RELEASED && escPressed) {
                    escPressed = false
                }
            }
        }
        if (events.filterIsInstance<QuitEvent>().any()) {
            running = false
        }
    }


}