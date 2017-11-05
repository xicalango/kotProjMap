package xx.projmap.scene

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform

class PolygonEntity(origin: MutPoint, vararg points: GeoPoint) : Entity(origin) {

    private val polygon: Array<GeoPoint> = arrayOf<GeoPoint>(origin) + points
    private val dstPolygon: Array<MutPoint> = Array(points.size + 1, { MutPoint() })

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        polygon.forEachIndexed { index, point ->
            transform.srcToDst(point.translated(origin), dstPolygon[index])
        }
        graphicsAdapter.drawPointArray(dstPolygon)
    }

}