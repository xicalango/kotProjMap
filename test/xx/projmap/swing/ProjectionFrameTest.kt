package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.MutRect
import xx.projmap.geometry.Rect
import xx.projmap.scene.*
import javax.swing.JFrame

internal class ProjectionFrameTest {

    @Test
    internal fun testProjectionFrame() {
        val eventQueue = EventQueue()
        val scene = Scene(eventQueue)
        val world = scene.world
        world.entities += RectEntity(Rect(100.0, 100.0, 20.0, 20.0))
        world.entities += TextEntity("Hallo Weld!", MutPoint(50.0, 50.0))

        val projectionFrame = ProjectionFrame(eventQueue)
        scene.cameras += Camera(MutRect(0.0, 0.0, 320.0, 240.0), projectionFrame.mainViewport)

        projectionFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        projectionFrame.showFrame()

        while (true) {
            projectionFrame.mainViewport.render(scene)
            Thread.sleep(1000)
        }
    }

}