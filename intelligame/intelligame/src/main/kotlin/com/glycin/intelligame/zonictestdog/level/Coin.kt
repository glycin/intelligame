package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.intellij.psi.PsiMethod
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import java.awt.Rectangle

class Coin(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val method: PsiMethod
){
    val bounds = Rectangle(position.x, position.y, width, height)
    var pickedUp = false

    fun draw(g: Graphics2D) {
        g.color = JBColor.YELLOW
        g.fillOval(position.x, position.y, width, height)
    }

    fun pickUp() {
        pickedUp = true
        position = Vec2(-50000, -50000)
    }
}