package xx.projmap.swing

import xx.projmap.scene.EventQueue
import xx.projmap.scene.KeyEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import javax.swing.JFrame

class ProjectionFrame(private val eventQueue: EventQueue, private val projectionPanel: ProjectionPanel) : JFrame() {

    init {
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                val size = e?.component?.size
                if (size != null) {
                    projectionPanel.onResize(size)
                }
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: java.awt.event.KeyEvent?) {
                if (e != null) {
                    eventQueue.addEvent(KeyEvent(e.keyChar, this@ProjectionFrame))
                }
            }
        })

        add(projectionPanel)
        pack()
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    }

}
