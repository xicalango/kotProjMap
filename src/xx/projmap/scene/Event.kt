package xx.projmap.scene

import xx.projmap.geometry.GeoPoint
import java.util.concurrent.ConcurrentLinkedDeque

enum class EventType {
    MOUSE_CLICK_EVENT,
    KEY_PRESS_EVENT
}

enum class MouseButton {
    LEFT,
    RIGHT,
    MIDDLE
}

interface Event {
    val eventType: EventType
    val origin: Any
}

data class MouseClickEvent(val point: GeoPoint, val button: MouseButton, override val origin: Viewport) : Event {
    override val eventType: EventType
        get() = EventType.MOUSE_CLICK_EVENT
}

data class KeyEvent(val keyChar: Char, override val origin: Any) : Event {
    override val eventType: EventType
        get() = EventType.KEY_PRESS_EVENT
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