package xx.projmap.scene2

import org.junit.jupiter.api.Test
import xx.projmap.events.EventQueue
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.Quad
import xx.projmap.geometry.Rect
import xx.projmap.geometry.Transformation
import xx.projmap.geometry.toQuad
import xx.projmap.swing.ProjectionFrame
import javax.swing.JFrame

internal class Scene2Test {

    private class RectEntity : Entity("rect") {

        init {
            addComponent(RectRenderable(Rect(0.0, 0.0, 5.0, 5.0)))
        }
    }

    @Test
    internal fun test() {
        val eventQueue = EventQueue()

        val frame = ProjectionFrame(eventQueue)
        val subViewport = frame.projectionPanel.createSubViewport(Rect(50.0, 100.0, 100.0, 100.0))

        val scene = Scene()

        val myEntity = object : Entity("myEntity") {
            init {
                origin.x = 10.0
                origin.y = 10.0

                addComponent(RectRenderable(Rect(0.0, 0.0, 10.0, 10.0)))
                addComponent(object : Behavior() {

                    lateinit var camera: Camera
                    lateinit var subCamera: Camera

                    init {
                        enabled = true
                    }

                    override fun setup() {
                        camera = sceneFacade.cameras.find { it.name == "mainCamera" }?.camera!!
                        subCamera = sceneFacade.cameras.find { it.name == "subCamera" }?.camera!!
                    }

                    override fun update(dt: Double) {
                        entity.origin.move(dx = 0.1)
                    }

                    override fun onMouseClicked(event: MouseClickEvent) {
                        println("$event")

                        val mainCam = camera.viewportToCamera(event.point)
                        val mainWorld = camera.viewportToWorld(event.point)
                        val subCam = subCamera.viewportToCamera(event.point)
                        val subWorld = subCamera.viewportToWorld(event.point)

                        println("mainCam: $mainCam")
                        println("mainWorld: $mainWorld")
                        println("subCam: $subCam")
                        println("subWorld: $subWorld")

                        val rectEntity = RectEntity()
                        rectEntity.origin.updateFrom(subWorld)
                        entity.sceneFacade.addEntity(rectEntity)

                    }
                })
            }
        }

        val origin = Rect(0.0, 0.0, 100.0, 75.0).toQuad()
        val dest = Quad(0.0, 0.0, 100.0, 10.0, 20.0, 210.0, 40.0, 100.0)

        val transformation = Transformation(origin, dest)

        val camera = CameraEntity(Rect(0.0, 0.0, 640.0, 480.0), frame.projectionPanel)
        camera.name = "mainCamera"
        val camera2 = CameraEntity(Rect(0.0, 0.0, 640.0, 480.0), subViewport)
        camera2.name = "subCamera"

        scene.addEntity(myEntity)
        scene.addEntity(camera)
        scene.addEntity(camera2)

        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        while (true) {
            scene.startFrame()
            scene.update(0.0)
            scene.handleEvents(eventQueue)
            frame.projectionPanel.render(scene)
            Thread.sleep(1000 / 30)
        }

    }

}