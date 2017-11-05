package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.MutRect
import xx.projmap.scene.Camera
import xx.projmap.scene.RectEntity
import xx.projmap.scene.Scene

internal class ProjectionFrameTest {

    @Test
    internal fun testProjectionFrame() {
        val scene = Scene()
        val world = scene.world
        world.entities += RectEntity(MutPoint(100.0, 100.0), 20.0, 20.0)

        val panel = ProjectionPanel(scene.eventQueue)
        scene.cameras += Camera(MutRect(0.0, 0.0, 320.0, 240.0), panel)

        val projectionFrame = ProjectionFrame(scene.eventQueue, panel)
        projectionFrame.isVisible = true

        while (true) {
            scene.render()
            panel.render()
            Thread.sleep(1000)
        }
    }

}