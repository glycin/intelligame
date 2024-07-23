package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.intellij.psi.PsiMethod
import com.intellij.ui.JBColor
import java.awt.Graphics2D

class Coin(
    val position: Vec2,
    val width: Int,
    val height: Int,
    val method: PsiMethod
){

    fun draw(g: Graphics2D) {
        g.color = JBColor.YELLOW
        g.fillOval(position.x, position.y, width, height)
    }
}