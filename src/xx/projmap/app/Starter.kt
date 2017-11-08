package xx.projmap.app

import xx.projmap.events.EventQueue
import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.Point
import xx.projmap.geometry.Rect
import xx.projmap.scene.Scene
import xx.projmap.simulation.api.Simulation
import xx.projmap.simulation.api.SimulationStateManager
import xx.projmap.simulation.impl.CalibrationState
import xx.projmap.simulation.impl.KeyEditingState
import xx.projmap.swing.ProjectionFrame
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.JFrame
import kotlin.collections.ArrayList

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

    val properties = loadProperties()

    val graphicsFpsLimit = properties.getProperty(GRAPHICS_FPS_LIMIT_KEY).toInt()
    val simulationFpsLimit = properties.getProperty(SIMULATION_FPS_LIMIT_KEY).toInt()

    val keys = loadKeys()
    val calibrationPoints = loadCalibration()

    runSimulation(graphicsFpsLimit, simulationFpsLimit, keys, calibrationPoints)

    System.exit(0)
}


private fun runSimulation(graphicsFpsLimit: Int, simulationFpsLimit: Int, keys: List<GeoRect>?, calibrationPoints: Array<Point>?): Simulation {
    val eventQueue = EventQueue()
    val scene = Scene(eventQueue)

    val stateManager = SimulationStateManager(scene)
    val calibrationState = CalibrationState(stateManager, scene, calibrationPoints)
    val keyEditingState = KeyEditingState(stateManager, scene, keys)

    stateManager.addState(calibrationState)
    stateManager.addState(keyEditingState)

    val simulation = Simulation(stateManager, "calibration", graphicsFpsLimit = graphicsFpsLimit, simulationFpsLimit = simulationFpsLimit)
    val frame = ProjectionFrame(eventQueue)
    val viewport2 = frame.mainViewport.createSubViewport(Rect(0.0, 0.0, 200.0, 150.0))



    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.showFrame()

    simulation.run(frame.mainViewport, mapOf(Pair("debug", viewport2)))
    return simulation
}

private fun loadProperties(): Properties {
    val properties = Properties(defaultProperties)

    val propertiesFile = Paths.get("kotProjMap.properties")
    if (Files.exists(propertiesFile)) {
        Files.newInputStream(propertiesFile).use(properties::load)
    }
    return properties
}

private fun loadKeys(): List<GeoRect>? {
    val keyPropertiesFile = Paths.get("keysFile.properties")
    if (Files.exists(keyPropertiesFile).not()) {
        return null
    }

    val properties = Properties()
    Files.newInputStream(keyPropertiesFile).use(properties::load)

    val keys: MutableList<GeoRect> = ArrayList()

    properties.forEach { _, propObj ->
        val propString = propObj as? String ?: return@forEach

        val keyData = propString.split(",", limit = 4).map { it.toDouble() }

        keys += Rect(keyData[0], keyData[1], keyData[2], keyData[3])
    }

    return keys
}

fun loadCalibration(): Array<Point>? {
    val pointPropertiesFile = Paths.get("calibrationPoints.properties")
    if (Files.exists(pointPropertiesFile).not()) {
        return null
    }

    val properties = Properties()
    Files.newInputStream(pointPropertiesFile).use(properties::load)

    val points: MutableList<Point> = ArrayList()

    properties.forEach { _, propObj ->
        val propString = propObj as? String ?: return@forEach

        val pointData = propString.split(",", limit = 2).map { it.toDouble() }

        points += Point(pointData[0], pointData[1])
    }

    return points.toTypedArray()
}

