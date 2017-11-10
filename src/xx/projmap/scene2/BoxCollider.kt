package xx.projmap.scene2

import xx.projmap.geometry.GeoPoint

class BoxCollider(private val renderable: Renderable) : Component() {

    fun collidesWith(point: GeoPoint): Boolean = point in boundingBox

    val boundingBox
        get() = renderable.boundingBox

}