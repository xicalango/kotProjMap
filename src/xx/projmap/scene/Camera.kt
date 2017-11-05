package xx.projmap.scene

import xx.projmap.geometry.*

class Camera(region: GeoRect, val viewport: Viewport, private val transform: Transform = IdentityTransform(), val id: String? = null) {

    private val graphicsAdapter = viewport.graphicsAdapter

    val region: MutRect = region.toMutable()

    var visible = true

    fun render(world: World) {
        assert(visible)

        viewport.initialize()

        graphicsAdapter.push()
        graphicsAdapter.translate(viewport.region.x - region.x, viewport.region.y - region.y)
        graphicsAdapter.scale(viewport.region.w / region.w, viewport.region.h / region.h)

        world.entities.filter { it.visible }.forEach { it.render(graphicsAdapter, transform) }

        graphicsAdapter.pop()

        viewport.finish()
    }

    fun viewportToCamera(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint =
            viewport.region.transformTo(region, srcPoint, dstPoint)

    fun viewportToWorld(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint {
        val cameraPoint = viewportToCamera(srcPoint, dstPoint)
        return transform.dstToSrc(cameraPoint, cameraPoint)
    }

}