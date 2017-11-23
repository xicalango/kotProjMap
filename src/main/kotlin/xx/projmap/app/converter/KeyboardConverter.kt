package xx.projmap.app.converter

import okio.Okio
import xx.projmap.app.KeyResource
import xx.projmap.app.KeyboardResource
import xx.projmap.app.keyboardResourceAdapter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.size != 2) {
        println("usage: KeyboardConverter <properties file> <json file>")
        exitProcess(1)
    }

    val propertiesPath = Paths.get(args[0])
    val jsonPath = Paths.get(args[1])

    if (!Files.exists(propertiesPath)) {
        println("properties file does not exist: $propertiesPath")
        exitProcess(1)
    }

    val keyProperties = Properties()

    Files.newInputStream(propertiesPath).use(keyProperties::load)

    val keyboardWidth = keyProperties.getProperty("keyboard.width", "460").toDouble()
    val keyboardHeight = keyProperties.getProperty("keyboard.height", "170").toDouble()

    val numKeys = keyProperties.getProperty("keys.count", "0").toInt()


    val keys = (0 until numKeys).map { index ->

        val x = keyProperties.getProperty("key$index.x")?.toDouble()!!
        val y = keyProperties.getProperty("key$index.y")?.toDouble()!!
        val w = keyProperties.getProperty("key$index.w")?.toDouble()!!
        val h = keyProperties.getProperty("key$index.h")?.toDouble()!!

        val keyChar = keyProperties.getProperty("key$index.char")?.toCharArray()?.getOrNull(0)
        val keyCode = keyProperties.getProperty("key$index.code")?.toInt()

        KeyResource(x, y, w, h, keyChar, keyCode)
    }.sortedWith(Comparator { k1, k2 -> k1.compareToWithDelta(k2, 5.0) })

    val keyboardResource = KeyboardResource(keyboardWidth, keyboardHeight, keys)

    Okio.buffer(Okio.sink(jsonPath)).use { sink ->
        keyboardResourceAdapter.toJson(sink, keyboardResource)
    }
}
