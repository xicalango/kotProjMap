package xx.projmap.events

import java.util.concurrent.ConcurrentLinkedDeque

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