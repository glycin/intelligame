package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import java.awt.Rectangle
import kotlin.math.roundToInt

class Tile(
    val position: Vec2,
    val width: Int,
    val height: Int,
) {
    val minX = position.x.roundToInt()
    val maxX = position.x.roundToInt() + width
    val minY = position.y.roundToInt()
    val maxY = position.y.roundToInt() + height
    val bounds = Rectangle(position.x.roundToInt(), position.y.roundToInt(), width, height)
}