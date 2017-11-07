package xx.projmap.scene2

import org.junit.jupiter.api.Test
import xx.projmap.geometry.Rect
import xx.projmap.scene.EventQueue
import xx.projmap.swing.ProjectionFrame
import javax.swing.JFrame

internal class Scene2Test {

    @Test
    internal fun test() {
        val eventQueue = EventQueue()

        val frame = ProjectionFrame(eventQueue)
        val subViewport = frame.projectionPanel.createSubViewport(Rect(0.0, 100.0, 100.0, 100.0))

        val scene = Scene()

        val myEntity = object : Entity("myEntity") {
            init {
                addComponent(RectRenderable(Rect(0.0, 0.0, 10.0, 10.0)))
                addComponent(object : Behavior() {

                    init {
                        enabled = true
                    }

                    override fun initialize() {
                        origin.x = 10.0
                        origin.y = 10.0
                    }

                    override fun update(dt: Double) {
                        entity.origin.move(dx = 0.1)
                        subViewport.region.move(dx = 1.0)
                    }
                })
            }
        }

        val camera = CameraEntity(Rect(0.0, 0.0, 640.0, 480.0), frame.projectionPanel)
        val camera2 = CameraEntity(Rect(0.0, 0.0, 30.0, 30.0), subViewport)

        scene.addEntity(myEntity)
        scene.addEntity(camera)
        scene.addEntity(camera2)

        scene.initialize()

        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        while (true) {
            scene.update(0.0)
            frame.projectionPanel.render(scene)
            Thread.sleep(1000 / 30)
        }

    }

}