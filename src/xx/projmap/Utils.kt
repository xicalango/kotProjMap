package xx.projmap

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*

fun storeProperties(path: Path, block: Properties.() -> Unit) {
    val properties = Properties()
    block(properties)
    Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { stream ->
        properties.store(stream, System.currentTimeMillis().toString())
    }
    println("Persisted to: $path")
}

