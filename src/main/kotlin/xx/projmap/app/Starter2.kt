package xx.projmap.app

import okio.Okio
import xx.projmap.geometry.Rect
import xx.projmap.graphics.createSubRenderDestination
import xx.projmap.scene2.Simulation
import xx.projmap.scene2.createCamera
import xx.projmap.swing.ProjectionFrame
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.swing.JFrame

private const val CONFIG_FILE_NAME = "appConfig.json"

fun main(args: Array<String>) {
    val configPath = Paths.get(args.getOrElse(0, { CONFIG_FILE_NAME }))
    val config = loadAppConfig(configPath)

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

    storeAppConfig(configPath, config)

    System.exit(0)
}

fun storeAppConfig(configPath: Path, config: AppConfig) {
    Okio.buffer(Okio.sink(Files.newOutputStream(configPath))).use {
        appConfigAdapter.toJson(it, config)
    }
}

private fun loadAppConfig(configPath: Path): AppConfig {
    return if (Files.exists(configPath)) {
        Okio.buffer(Okio.source(Files.newInputStream(configPath))).use(appConfigAdapter::fromJson)!!
    } else {
        AppConfig()
    }
}
