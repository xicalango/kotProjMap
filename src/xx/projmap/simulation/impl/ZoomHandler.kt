package xx.projmap.simulation.impl

import xx.projmap.scene.Camera
import xx.projmap.scene.Direction
import xx.projmap.scene.Event
import xx.projmap.scene.KeyEvent
import xx.projmap.simulation.api.Script

internal const val DEFAULT_ACCELERATION = .05
internal const val MAX_SPEED = 1.0

class ZoomHandler(private val camera: Camera) : Script {

    private var speed = 0.0
    private var acceleration = 0.0
    private var direction = 0.0

    override fun update(dt: Double) {
        if (acceleration >= 0.0 && speed < MAX_SPEED) {
            speed += acceleration * dt
            if (Math.abs(speed) >= MAX_SPEED) {
                speed = MAX_SPEED
            }
        }

        if (speed != 0.0) {
            camera.region.scale(Math.pow(10.0, direction * speed * dt))
        }
    }

    override fun handleEvent(event: Event) = when (event) {
        is KeyEvent -> handleKeyEvent(event)
        else -> Unit
    }

    private fun handleKeyEvent(event: KeyEvent) {
        if (event.direction == Direction.RELEASED) {
            acceleration = 0.0
            speed = 0.0
            direction = 0.0
        } else {
            acceleration = DEFAULT_ACCELERATION
            when (event.keyChar) {
                '-' -> direction = 1.0
                '+' -> direction = -1.0
            }
        }
    }

}