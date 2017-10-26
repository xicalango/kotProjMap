package xx.projmap.swing

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

class ProjectionPanel : JPanel() {

    private var bufferedImage: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR)
    val graphicsAdapter: Graphics2DImpl

    private var frameCounter = 0
    private var last = System.currentTimeMillis()

    init {
        preferredSize = Dimension(640, 480)

        graphicsAdapter = Graphics2DImpl(bufferedImage.createGraphics())
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

}