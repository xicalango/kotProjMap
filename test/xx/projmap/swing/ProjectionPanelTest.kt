package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.geometry.*
import xx.projmap.scene.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JFrame

internal class ProjectionPanelTest {

    @Test
    internal fun testProjectionPanel() {
        val scene = Scene()
        val world = scene.world
        val entity = PointEntity(MutPoint(10.0, 10.0))
        world.entities += entity
        world.entities += RectEntity(MutPoint(100.0, 100.0), 20.0, 20.0)


        val srcQuad = Rect(0.0, 0.0, 640.0, 480.0).toQuad()
        val dstQuad = Quad(10.0, 10.0, 410.0, 100.0, 410.0, 250.0, 10.0, 250.0)
        val projectionTransform = Transformation(srcQuad, dstQuad).toTransform()

        val panel = ProjectionPanel()
        val viewport = panel.graphicsAdapter.createViewport(MutRect(0.0, 0.0, 640.0, 480.0))
        val viewport2 = panel.graphicsAdapter.createViewport(MutRect(320.0, 240.0, 320.0, 240.0))
        scene.cameras += Camera(MutRect(0.0, 0.0, 640.0, 480.0), viewport, projectionTransform)
        scene.cameras += Camera(MutRect(0.0, 0.0, 320.0, 240.0), viewport2)

        val frame: JFrame = with(JFrame()) {

            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    val size = e?.component?.size
                    if (size != null) {
                        panel.onResize(size)
                        viewport.region.w = panel.size.getWidth()
                        viewport.region.h = panel.size.getHeight()
                    }
                }
            })

            add(panel)
            pack()
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            this
        }

        frame.isVisible = true

        var dv = -1.0
        while (true) {
            entity.move(dx = dv)
            viewport2.region.x += dv
            viewport2.region.y += dv
            if (viewport2.region.x < 0 || viewport2.region.y < 0 || viewport2.region.x > panel.width || viewport2.region.y > panel.height) {
                dv = -dv
            }
            scene.render()
            panel.repaint()
            Thread.sleep(1000 / 30)
        }
    }
}