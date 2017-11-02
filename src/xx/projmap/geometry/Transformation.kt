package xx.projmap.geometry

import org.ejml.simple.SimpleMatrix
import xx.projmap.geometry.TransformationDirection.DST_TO_SRC
import xx.projmap.geometry.TransformationDirection.SRC_TO_DST

enum class TransformationDirection {
    SRC_TO_DST,
    DST_TO_SRC
}

class Transformation(srcQuad: GeoQuad, dstQuad: GeoQuad) {

    private val transformationMatrix: SimpleMatrix
    private val reverseMatrix: SimpleMatrix

    init {
        val srcMatrix = srcQuad.toProjectionMatrix()
        val srcMatrixInv = srcMatrix.invert()

        val dstMatrix = dstQuad.toProjectionMatrix()
        val dstMatrixInv = dstMatrix.invert()

        transformationMatrix = dstMatrix.mult(srcMatrixInv)
        reverseMatrix = srcMatrix.mult(dstMatrixInv)
    }

    private fun transform(src: GeoPoint, matrix: SimpleMatrix, dst: MutPoint): MutPoint {
        val extendedMatrix = src.toExtendedMatrix()
        val transformedMatrix = matrix.mult(extendedMatrix)

        dst.x = transformedMatrix[0, 0] / transformedMatrix[2, 0]
        dst.y = transformedMatrix[1, 0] / transformedMatrix[2, 0]

        return dst
    }

    private fun getTransformationMatrix(direction: TransformationDirection) = when(direction) {
        SRC_TO_DST -> transformationMatrix
        DST_TO_SRC -> reverseMatrix
    }

    fun transform(srcPoint: GeoPoint, direction: TransformationDirection, dstPoint: MutPoint = MutPoint()) : MutPoint {
        val matrix = getTransformationMatrix(direction)
        return transform(srcPoint, matrix, dstPoint)
    }

    fun srcToDst(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()) = transform(srcPoint, transformationMatrix, dstPoint)
    fun dstToSrc(srcPoint: GeoPoint, dstPoint: MutPoint = MutPoint()) = transform(srcPoint, reverseMatrix, dstPoint)
}