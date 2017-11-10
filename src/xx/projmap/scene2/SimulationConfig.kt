package xx.projmap.scene2

import java.util.*

data class SimulationConfig(
        val graphicsFpsLimit: Int? = 60,
        val simulationFpsLimit: Int? = 100
)

fun simulationConfigFromProperties(properties: Properties): SimulationConfig {
    val graphicsFpsLimit = properties.getProperty("sim.${SimulationConfig::graphicsFpsLimit.name}", "60").toInt()
    val simulationFpsLimit = properties.getProperty("sim.${SimulationConfig::simulationFpsLimit.name}", "100").toInt()

    return SimulationConfig(graphicsFpsLimit, simulationFpsLimit)
}