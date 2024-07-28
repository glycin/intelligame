package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.intellij.psi.PsiMethod
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class Coin(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val method: PsiMethod,
    private val sprites: List<BufferedImage>,
){
    var bounds = Rectangle(position.x, position.y, width, height)
    var pickedUp = false
    var dropped = false
    var toBeRemoved = false

    private var currentIndex = 0
    private var frameHoldCount: Int = 0
    private var dropStartPosition = Vec2.zero
    private var dropTargetPosition = Vec2.zero
    private var dimAlpha = false
    private var curDropFrame = 0
    private val totalDropFrames = 360

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
    }

    fun drawDrop(g: Graphics2D) {
        curDropFrame++
        val t = curDropFrame.toDouble() / totalDropFrames
        val x = (position.x + t * (dropTargetPosition.x - dropStartPosition.x)).roundToInt()
        val y = (position.y + t * (dropTargetPosition.y - dropStartPosition.y)).roundToInt()

        if(curDropFrame % 20 == 0) {
            dimAlpha = !dimAlpha
        }
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, if(dimAlpha) 0.5f else 1.0f)

        val sprite = sprites[4]
        g.drawImage(sprite, x, y, width, height, null)

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        if(curDropFrame > totalDropFrames) {
            dropped = false
            pickedUp = false
            toBeRemoved = true
            position = Vec2(-50000, -50000)
            bounds = Rectangle(position.x, position.y, width, height)
        }
    }

    fun pickUp() {
        pickedUp = true
        position = Vec2(-50000, -50000)
        bounds = Rectangle(position.x, position.y, width, height)
    }

    fun loseCoin(dropPosition: Vec2, targetPosition: Vec2) {
        dropped = true
        position = dropPosition
        dropStartPosition = position
        dropTargetPosition = targetPosition
    }
}