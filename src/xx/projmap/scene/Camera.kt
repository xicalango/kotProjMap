package xx.projmap.scene

import xx.projmap.geometry.GeoRect

class Camera(val region: GeoRect, val viewport: Viewport, val transform: Transform? = null) {

    private val graphicsAdapter = viewport.graphicsAdapter

    fun render(world: World) {
        viewport.clear()

        graphicsAdapter.push()
        graphicsAdapter.translate(viewport.region.x - region.x, viewport.region.y - region.y)
        graphicsAdapter.scale(viewport.region.w / region.w, viewport.region.h / region.h)

        world.entities.forEach { it.render(graphicsAdapter, transform) }

        graphicsAdapter.pop()
    }

}