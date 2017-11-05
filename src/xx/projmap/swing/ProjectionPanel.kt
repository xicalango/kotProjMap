package xx.projmap.swing

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.Rect
import xx.projmap.scene.Viewport
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.JPanel

class ProjectionPanel : JPanel(), Viewport {

    private var bufferedImage: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
    override val graphicsAdapter: Graphics2DImpl

    private var frameCounter = 0
    private var last = System.currentTimeMillis()

    override var drawBorder: Boolean = true

    init {
        preferredSize = Dimension(640, 480)

        graphicsAdapter = Graphics2DImpl(bufferedImage.createGraphics())

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e != null) {
                    println("${e.x}, ${e.y}")
                }
            }
        })
    }

    fun onResize(newDimension: Dimension) {
        preferredSize = newDimension
        bufferedImage = BufferedImage(newDimension.width, newDimension.height, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics2D = bufferedImage.createGraphics()
        graphics2D.background = Color(0, 0, 0, 0)
        graphicsAdapter.graphics2D = graphics2D
    }

    override fun paintComponent(g: Graphics?) {
        g?.color = Color.BLACK
        g?.fillRect(0, 0, width, height)

        g?.drawImage(bufferedImage, 0, 0, this)

        frameCounter++
        if (System.currentTimeMillis() - last >= 1000) {
            println("[paintComponent] FPS: $frameCounter")
            last = System.currentTimeMillis()
            frameCounter = 0
        }
    }

    override val region: GeoRect
        get() = Rect(0.0, 0.0, width.toDouble(), height.toDouble())

    override fun clear() {
        graphicsAdapter.graphics2D.withColor(Color.BLACK, {
            it.fillRect(0, 0, width, height)
        })
    }

}

inline fun <R> Graphics2D.withColor(newColor: Color, body: (Graphics2D) -> R): R {
    val oldColor = color
    color = newColor
    val result = body(this)
    color = oldColor
    return result
}