package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import java.awt.Graphics2D
import java.awt.Image

class AnimatedEffect(
    private val position: Vec2,
    private val sprites: List<Image>,
    private val scaleMultiplier: Int = 2
) {
    private val spriteWidth = sprites[0].getWidth(null)
    private val spriteHeight = sprites[0].getHeight(null)

    var shown = false
    private var currentIndex = 0

    fun draw(g: Graphics2D) {
        val sprite = sprites[currentIndex]
        g.drawImage(sprite, position.x - (spriteWidth / 2), position.y - (spriteHeight / 2), spriteWidth * scaleMultiplier, spriteHeight * scaleMultiplier, null)
        currentIndex++
        if(currentIndex >= sprites.size - 1) {
            shown = true
        }
    }
}