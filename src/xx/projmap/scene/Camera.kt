package xx.projmap.scene

import xx.projmap.geometry.*

class Camera(val region: GeoRect, val viewport: Viewport, val transform: Transform? = null) {

    private val graphicsAdapter = viewport.graphicsAdapter

    fun render(world: World) {
        viewport.clear()

        graphicsAdapter.push()
        graphicsAdapter.translate(viewport.region.x - region.x, viewport.region.y - region.y)
        graphicsAdapter.scale(viewport.region.w / region.w, viewport.region.h / region.h)

        world.entities.forEach(this::render)

        graphicsAdapter.pop()
    }

    private fun render(entity: Entity) {
        when (entity) {
            is PointEntity -> render(entity)
            is RectEntity -> render(entity)
        }
    }

    private fun render(entity: RectEntity) {
        val srcPointArray = Rect(entity.origin.x, entity.origin.y, entity.w, entity.h).toQuad().toPointArray()
        val dstPointArray = srcPointArray.map(Point::toMutable).toTypedArray()

        if (transform != null) {
            srcPointArray.mapIndexed { index, point ->
                transform.srcToDst(point, dstPointArray[index])
            }
        }

        graphicsAdapter.drawPointArray(dstPointArray)
    }

    private fun render(entity: PointEntity) {
        val point = entity.origin.copy()

        transform?.srcToDst(entity.origin, point)

        graphicsAdapter.drawPoint(point)
    }

}