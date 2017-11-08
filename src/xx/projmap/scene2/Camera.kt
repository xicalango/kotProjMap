package xx.projmap.scene2

import xx.projmap.geometry.*
import xx.projmap.scene.GraphicsAdapter
import xx.projmap.scene.Viewport

class Camera(region: GeoRect, val viewport: Viewport, var transform: Transform = IdentityTransform()) : Component() {

    val region: MutRect = region.toMutable()

    fun render(graphicsAdapter: GraphicsAdapter) {
        val viewportRegion = viewport.region

        val translatedRegion = region + entity.origin

        viewport.initialize()

        graphicsAdapter.push()
        graphicsAdapter.translate(viewportRegion.x - translatedRegion.x, viewportRegion.y - translatedRegion.y)
        graphicsAdapter.scale(viewportRegion.w / translatedRegion.w, viewportRegion.h / translatedRegion.h)

        entity.sceneFacade.allEntities
                .flatMap { it.getComponentsByType<Renderable>() }
                .isEnabled()
                .forEach { it.render(graphicsAdapter, transform) }

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