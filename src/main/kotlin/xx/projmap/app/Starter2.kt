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

    val configFileName = args.getOrElse(0, { CONFIG_FILE_NAME })
    val config = loadProperties(configFileName)

    val simulation = Simulation(config = config)

    val frame = ProjectionFrame(simulation.eventQueue)

    simulation.scene.createEntity(::KeyboardEntity)
    simulation.scene.createEntity(::StateManager)
    val unitRect = frame.projectionPanel.region.toNormalized()
    simulation.scene.createCamera(unitRect, frame.projectionPanel, name = "mainCamera")
    val subRenderDestination = frame.projectionPanel.createSubRenderDestination(Rect(0.0, 0.0, 150.0, 60.0))
    simulation.scene.createCamera(Rect(0.0, 0.0, 500.0, 200.0), subRenderDestination, name = "debugCamera")

    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.isVisible = true

    simulation.run(frame.projectionPanel)

    storeProperties(configFileName, config)

    System.exit(0)
}
