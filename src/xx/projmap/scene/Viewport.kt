package xx.projmap.scene

import xx.projmap.geometry.MutRect
import xx.projmap.geometry.toPointArray

class Viewport(val region: MutRect, val graphicsAdapter: GraphicsAdapter) {
    var drawBorder: Boolean = true

    private fun clear() {
        graphicsAdapter.clear(region.x, region.y, region.w, region.h)
    }

    fun initialize() {
        clear()
        if (drawBorder) {
            graphicsAdapter.drawPointArray(region.toPointArray(), DrawStyle.LINE)
        }
        graphicsAdapter.clip(region.x, region.y, region.w, region.h)
    }

    fun finish() {
        graphicsAdapter.resetClip()
    }
}