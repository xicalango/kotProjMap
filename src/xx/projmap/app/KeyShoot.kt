package xx.projmap.app

import xx.projmap.events.KeyEvent
import xx.projmap.events.MouseButton
import xx.projmap.events.MouseClickEvent
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.MutRect
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

    private val maxLiveTime = 60.0
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

class DecayingRect : Entity("decayingRect") {

    val rectRenderable = RectRenderable(MutRect(0.0, 0.0, 0.0, 0.0))
    val decayingRectBehavior = DecayingRectBehavior()

    init {
        addComponent(rectRenderable)
        addComponent(decayingRectBehavior)
    }

}

class DecayingRectBehavior : Behavior() {

    private val maxLiveTime = 2.0
    private var liveTime = maxLiveTime

    var color: Float = 0.0f

    private lateinit var rectRenderable: RectRenderable

    override fun initialize() {
        rectRenderable = entity.findComponent()!!
    }

    override fun update(dt: Double) {
        liveTime -= dt

        rectRenderable.color = Color.getHSBColor(color, 1.0f, (liveTime / maxLiveTime).toFloat())

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

    private var minX = -70.0
    private val maxX = 600.0

    private var minY = -300.0
    private val maxY = 0.0

    private val xStep = 20.0
    private val yStep = 25.0

    private var lineX = minX
    private var lineY = minY

    override fun setup() {
        keyboardEntity = scene.findEntity()!!
        keyboardBehavior = keyboardEntity.findComponent()!!
        enabled = false
    }

    override fun onKeyReleased(event: KeyEvent) {
        val keyEntity = keyboardBehavior.findKeyByEvent(event)

        if (keyEntity != null) {
            if (keyEntity.keyBehavior.keyCode == 10) {
                lineY += 25.0
                lineX = minX

                if (lineY >= maxY) {
                    lineY = minY
                }
                return
            }

            val flyingLetter = scene.createEntity(::FlyingLetter, parent = entity, name = "key_${keyEntity.keyBehavior.keyChar}")
            flyingLetter.origin.set(keyEntity.origin)

            flyingLetter.flyingLetterBehavior.destination.x = lineX
            flyingLetter.flyingLetterBehavior.destination.y = lineY

            lineX += xStep
            if (lineX >= maxX) {
                lineX = minX
                lineY += yStep
            }

            flyingLetter.flyingLetterBehavior.velocity = -250.0 + random.nextInt(50)
            flyingLetter.textRenderable.text = keyEntity.keyBehavior.keyChar.toString()

            val color = random.nextFloat()
            flyingLetter.flyingLetterBehavior.color = color

            val decayingRect = scene.createEntity(::DecayingRect, parent = entity, name = "decay_${keyEntity.keyBehavior.keyChar}")
            decayingRect.origin.set(keyEntity.origin)
            decayingRect.rectRenderable.rect.updateFrom(keyEntity.rectRenderable.rect)
            decayingRect.decayingRectBehavior.color = color
        }

    }

    override fun onMouseClicked(event: MouseClickEvent) {
        if (event.button == MouseButton.RIGHT) {
            val worldPoint = scene.getMainCamera().camera.viewportToWorld(event.point)
            lineX = worldPoint.x
            lineY = worldPoint.y
            minX = worldPoint.x
            minY = worldPoint.y
        }
    }

    override fun onActivation() {
        keyboardEntity.findChildren<KeyEntity>().map { it.findComponent<RectRenderable>() }.forEach { renderable ->
            renderable?.color = Color.WHITE
            renderable?.drawStyle = DrawStyle.LINE
        }
        lineX = minX
    }

    override fun onDeactivation() {
        keyboardEntity.findChildren<KeyEntity>().map { it.findComponent<RectRenderable>() }.forEach { renderable ->
            renderable?.drawStyle = DrawStyle.FILL
        }

        entity.findChildren<FlyingLetter>().forEach { it.destroy = true }
    }

}