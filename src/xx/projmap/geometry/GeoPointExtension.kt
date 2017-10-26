package xx.projmap.geometry

import org.ejml.simple.SimpleMatrix

fun GeoPoint.toExtendedMatrix(z: Double = 1.0) = SimpleMatrix(3, 1, true, x, y, z)

