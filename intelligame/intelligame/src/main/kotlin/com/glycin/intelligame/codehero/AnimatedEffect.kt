package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import java.awt.Graphics2D
import java.awt.Image
import kotlin.math.roundToInt

class AnimatedEffect(
    private val position: Vec2,
    private val sprites: List<Image>,
    private val scaleMultiplier: Int = 2,
    private var frameHoldCount: Int = 4
) {
    private val spriteWidth = sprites[0].getWidth(null)
    private val spriteHeight = sprites[0].getHeight(null)

    var shown = false
    private var currentIndex = 0
    fun draw(g: Graphics2D) {
        val sprite = sprites[currentIndex]
        g.drawImage(sprite, position.x.roundToInt() - (spriteWidth / 2), position.y.roundToInt() - (spriteHeight / 2), spriteWidth * scaleMultiplier, spriteHeight * scaleMultiplier, null)
        frameHoldCount++

        if(frameHoldCount % 4 == 0) {
            currentIndex++
        }

        if(currentIndex >= sprites.size - 1) {
            shown = true
        }
    }
}