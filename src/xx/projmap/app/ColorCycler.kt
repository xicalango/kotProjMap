package xx.projmap.app

import xx.projmap.graphics.DrawStyle
import xx.projmap.scene2.*
import java.awt.Color

class ColorCyclerEntity : Entity("colorCycler") {

    init {
        addComponent(ColorCyclerBehavior())
    }

}

class ColorCyclerBehavior : Behavior() {

    private lateinit var keyboardEntity: KeyboardEntity

    private var time = .1
    private var current = 0

    private val length = 20

    override fun setup() {
        keyboardEntity = scene.findEntity()!!
        enabled = false
    }

    override fun update(dt: Double) {
        time -= dt

        if (time <= 0) {
            time = .1

            val keys = keyboardEntity.findChildren<KeyEntity>()
            val numKeys = keys.size

            keys.forEachIndexed { index, keyEntity ->
                val actualIndex = (index + current) % numKeys
                val color = if (actualIndex in 0..length) {
                    Color.getHSBColor(index / numKeys.toFloat(), 1.0f, 1.0f - (actualIndex / length.toFloat()))
                } else {
                    Color.BLACK
                }
                keyEntity.findComponent<RectRenderable>()?.color = color
            }
            current++
            current %= numKeys
        }
    }

    override fun onActivation() {
        keyboardEntity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach {
            it.color = Color.BLACK
            it.drawStyle = DrawStyle.FILL
            it.enabled = true
        }
    }

    override fun onDeactivation() {
        keyboardEntity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach {
            it.drawStyle = DrawStyle.LINE
        }
    }


}