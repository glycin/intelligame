package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import java.awt.Graphics2D
import java.awt.Image

class SuccessEffect(
    private val position: Vec2,
    private val sprites: List<Image>,
) {
    var shown = false
    private var currentIndex = 0
    
    fun draw(g: Graphics2D) {
        val sprite = sprites[currentIndex]
        g.drawImage(sprite, position.x, position.y, sprite.getWidth(null), sprite.getHeight(null), null)
        currentIndex++
        if(currentIndex >= sprites.size - 1) {
            shown = true
        }
    }
}