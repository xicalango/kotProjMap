package xx.projmap.scene2

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.IdentityTransform
import xx.projmap.geometry.Transform
import xx.projmap.graphics.RenderDestination

class CameraEntity(region: GeoRect, renderDestination: RenderDestination, transform: Transform = IdentityTransform()) : Entity() {

    val camera: Camera = Camera(region, renderDestination, transform)

    init {
        addComponent(camera)
    }

}