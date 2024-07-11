package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import java.awt.Rectangle

class Tile(
    val position: Vec2,
    val width: Int,
    val height: Int,
) {
    val minX = position.x
    val maxX = position.x + width
    val minY = position.y
    val maxY = position.y + height
    val bounds = Rectangle(position.x, position.y, width, height)
}