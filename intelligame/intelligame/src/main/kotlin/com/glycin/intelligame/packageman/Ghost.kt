package com.glycin.intelligame.packageman

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics2D

class Ghost(
    val position: Vec2,
    val width: Int,
    val height: Int,
    val cellX: Int,
    val cellY: Int,
    private val color: Color,
) {
    private val eyeWidth: Int = 2
    private val eyeHeight: Int = 3
    private val eyeXOffset: Int = 2
    private val eyeYOffset: Int = 2

    fun render(g: Graphics2D) {
        g.color = color
        g.fillRoundRect(position.x, position.y, width, height - 5, 5, 5)

        val xPoints = arrayOf(position.x, position.x + 2, position.x + 5, position.x + 7, position.x + width).toIntArray()
        val yPoints = arrayOf(
            position.y + height - 5,
            position.y + height,
            position.y + height - 5,
            position.y + height,
            position.y + height - 5,
        ).toIntArray()

        g.fillPolygon(xPoints, yPoints, xPoints.size)
        g.color = JBColor.WHITE.brighter().brighter().brighter().brighter()
        g.fillOval(position.x + eyeXOffset, position.y + eyeYOffset, eyeWidth, eyeHeight);
        g.fillOval(position.x + width - eyeXOffset - eyeWidth, position.y + eyeYOffset, eyeWidth, eyeHeight);
    }
}