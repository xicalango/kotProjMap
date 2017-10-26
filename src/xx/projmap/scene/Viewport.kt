package xx.projmap.scene

import xx.projmap.geometry.MutRect

class Viewport(val region: MutRect, val graphicsAdapter: GraphicsAdapter) {
    fun clear() {
        graphicsAdapter.clear(region.x, region.y, region.w, region.h)
    }
}