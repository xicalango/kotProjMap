package xx.projmap.scene

import xx.projmap.geometry.GeoPoint
import java.util.concurrent.ConcurrentLinkedDeque

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

class EventQueue {

    private val events = ConcurrentLinkedDeque<Event>()

    fun addEvent(event: Event) {
        events.push(event)
    }

    fun getCurrentEvents(): List<Event> {
        val currentEvents = events.toList()
        events.clear()
        return currentEvents
    }

}