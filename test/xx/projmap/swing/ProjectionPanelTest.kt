package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.geometry.*
import xx.projmap.scene.Camera
import xx.projmap.scene.PointEntity
import xx.projmap.scene.RectEntity
import xx.projmap.scene.Scene
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JFrame

internal class ProjectionPanelTest {

    @Test
    internal fun testProjectionPanel() {
        val scene = Scene()
        val world = scene.world
        val entity = PointEntity(MutPoint(310.0, 110.0))
        val rectEntity = RectEntity(MutPoint(100.0, 100.0), 20.0, 20.0)
        world.entities += entity
        world.entities += rectEntity


        val srcQuad = Rect(0.0, 0.0, 640.0, 480.0).toQuad()
        val dstQuad = Quad(10.0, 10.0, 410.0, 100.0, 410.0, 250.0, 10.0, 250.0)
        val projectionTransform = Transformation(srcQuad, dstQuad)

        val panel = ProjectionPanel()
        scene.cameras += Camera(MutRect(0.0, 0.0, 640.0, 480.0), panel)

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

        while (true) {
            entity.move(dx = 0.1)
            scene.render()
            panel.render()
            Thread.sleep(1000 / 30)
        }
    }
}