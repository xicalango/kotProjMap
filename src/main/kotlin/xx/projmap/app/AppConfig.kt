package xx.projmap.app

import xx.projmap.geometry.MutPoint
import xx.projmap.moshi
import xx.projmap.scene2.Entity

data class AppConfig(
        var defaultKeyWidth: Double = 11.0,
        var defaultKeyHeight: Double = 11.0,
        val calibrationPoints: List<MutPoint> = emptyList(),
        val keyboardFile: String = "keyboard.json"
)

val appConfigAdapter = moshi.adapter(AppConfig::class.java).indent("  ")

class AppConfigEntity(val config: AppConfig) : Entity("config")
