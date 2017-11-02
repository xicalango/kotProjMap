package xx.projmap.scene

import xx.projmap.geometry.MutRect
import xx.projmap.geometry.toPointArray

class Viewport(val region: MutRect, val graphicsAdapter: GraphicsAdapter) {
    private fun clear() {
        graphicsAdapter.clear(region.x, region.y, region.w, region.h)
    }

    private fun drawBorder() {
        graphicsAdapter.drawPointArray(region.toPointArray(), DrawStyle.LINE)
    }

    fun initialize() {
        clear()
        drawBorder()
        graphicsAdapter.clip(region.x, region.y, region.w, region.h)
    }

    fun finish() {
        graphicsAdapter.resetClip()
    }
}