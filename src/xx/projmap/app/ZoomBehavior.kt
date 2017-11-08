package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.Rect
import xx.projmap.scene2.Behavior
import xx.projmap.scene2.Camera

private const val DEFAULT_ACCELERATION = .05
private const val MAX_SPEED = 1.0

class ZoomBehavior : Behavior() {

    private lateinit var originalRegion: GeoRect

    var camera: Camera? = null
        set(value) {
            field = value
            originalRegion = value?.region?.toImmutable() ?: Rect(0.0, 0.0, 0.0, 0.0)
        }

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
            camera?.region?.scale(Math.pow(10.0, direction * speed * dt))
        }
    }

    override fun onKeyPressed(event: KeyEvent) {
        acceleration = DEFAULT_ACCELERATION
        when (event.keyChar) {
            '-' -> direction = 1.0
            '+' -> direction = -1.0
        }
    }

    override fun onKeyReleased(event: KeyEvent) {
        acceleration = 0.0
        speed = 0.0
        direction = 0.0
        if (event.keyChar == 'r') {
            camera?.region?.updateFrom(originalRegion!!)
        }
    }
}