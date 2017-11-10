package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.geometry.MutPoint
import xx.projmap.graphics.DrawStyle
import xx.projmap.scene2.*
import java.awt.Color
import java.util.*

class FlyingLetter : Entity("Flying letter") {

    val textRenderable = TextRenderable()
    val flyingLetterBehavior = FlyingLetterBehavior()

    init {
        textRenderable.setSpacing(3.0)
        addComponent(textRenderable)
        addComponent(flyingLetterBehavior)
    }
}

class FlyingLetterBehavior(var velocity: MutPoint = MutPoint()) : Behavior() {


    override fun update(dt: Double) {
        entity.origin.move(velocity.x * dt, velocity.y * dt)

        if (entity.origin.y <= -1000.0) {
            entity.destroy = true
        }
    }

}

class KeyShootEntity : Entity("shoot") {

    val keyShootBehavior = KeyShootBehavior()

    init {
        addComponent(keyShootBehavior)
    }

}

class KeyShootBehavior : Behavior() {

    private val random = Random()

    private lateinit var keyboardEntity: KeyboardEntity
    private lateinit var keyboardBehavior: KeyboardBehavior

    override fun setup() {
        keyboardEntity = sceneFacade.findEntity()!!
        keyboardBehavior = keyboardEntity.findComponent()!!
        enabled = false
    }

    override fun onKeyReleased(event: KeyEvent) {
        val keyEntity = keyboardBehavior.findEntityByEvent(event)

        if (keyEntity != null) {
            val flyingLetter = sceneFacade.createEntity(::FlyingLetter, parent = entity, name = "key_${keyEntity.keyBehavior.keyChar}")
            flyingLetter.origin.set(keyEntity.origin)
            flyingLetter.flyingLetterBehavior.velocity.y = -(100.0 + random.nextInt(50))
            flyingLetter.textRenderable.text = keyEntity.keyBehavior.keyChar.toString()
            flyingLetter.textRenderable.color = Color.getHSBColor(random.nextFloat(), 1.0f, 1.0f)
        }

    }

    override fun onActivation() {
        keyboardEntity.findChildren<KeyEntity>().map { it.findComponent<RectRenderable>() }.forEach { renderable ->
            renderable?.color = Color.WHITE
            renderable?.drawStyle = DrawStyle.LINE
        }
    }

    override fun onDeactivation() {
        keyboardEntity.findChildren<KeyEntity>().map { it.findComponent<RectRenderable>() }.forEach { renderable ->
            renderable?.drawStyle = DrawStyle.FILL
        }

        entity.findChildren<FlyingLetter>().forEach { it.destroy = true }
    }

}