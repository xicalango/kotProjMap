package xx.projmap

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

fun getInputStream(filename: String, c: Class<*>): InputStream? {
    val path = Paths.get(filename)
    println("looking up $path on local filesystem")
    if (Files.exists(path)) {
        return Files.newInputStream(path)
    }

    println("looking up /$filename as resource")
    return c.getResourceAsStream("/$filename")
}
