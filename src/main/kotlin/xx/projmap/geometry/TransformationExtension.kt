package xx.projmap.geometry

import xx.projmap.geometry.TransformationDirection.SRC_TO_DST

fun Transformation.transformQuad(quad: Quad, direction: TransformationDirection = SRC_TO_DST): Quad {
    val pointArray = quad.toPointArray()

    val dstPointArray = Array(4, { MutPoint() })

    pointArray.zip(dstPointArray).map { (srcPoint, dstPoint) ->
        transform(srcPoint, direction, dstPoint)
    }

    return createQuadFromPoints(dstPointArray)
}
