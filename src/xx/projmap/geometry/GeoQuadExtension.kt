package xx.projmap.geometry

import org.ejml.simple.SimpleMatrix

fun GeoQuad.toProjectionMatrix(): SimpleMatrix {
    val matrixA = SimpleMatrix(3, 3)
    (0 until 3).forEach {
        matrixA[0, it] = getX(it)
        matrixA[1, it] = getY(it)
        matrixA[2, it] = 1.0
    }

    val vecB = SimpleMatrix(3, 1)
    vecB[0, 0] = getX(3)
    vecB[1, 0] = getY(3)
    vecB[2, 0] = 1.0

    val vecX = matrixA.solve(vecB)

    val matrixX = SimpleMatrix(3, 3)
    (0 until 3).forEach { x ->
        (0 until 3).forEach { y ->
            matrixX[x, y] = vecX[y, 0]
        }
    }

    return matrixA.elementMult(matrixX)
}

