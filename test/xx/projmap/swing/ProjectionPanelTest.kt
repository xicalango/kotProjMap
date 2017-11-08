package xx.projmap.swing

import org.junit.jupiter.api.Test
import xx.projmap.events.Direction
import xx.projmap.events.EventQueue
import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.*
import xx.projmap.scene.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import javax.swing.JFrame

internal class ProjectionPanelTest {

    @Test
    internal fun testProjectionPanel() {
        val eventQueue = EventQueue()
        val scene = Scene(eventQueue)
        val world = scene.world
        val entity = PointEntity(origin = MutPoint(310.0, 110.0))
        val rectEntity = RectEntity(Rect(100.0, 100.0, 20.0, 20.0))
        world.entities += entity
        world.entities += rectEntity


        val srcQuad = Rect(0.0, 0.0, 640.0, 480.0).toQuad()
        val dstQuad = Quad(10.0, 10.0, 410.0, 100.0, 410.0, 250.0, 10.0, 250.0)
        val projectionTransform = Transformation(srcQuad, dstQuad)

        val panel = ProjectionPanel(scene.eventQueue)
        val viewport2 = panel.graphicsAdapter.createViewport(Rect(0.0, 0.0, 100.0, 75.0))
        viewport2.region.scale(2.0)
        scene.cameras += Camera(MutRect(0.0, 0.0, 320.0, 240.0), panel, projectionTransform)
        val camera2 = Camera(MutRect(0.0, 0.0, 640.0, 480.0), viewport2, id = "debug")
        scene.cameras += camera2

        val frame: JFrame = with(JFrame()) {

            addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    val size = e?.component?.size
                    if (size != null) {
                        panel.onResize(size)
                    }
                }
            })

            addKeyListener(object : KeyAdapter() {
                override fun keyReleased(e: java.awt.event.KeyEvent?) {
                    if (e != null) {
                        scene.eventQueue.addEvent(KeyEvent(e.keyChar, Direction.RELEASED, this@with))
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
            scene.eventQueue.getCurrentEvents().forEach { event ->
                when (event) {
                    is MouseClickEvent -> handleMouseClick(scene, event)
                    is KeyEvent -> handleKey(scene, event)
                }
            }
            entity.move(dx = 0.1)
            viewport2.region.move(dy = +1.0)
            panel.render(scene)
            Thread.sleep(1000 / 30)
        }
    }

    private fun handleKey(scene: Scene, event: KeyEvent) {
        if (event.keyChar == '2') {
            scene.cameras.filter { it.id == "debug" }.forEach { it.visible = !it.visible }
        }
    }

    private fun handleMouseClick(scene: Scene, event: MouseClickEvent) {
        println("Event: $event")
        println("screen: ${event.point}")
        val dstPoint = MutPoint()
        scene.cameras.filter { it.viewport == event.origin }.forEach { camera ->
            camera.viewportToCamera(event.point, dstPoint)
            println("camera: $dstPoint")
            camera.viewportToWorld(event.point, dstPoint)
            println("world: $dstPoint")

            scene.world.entities += PointEntity(dstPoint.copy())
        }
    }
}