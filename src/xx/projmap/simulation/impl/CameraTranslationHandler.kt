package xx.projmap.simulation.impl

import xx.projmap.events.Direction
import xx.projmap.events.Event
import xx.projmap.events.KeyEvent
import xx.projmap.scene.Camera
import xx.projmap.simulation.api.Script

class CameraTranslationHandler(private val camera: Camera) : Script {

    override fun update(dt: Double) {
    }

    override fun handleEvent(event: Event) {
        if (event is KeyEvent) {
            if (event.direction == Direction.RELEASED) {
                when (event.keyChar) {
                    'w' -> camera.region.move(dy = -1.0)
                    's' -> camera.region.move(dy = 1.0)
                    'a' -> camera.region.move(dx = -1.0)
                    'd' -> camera.region.move(dx = 1.0)
                    'u' -> camera.region.resize(dh = 1.0)
                    'j' -> camera.region.resize(dh = -1.0)
                    'h' -> camera.region.resize(dw = 1.0)
                    'k' -> camera.region.resize(dw = -1.0)
                    'W' -> camera.region.move(dy = -10.0)
                    'S' -> camera.region.move(dy = 10.0)
                    'A' -> camera.region.move(dx = -10.0)
                    'D' -> camera.region.move(dx = 10.0)
                    'U' -> camera.region.resize(dh = 10.0)
                    'J' -> camera.region.resize(dh = -10.0)
                    'H' -> camera.region.resize(dw = 10.0)
                    'K' -> camera.region.resize(dw = -10.0)
                }
            }
        }
    }
}