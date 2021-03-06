package xx.projmap.swing

import xx.projmap.events.EventQueue
import xx.projmap.events.MouseButton
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.Point
import xx.projmap.geometry.Rect
import xx.projmap.graphics.RenderDestination
import xx.projmap.graphics.RenderableScene
import xx.projmap.graphics.Renderer
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.SwingUtilities

class ProjectionPanel(val eventQueue: EventQueue) : JPanel(), RenderDestination, Renderer {
    private var bufferedImage: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
    private var frameCounter = 0
    private var last = System.currentTimeMillis()

    private var renderableScene: RenderableScene? = null

    val graphicsAdapter: Graphics2DImpl

    override val region: GeoRect
        get() = Rect(0.0, 0.0, width.toDouble(), height.toDouble())

    init {
        preferredSize = Dimension(640, 480)

        background = Color.BLACK

        graphicsAdapter = Graphics2DImpl(bufferedImage.createGraphics())

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e != null) {
                    eventQueue.addEvent(MouseClickEvent(e.toPoint(), e.toMouseButton(), this@ProjectionPanel))
                }
            }
        })
    }

    override fun render(scene: RenderableScene) {
        this.renderableScene = scene
        repaint()
    }

    fun onResize(newDimension: Dimension) {
        preferredSize = newDimension
        bufferedImage = BufferedImage(newDimension.width, newDimension.height, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics2D = bufferedImage.createGraphics()
        graphics2D.background = Color(0, 0, 0, 0)
        graphicsAdapter.graphics2D = graphics2D
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        renderableScene?.render(graphicsAdapter)

        g?.drawImage(bufferedImage, 0, 0, this)

        frameCounter++
        if (System.currentTimeMillis() - last >= 1000) {
            println("[paintComponent] FPS: $frameCounter")
            last = System.currentTimeMillis()
            frameCounter = 0
        }
    }

}

fun MouseEvent.toMouseButton(): MouseButton = when {
    SwingUtilities.isLeftMouseButton(this) -> MouseButton.LEFT
    SwingUtilities.isMiddleMouseButton(this) -> MouseButton.MIDDLE
    SwingUtilities.isRightMouseButton(this) -> MouseButton.RIGHT
    else -> throw IllegalArgumentException("$this")
}

fun MouseEvent.toPoint(): Point = Point(x.toDouble(), y.toDouble())
