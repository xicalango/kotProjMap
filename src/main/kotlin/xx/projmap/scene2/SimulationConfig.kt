package xx.projmap.scene2

import okio.Okio
import xx.projmap.moshi
import java.nio.file.Files
import java.nio.file.Paths

private val simulationConfigAdapter = moshi.adapter(SimulationConfig::class.java)

data class SimulationConfig(
        val graphicsFpsLimit: Int? = 60,
        val simulationFpsLimit: Int? = 100
)

fun loadSimulationConfig(): SimulationConfig {
    val simulationConfigPath = Paths.get("simulationConfig.json")

    if (!Files.exists(simulationConfigPath)) {
        return SimulationConfig()
    }


    return Okio.buffer(Okio.source(simulationConfigPath)).use(simulationConfigAdapter::fromJson) ?: SimulationConfig()
}
