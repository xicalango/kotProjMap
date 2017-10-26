package xx.projmap.swing

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

class ProjectionPanel : JPanel() {

    private var bufferedImage: BufferedImage = BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR)
    val graphicsAdapter: Graphics2DImpl

    private var frameCounter = 0
    private var last = System.currentTimeMillis()

    init {
        preferredSize = Dimension(640, 480)
        background = Color.WHITE
        foreground = Color.BLACK

        val graphics2D = bufferedImage.createGraphics()
        graphics2D.color = Color.BLACK
        graphics2D.background = Color.WHITE
        graphicsAdapter = Graphics2DImpl(graphics2D)
    }

    fun onResize(newDimension: Dimension) {
        preferredSize = newDimension
        bufferedImage = BufferedImage(newDimension.width, newDimension.height, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics2D = bufferedImage.createGraphics()
        graphics2D.color = Color.BLACK
        graphics2D.background = Color.WHITE
        graphicsAdapter.graphics2D = graphics2D
        repaint()
    }

    override fun paintComponent(g: Graphics?) {

        g?.drawImage(bufferedImage, 0, 0, this)

        frameCounter++
        if (System.currentTimeMillis() - last >= 1000) {
            println("[paintComponent] FPS: $frameCounter")
            last = System.currentTimeMillis()
            frameCounter = 0
        }
    }

}