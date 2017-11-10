package xx.projmap.app

import xx.projmap.geometry.Rect
import xx.projmap.graphics.createSubRenderDestination
import xx.projmap.scene2.Simulation
import xx.projmap.scene2.createCamera
import xx.projmap.swing.ProjectionFrame
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.JFrame

private const val CONFIG_FILE_NAME = "config.properties"

private fun loadProperties(fileName: String): Properties {
    val path = Paths.get(fileName)

    val properties = Properties()

    if (Files.exists(path)) {
        Files.newInputStream(path).use(properties::load)
    }

    println("loaded config: $properties")

    return properties
}

private fun storeProperties(fileName: String, properties: Properties) {
    val path = Paths.get(fileName)

    println("storing config: $properties")

    Files.newOutputStream(path).use { properties.store(it, fileName) }
}

fun main(args: Array<String>) {

    val config = loadProperties(CONFIG_FILE_NAME)

    val simulation = Simulation(config = config)

    val frame = ProjectionFrame(simulation.eventQueue)

    simulation.scene.createEntity(::StateManager)
    val unitRect = frame.projectionPanel.region.toNormalized()
    simulation.scene.createCamera(unitRect, frame.projectionPanel, name = "mainCamera")
    val subRenderDestination = frame.projectionPanel.createSubRenderDestination(Rect(0.0, 0.0, 100.0, 75.0))
    simulation.scene.createCamera(unitRect, subRenderDestination, name = "debugCamera")

    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.isVisible = true

    simulation.run(frame.projectionPanel)

    storeProperties(CONFIG_FILE_NAME, config)

    System.exit(0)
}
