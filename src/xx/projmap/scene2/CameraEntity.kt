package xx.projmap.scene2

import xx.projmap.geometry.GeoRect
import xx.projmap.geometry.IdentityTransform
import xx.projmap.geometry.Transform
import xx.projmap.scene.Viewport

class CameraEntity(region: GeoRect, viewport: Viewport, transform: Transform = IdentityTransform()) : Entity() {

    val camera: Camera = Camera(region, viewport, transform)

    init {
        addComponent(camera)
    }

}