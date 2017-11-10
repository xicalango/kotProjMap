package xx.projmap

import org.junit.jupiter.api.Test
import xx.projmap.app.KeyEntity
import xx.projmap.app.KeyboardEntity
import xx.projmap.scene2.Scene

internal class TestOrder {


    @Test
    internal fun testOrder() {

        val scene = Scene()
        val keyboardEntity = KeyboardEntity()
        keyboardEntity.initialize(scene)

        val sorted = keyboardEntity.findChildren<KeyEntity>().map { it.origin }.sorted()

        sorted.forEach {
            println(it)
        }

    }
}