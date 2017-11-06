package xx.projmap.app

import xx.projmap.geometry.Rect
import xx.projmap.scene.RectEntity
import xx.projmap.simulation.api.Simulation
import xx.projmap.simulation.impl.CalibrationState
import xx.projmap.simulation.impl.MainState
import xx.projmap.swing.ProjectionFrame
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.JFrame

const val GRAPHICS_FPS_LIMIT_KEY = "graphicsFpsLimit"
const val SIMULATION_FPS_LIMIT_KEY = "graphicsFpsLimit"

private val defaultProperties: Properties
    get() {
        val properties = Properties()
        properties.setProperty(GRAPHICS_FPS_LIMIT_KEY, "60")
        properties.setProperty(SIMULATION_FPS_LIMIT_KEY, "100")
        return properties
    }

fun main(args: Array<String>) {

    val properties = Properties(defaultProperties)

    val propertiesFile = Paths.get("kotProjMap.properties")
    if (Files.exists(propertiesFile)) {
        val inputStream = Files.newInputStream(propertiesFile)
        properties.load(inputStream)
    }

    val graphicsFpsLimit = properties.getProperty(GRAPHICS_FPS_LIMIT_KEY).toInt()
    val simulationFpsLimit = properties.getProperty(SIMULATION_FPS_LIMIT_KEY).toInt()

    val simulation = Simulation(listOf(::CalibrationState, ::MainState), "calibration", graphicsFpsLimit = graphicsFpsLimit, simulationFpsLimit = simulationFpsLimit)
    val frame = ProjectionFrame(simulation.eventQueue)
    val viewport2 = frame.mainViewport.createSubViewport(Rect(0.0, 0.0, 200.0, 150.0))

    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.showFrame()

    simulation.run(frame.mainViewport, mapOf(Pair("debug", viewport2)))

    simulation.scene.world["key"].forEach { keyEntity ->
        val entity = keyEntity as RectEntity
        println("${entity.translatedRect}")
    }

    System.exit(0)
}

