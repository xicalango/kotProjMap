package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.geometry.MutRect
import xx.projmap.geometry.Rect
import xx.projmap.scene.Camera
import xx.projmap.scene.EventQueue
import xx.projmap.scene.RectEntity
import xx.projmap.scene.Scene

internal class ProjectionFrameTest {

    @Test
    internal fun testProjectionFrame() {
        val eventQueue = EventQueue()
        val scene = Scene(eventQueue)
        val world = scene.world
        world.entities += RectEntity(Rect(100.0, 100.0, 20.0, 20.0))

        val projectionFrame = ProjectionFrame(eventQueue)
        scene.cameras += Camera(MutRect(0.0, 0.0, 320.0, 240.0), projectionFrame.mainViewport)

        projectionFrame.showFrame()

        while (true) {
            scene.render()
            projectionFrame.mainViewport.render()
            Thread.sleep(1000)
        }
    }

}