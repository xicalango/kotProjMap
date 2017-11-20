package xx.projmap.graphics

import xx.projmap.geometry.MutPoint
import xx.projmap.geometry.Point
import kotlin.experimental.and

// taken from https://hackaday.io/project/6309-vga-graphics-over-spi-and-serial-vgatonic/log/20759-a-tiny-4x6-pixel-font-that-will-fit-on-almost-any-microcontroller-license-mit
// Font Definition
private val font4x6: Array<IntArray> = arrayOf(
        intArrayOf(0x00, 0x00),   /*SPACE*/
        intArrayOf(0x49, 0x08),   /*'!'*/
        intArrayOf(0xb4, 0x00),   /*'"'*/
        intArrayOf(0xbe, 0xf6),   /*'#'*/
        intArrayOf(0x7b, 0x7a),   /*'$'*/
        intArrayOf(0xa5, 0x94),   /*'%'*/
        intArrayOf(0x55, 0xb8),   /*'&'*/
        intArrayOf(0x48, 0x00),   /*'''*/
        intArrayOf(0x29, 0x44),   /*'('*/
        intArrayOf(0x44, 0x2a),   /*')'*/
        intArrayOf(0x15, 0xa0),   /*'*'*/
        intArrayOf(0x0b, 0x42),   /*'+'*/
        intArrayOf(0x00, 0x50),   /*','*/
        intArrayOf(0x03, 0x02),   /*'-'*/
        intArrayOf(0x00, 0x08),   /*'.'*/
        intArrayOf(0x25, 0x90),   /*'/'*/
        intArrayOf(0x76, 0xba),   /*'0'*/
        intArrayOf(0x59, 0x5c),   /*'1'*/
        intArrayOf(0xc5, 0x9e),   /*'2'*/
        intArrayOf(0xc5, 0x38),   /*'3'*/
        intArrayOf(0x92, 0xe6),   /*'4'*/
        intArrayOf(0xf3, 0x3a),   /*'5'*/
        intArrayOf(0x73, 0xba),   /*'6'*/
        intArrayOf(0xe5, 0x90),   /*'7'*/
        intArrayOf(0x77, 0xba),   /*'8'*/
        intArrayOf(0x77, 0x3a),   /*'9'*/
        intArrayOf(0x08, 0x40),   /*':'*/
        intArrayOf(0x08, 0x50),   /*';'*/
        intArrayOf(0x2a, 0x44),   /*'<'*/
        intArrayOf(0x1c, 0xe0),   /*'='*/
        intArrayOf(0x88, 0x52),   /*'>'*/
        intArrayOf(0xe5, 0x08),   /*'?'*/
        intArrayOf(0x56, 0x8e),   /*'@'*/
        intArrayOf(0x77, 0xb6),   /*'A'*/
        intArrayOf(0x77, 0xb8),   /*'B'*/
        intArrayOf(0x72, 0x8c),   /*'C'*/
        intArrayOf(0xd6, 0xba),   /*'D'*/
        intArrayOf(0x73, 0x9e),   /*'E'*/
        intArrayOf(0x73, 0x92),   /*'F'*/
        intArrayOf(0x72, 0xae),   /*'G'*/
        intArrayOf(0xb7, 0xb6),   /*'H'*/
        intArrayOf(0xe9, 0x5c),   /*'I'*/
        intArrayOf(0x64, 0xaa),   /*'J'*/
        intArrayOf(0xb7, 0xb4),   /*'K'*/
        intArrayOf(0x92, 0x9c),   /*'L'*/
        intArrayOf(0xbe, 0xb6),   /*'M'*/
        intArrayOf(0xd6, 0xb6),   /*'N'*/
        intArrayOf(0x56, 0xaa),   /*'O'*/
        intArrayOf(0xd7, 0x92),   /*'P'*/
        intArrayOf(0x76, 0xee),   /*'Q'*/
        intArrayOf(0x77, 0xb4),   /*'R'*/
        intArrayOf(0x71, 0x38),   /*'S'*/
        intArrayOf(0xe9, 0x48),   /*'T'*/
        intArrayOf(0xb6, 0xae),   /*'U'*/
        intArrayOf(0xb6, 0xaa),   /*'V'*/
        intArrayOf(0xb6, 0xf6),   /*'W'*/
        intArrayOf(0xb5, 0xb4),   /*'X'*/
        intArrayOf(0xb5, 0x48),   /*'Y'*/
        intArrayOf(0xe5, 0x9c),   /*'Z'*/
        intArrayOf(0x69, 0x4c),   /*'['*/
        intArrayOf(0x91, 0x24),   /*'\'*/
        intArrayOf(0x64, 0x2e),   /*']'*/
        intArrayOf(0x54, 0x00),   /*'^'*/
        intArrayOf(0x00, 0x1c),   /*'_'*/
        intArrayOf(0x44, 0x00),   /*'`'*/
        intArrayOf(0x0e, 0xae),   /*'a'*/
        intArrayOf(0x9a, 0xba),   /*'b'*/
        intArrayOf(0x0e, 0x8c),   /*'c'*/
        intArrayOf(0x2e, 0xae),   /*'d'*/
        intArrayOf(0x0e, 0xce),   /*'e'*/
        intArrayOf(0x56, 0xd0),   /*'f'*/
        intArrayOf(0x55, 0x3B),   /*'g'*/
        intArrayOf(0x93, 0xb4),   /*'h'*/
        intArrayOf(0x41, 0x44),   /*'i'*/
        intArrayOf(0x41, 0x51),   /*'j'*/
        intArrayOf(0x97, 0xb4),   /*'k'*/
        intArrayOf(0x49, 0x44),   /*'l'*/
        intArrayOf(0x17, 0xb6),   /*'m'*/
        intArrayOf(0x1a, 0xb6),   /*'n'*/
        intArrayOf(0x0a, 0xaa),   /*'o'*/
        intArrayOf(0xd6, 0xd3),   /*'p'*/
        intArrayOf(0x76, 0x67),   /*'q'*/
        intArrayOf(0x17, 0x90),   /*'r'*/
        intArrayOf(0x0f, 0x38),   /*'s'*/
        intArrayOf(0x9a, 0x8c),   /*'t'*/
        intArrayOf(0x16, 0xae),   /*'u'*/
        intArrayOf(0x16, 0xba),   /*'v'*/
        intArrayOf(0x16, 0xf6),   /*'w'*/
        intArrayOf(0x15, 0xb4),   /*'x'*/
        intArrayOf(0xb5, 0x2b),   /*'y'*/
        intArrayOf(0x1c, 0x5e),   /*'z'*/
        intArrayOf(0x6b, 0x4c),   /*'{'*/
        intArrayOf(0x49, 0x48),   /*'|'*/
        intArrayOf(0xc9, 0x5a),   /*')'*/
        intArrayOf(0x54, 0x00),   /*'~'*/
        intArrayOf(0x56, 0xe2)    /*''*/
)

