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

class FlyingLetterBehavior(var velocity: Double = 100.0, val destination: MutPoint = MutPoint(0.0, -1000.0)) : Behavior() {

    private val maxLiveTime = 20.0
    private var liveTime = maxLiveTime

    var color: Float = 0.0f

    private lateinit var textRenderable: TextRenderable

    override fun initialize() {
        textRenderable = entity.findComponent()!!
    }

    override fun update(dt: Double) {
        liveTime -= dt

        textRenderable.color = Color.getHSBColor(color, 1.0f, (liveTime / maxLiveTime).toFloat())

        val dx = entity.origin.x - destination.x
        val dy = entity.origin.y - destination.y

        if (Math.abs(dx) > 1.0 || Math.abs(dy) > 1.0) {

            val phi = Math.atan2(dy, dx)

            val moveX = velocity * Math.cos(phi)
            val moveY = velocity * Math.sin(phi)

            entity.origin.move(dx = moveX * dt, dy = moveY * dt)
        }

        if (liveTime <= 0) {
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

    private val minX = -70.0
    private val maxX = 600.0

    private var destinationX = minX

    private val lineY = -300.0

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

            flyingLetter.flyingLetterBehavior.destination.x = destinationX
            flyingLetter.flyingLetterBehavior.destination.y = lineY

            destinationX += 20.0
            if (destinationX >= maxX) {
                destinationX = minX
            }

            flyingLetter.flyingLetterBehavior.velocity = -150.0 + random.nextInt(50)
            flyingLetter.textRenderable.text = keyEntity.keyBehavior.keyChar.toString()

            val color = random.nextFloat()
            flyingLetter.flyingLetterBehavior.color = color
            flyingLetter.textRenderable.color = Color.getHSBColor(color, 1.0f, 1.0f)
        }

    }

    override fun onActivation() {
        keyboardEntity.findChildren<KeyEntity>().map { it.findComponent<RectRenderable>() }.forEach { renderable ->
            renderable?.color = Color.WHITE
            renderable?.drawStyle = DrawStyle.LINE
        }
        destinationX = minX
    }

    override fun onDeactivation() {
        keyboardEntity.findChildren<KeyEntity>().map { it.findComponent<RectRenderable>() }.forEach { renderable ->
            renderable?.drawStyle = DrawStyle.FILL
        }

        entity.findChildren<FlyingLetter>().forEach { it.destroy = true }
    }

}