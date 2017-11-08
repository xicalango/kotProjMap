package xx.projmap.events

import java.util.concurrent.ConcurrentLinkedDeque

class EventQueue {

    private val events = ConcurrentLinkedDeque<Event>()

    val currentEvents: List<Event>
        get() {
            val currentEvents = events.toList()
            events.clear()
            return currentEvents
        }

    fun addEvent(event: Event) {
        events.push(event)
    }

}