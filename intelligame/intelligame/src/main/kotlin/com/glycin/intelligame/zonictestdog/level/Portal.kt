package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.zonictestdog.CollisionsManager
import com.glycin.intelligame.zonictestdog.ZtdGame
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import java.awt.Rectangle

class Portal(
    val position: Vec2,
    val height: Int,
    val width: Int,
    val file: PsiFile,
    val element: PsiElement,
    val textRange: TextRange,
    val cm: CollisionsManager,
    val ztdGame: ZtdGame,
) {
    private val bounds: Rectangle = Rectangle(position.x, position.y, width, height)

    fun drawPortal(g: Graphics2D) {
        g.color = JBColor.red.brighter()
        g.fillRect(position.x - (width / 2), position.y - (height / 2), width, height)
        checkIfZonicIsIn()
    }

    private fun checkIfZonicIsIn() {
        if(cm.isZonicInPortal(bounds)){
            ztdGame.travelTo(this)
        }
    }
}