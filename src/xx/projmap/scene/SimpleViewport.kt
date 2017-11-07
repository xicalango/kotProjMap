package xx.projmap.scene

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.MutRect
import xx.projmap.geometry.toPointArray

interface Viewport {
    val graphicsAdapter: GraphicsAdapter
    var drawBorder: Boolean

    val region: GeoRect

    fun render(scene: Scene)
    fun clear()

    fun initialize() {
        clear()
        if (drawBorder) {
            graphicsAdapter.drawGeoEntity(region, DrawStyle.LINE)
        }
        graphicsAdapter.clip(region.x, region.y, region.w, region.h)
    }

    fun finish() {
        graphicsAdapter.resetClip()
    }

    fun createSubViewport(subRegion: GeoRect): SimpleViewport {
        assert(subRegion in region)
        return SimpleViewport(subRegion.toMutable(), graphicsAdapter)
    }
}

class SimpleViewport(override val region: MutRect, override val graphicsAdapter: GraphicsAdapter) : Viewport {
    override fun render(scene: Scene) {
    }

    override var drawBorder: Boolean = true

    override fun clear() {
        graphicsAdapter.clear(region.x, region.y, region.w, region.h)
    }

    override fun initialize() {
        clear()
        if (drawBorder) {
            graphicsAdapter.drawPointArray(region.toPointArray(), DrawStyle.LINE)
        }
        graphicsAdapter.clip(region.x, region.y, region.w, region.h)
    }

    override fun finish() {
        graphicsAdapter.resetClip()
    }
}