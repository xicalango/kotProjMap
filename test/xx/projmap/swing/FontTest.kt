package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.events.EventQueue
import xx.projmap.geometry.Point
import xx.projmap.graphics.render4x6
import xx.projmap.graphics.withColor
import java.awt.Color
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JFrame

internal class FontTest {

    @Test
    internal fun testFonts() {
        val eventQueue = EventQueue()
        val panel = ProjectionPanel(eventQueue)

        val frame: JFrame = with(JFrame()) {

            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    val size = e?.component?.size
                    if (size != null) {
                        panel.onResize(size)
                    }
                }
            })

            add(panel)
            pack()
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            this
        }

        frame.isVisible = true

        var xpos = 10.0
        while (true) {
            panel.initialize()
            panel.graphicsAdapter.withColor(Color.WHITE) {
                drawPoint(Point(100.0, 100.0))
                render4x6(xpos, xpos, "The quick brown fox jumps over", xPointSpacing = 5.0, yPointSpacing = 5.0)
                render4x6(xpos, xpos + 40.0, "the lazy old dog. 1234567890", xPointSpacing = 5.0, yPointSpacing = 5.0)
            }
            panel.finish()

            panel.repaint()

            xpos += 0.3
            Thread.sleep(1000 / 30)
        }

    }
}