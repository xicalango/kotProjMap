package xx.projmap.swing

import xx.projmap.events.Direction
import xx.projmap.events.EventQueue
import xx.projmap.events.KeyEvent
import xx.projmap.events.QuitEvent
import xx.projmap.scene.Viewport
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import javax.swing.JFrame

class ProjectionFrame(private val eventQueue: EventQueue) : JFrame() {

    val projectionPanel: ProjectionPanel = ProjectionPanel(eventQueue)

    val mainViewport: Viewport = projectionPanel

    fun showFrame() {
        isVisible = true
    }

    init {
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                val size = e?.component?.size
                if (size != null) {
                    projectionPanel.onResize(size)
                }
            }

            override fun componentHidden(e: ComponentEvent?) {
                eventQueue.addEvent(QuitEvent)
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: java.awt.event.KeyEvent?) {
                if (e != null) {
                    eventQueue.addEvent(KeyEvent(e.keyChar, Direction.PRESSED, this@ProjectionFrame))
                }
            }

            override fun keyReleased(e: java.awt.event.KeyEvent?) {
                if (e != null) {
                    eventQueue.addEvent(KeyEvent(e.keyChar, Direction.RELEASED, this@ProjectionFrame))
                }
            }
        })

        add(projectionPanel)
        pack()
    }

}
