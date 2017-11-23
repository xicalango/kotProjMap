package xx.projmap.app

import okio.Okio
import xx.projmap.geometry.Rect
import xx.projmap.getInputStream
import xx.projmap.graphics.createSubRenderDestination
import xx.projmap.scene2.Simulation
import xx.projmap.scene2.createCamera
import xx.projmap.swing.ProjectionFrame
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JFrame

private const val CONFIG_FILE_NAME = "appConfig.json"

fun main(args: Array<String>) {
    val configFilename = args.getOrElse(0, { CONFIG_FILE_NAME })
    val config = loadAppConfig(configFilename)

    val simulation = Simulation()

    val frame = ProjectionFrame(simulation.eventQueue)

    simulation.scene.createEntity({ AppConfigEntity(config) })
    simulation.scene.createEntity(::KeyboardEntity)
    simulation.scene.createEntity(::StateManager)
    val unitRect = frame.projectionPanel.region.toNormalized()
    simulation.scene.createCamera(unitRect, frame.projectionPanel, name = "mainCamera")
    val subRenderDestination = frame.projectionPanel.createSubRenderDestination(Rect(0.0, 0.0, 150.0, 60.0))
    simulation.scene.createCamera(Rect(0.0, 0.0, 500.0, 200.0), subRenderDestination, name = "debugCamera")

    frame.extendedState = JFrame.MAXIMIZED_BOTH
    frame.isVisible = true

    simulation.run(frame.projectionPanel)

    storeAppConfig(configFilename, config)

    System.exit(0)
}

private fun storeAppConfig(configFilename: String, config: AppConfig) {
    Okio.buffer(Okio.sink(Files.newOutputStream(Paths.get(configFilename)))).use {
        appConfigAdapter.toJson(it, config)
    }
}

private fun loadAppConfig(configFilename: String): AppConfig {
    return getInputStream(configFilename, AppConfig::class.java)
            ?.let { Okio.buffer(Okio.source(it)) }
            .use(appConfigAdapter::fromJson)
            ?: AppConfig()
}
