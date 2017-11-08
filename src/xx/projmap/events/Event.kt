package xx.projmap.events

import xx.projmap.geometry.GeoPoint
import xx.projmap.scene.Viewport

enum class MouseButton {
    LEFT,
    RIGHT,
    MIDDLE
}

enum class Direction {
    PRESSED,
    RELEASED
}

sealed class Event {
    abstract val origin: Any
}

data class MouseClickEvent(val point: GeoPoint, val button: MouseButton, override val origin: Viewport) : Event()
data class KeyEvent(val keyChar: Char, val direction: Direction, override val origin: Any) : Event()
object QuitEvent : Event() {
    override val origin: Any
        get() = Unit
}

