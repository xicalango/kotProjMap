package xx.projmap.scene2

import xx.projmap.geometry.*
import xx.projmap.graphics.DrawStyle
import xx.projmap.graphics.GraphicsAdapter
import xx.projmap.graphics.RenderDestination
import xx.projmap.graphics.withColor
import java.awt.Color

class Camera(region: GeoRect, private val renderDestination: RenderDestination, var transform: Transform = IdentityTransform) : Component() {

    val region: MutRect = region.toMutable()

    val renderRegion
        get() = renderDestination.region

    var drawBorder: Boolean = true

    fun render(graphicsAdapter: GraphicsAdapter) {
        startFrame(graphicsAdapter)

        try {
            synchronized(sceneFacade.entities) {
                sceneFacade.entities.forEach { it.render(graphicsAdapter, transform) }
            }
        } finally {
            finishFrame(graphicsAdapter)
        }
    }

    private fun startFrame(graphicsAdapter: GraphicsAdapter) {
        val cameraRegion = region + entity.origin
        graphicsAdapter::clear.callWithRect(renderRegion)
        if (drawBorder) {
            graphicsAdapter.withColor(Color.WHITE) {
                drawGeoEntity(renderRegion, DrawStyle.LINE)
            }
        }
        graphicsAdapter::clip.callWithRect(renderRegion)

        graphicsAdapter.push()
        graphicsAdapter.translate(renderRegion.x - cameraRegion.x, renderRegion.y - cameraRegion.y)
        graphicsAdapter.scale(renderRegion.w / cameraRegion.w, renderRegion.h / cameraRegion.h)
    }

    private fun finishFrame(graphicsAdapter: GraphicsAdapter) {
        graphicsAdapter.pop()

        graphicsAdapter.resetClip()
    }

    fun viewportToCamera(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint =
            renderDestination.region.transformTo(region, srcPoint, dstPoint)

    fun viewportToWorld(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()): MutPoint {
        val cameraPoint = viewportToCamera(srcPoint, dstPoint)
        return transform.dstToSrc(cameraPoint, cameraPoint)
    }
}