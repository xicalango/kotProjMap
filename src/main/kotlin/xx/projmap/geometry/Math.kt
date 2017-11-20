package xx.projmap.geometry

fun proj2(srcStart: Double, srcEnd: Double, dstStart: Double, dstEnd: Double, value: Double): Double {
    val srcRange = srcEnd - srcStart
    val dstRange = dstEnd - dstStart

    return (((value - srcStart) / srcRange) * dstRange) + dstStart
}

fun scale2(srcRange: Double, dstRange: Double, value: Double) = (value / srcRange) * dstRange