private fun getFontLine(char: Char, lineNum: Int): Byte {
    val index = (char - 32).toInt()
    if (index < 0 || index >= font4x6.size) {
        return 0
    }

    var num = lineNum
    if (font4x6[index][1] and 1 == 1) {
        num -= 1
    }

    val pixel = when (num) {
        -1, 5 -> 0
        0 -> (font4x6[index][0]) ushr 4
        1 -> (font4x6[index][0]) ushr 1
        3 -> font4x6[index][1] ushr 4
        4 -> font4x6[index][1] ushr 1
        2 -> {
            // Split over 2 bytes
            val byteOne = (font4x6[index][0] and 0x03) shl 2
            val byteTwo = font4x6[index][1] and 0x02
            byteOne or byteTwo
        }
        else -> throw IllegalArgumentException("$num")
    }

    return (pixel and 0xE).toByte()
}

fun render4x6ToLambda(text: String, x: Double = 0.0, y: Double = 0.0, xPointSpacing: Double = 10.0, yPointSpacing: Double = 10.0, xLetterSpacingFactor: Double = 4.0, callback: (Double, Double) -> Unit) {
    text.forEachIndexed { index, char ->
        (0..5).forEach { line ->
            val fontLine = getFontLine(char, line)
            (3 downTo 0).map { (1 shl it).toByte() }.forEachIndexed { pixelIndex, pixelPos ->
                if (fontLine and pixelPos == pixelPos) {
                    val px = x + ((index * xPointSpacing * xLetterSpacingFactor) + pixelIndex * xPointSpacing)
                    val py = y + (line * yPointSpacing)
                    callback(px, py)
                }
            }
        }
    }
}

fun render4x6ToPoints(text: String, x: Double = 0.0, y: Double = 0.0, xPointSpacing: Double = 10.0, yPointSpacing: Double = 10.0, xLetterSpacingFactor: Double = 4.0): List<Point> {
    val list = ArrayList<Point>()
    render4x6ToLambda(text, x, y, xPointSpacing, yPointSpacing, xLetterSpacingFactor, { px, py ->
        list += Point(px, py)
    })
    return list
}

fun GraphicsAdapter.render4x6(x: Double, y: Double, text: String, xPointSpacing: Double = 10.0, yPointSpacing: Double = 10.0, xLetterSpacingFactor: Double = 4.0) {
    val mutPoint = MutPoint()
    render4x6ToLambda(text, x, y, xPointSpacing, yPointSpacing, xLetterSpacingFactor, { px, py ->
        mutPoint.x = px
        mutPoint.y = py
        drawPoint(mutPoint)
    })
}
