package xx.projmap.graphics

import xx.projmap.geometry.GeoRect

interface RenderDestination {
    val region: GeoRect
}

fun GeoRect.toRenderDestination() = object : RenderDestination {
    override val region: GeoRect = this@toRenderDestination
}

fun RenderDestination.createSubViewport(rect: GeoRect) = object : RenderDestination {
    override val region: GeoRect = rect
}