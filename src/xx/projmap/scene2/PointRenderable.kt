package xx.projmap.scene2

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Transform
import xx.projmap.scene.GraphicsAdapter

class PointRenderable : Renderable() {

    private val transformedPoint = MutPoint()

    override fun renderInternal(graphicsAdapter: GraphicsAdapter, transform: Transform) {
        transform.srcToDst(entity.origin.origin, transformedPoint)
        graphicsAdapter.drawPoint(transformedPoint)
    }

}