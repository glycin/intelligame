package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor
import java.awt.Graphics2D

class SuccessEffect(
    private val position: Vec2,
    private val width: Int,
    private val height: Int,
) {
    var shown = false
    private val effectDuration = 50
    private var calls = 0

    fun draw(g: Graphics2D) {
        g.color = JBColor.green.brighter()
        g.fillOval(position.x, position.y, width, height)

        calls++
        if(calls >= effectDuration) {
            shown = true
        }
    }
}