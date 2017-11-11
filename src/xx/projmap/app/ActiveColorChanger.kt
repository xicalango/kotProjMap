package xx.projmap.app

import xx.projmap.scene2.Behavior
import xx.projmap.scene2.Renderable
import java.awt.Color

class ActiveColorChanger(private val activeColor: Color = Color.RED, private val inactiveColor: Color = Color.WHITE) : Behavior() {

    private lateinit var renderable: Renderable

    var active: Boolean = false
        set(value) {
            field = value
            if (value) {
                renderable.color = activeColor
            } else {
                renderable.color = inactiveColor
            }
        }

    override fun initialize() {
        renderable = entity.findComponent()!!

    }
}