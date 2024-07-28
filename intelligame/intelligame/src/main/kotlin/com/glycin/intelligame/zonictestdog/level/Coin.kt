package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.intellij.psi.PsiMethod
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage

class Coin(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val method: PsiMethod,
    val sprites: List<BufferedImage>
){
    val bounds = Rectangle(position.x, position.y, width, height)
    var pickedUp = false

    private var currentIndex = 0
    private var frameHoldCount: Int = 0

    fun draw(g: Graphics2D) {
        val sprite = sprites[currentIndex]
        g.drawImage(sprite, position.x, position.y, width, height, null)
        frameHoldCount++

        if(frameHoldCount % 12 == 0) {
            currentIndex++
        }

        if(currentIndex >= sprites.size) {
            currentIndex = 0
            frameHoldCount = 0
        }

        g.color = JBColor.WHITE.brighter().brighter().brighter()
        g.drawRect(position.x, position.y, width, height)
    }

    fun pickUp() {
        pickedUp = true
        position = Vec2(-50000, -50000)
    }
}