package xx.projmap.app

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
    private var offset = 0

    override fun setup() {
        keyboardEntity = sceneFacade.findEntity()!!
    }

    override fun update(dt: Double) {
        time -= dt

        if (time <= 0) {
            time = .1

            val keys = keyboardEntity.findChildren<KeyEntity>()
            val numKeys = keys.size

            keys.forEachIndexed { index, keyEntity ->
                keyEntity.findComponent<RectRenderable>()?.color = Color.getHSBColor(((index + offset) % numKeys) / numKeys.toFloat(), 0.5f, 1.0f)
            }
            offset++
        }
    }

    override fun onActivation() {
        keyboardEntity.findChildren<KeyEntity>().flatMap { it.findComponents<Renderable>() }.forEach { it.enabled = true }
    }


}