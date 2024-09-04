package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.shared.Vec2
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class VelocitnikLaser(
    private var position: Vec2,
    private val width: Int,
    private val height: Int,
) {
    private val laserSprites = arrayOfNulls<BufferedImage>(15)
    private val frameDelay = 10
    private var currentAnimationIndex = 0
    private var skipFrameCount = 0
    private var currentSprite : BufferedImage

    init {
        val spriteLoader = SpriteSheetImageLoader(
            spriteSheetPath = "/Sprites/sheets/laser.png",
            cellWidth = 300,
            cellHeight = 100,
            numSprites = 15,
        )

        val sprites = spriteLoader.loadSprites()
        sprites.forEachIndexed { index, bufferedImage -> laserSprites[index] = bufferedImage }
        currentSprite = laserSprites[0]!!
    }

    fun draw(g: Graphics2D) {
        update()
        g.drawImage(currentSprite, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
    }

    private fun update() {
        doAnimation()
    }

    private fun doAnimation() {
        skipFrameCount++
        if(skipFrameCount % frameDelay == 0) {
            currentAnimationIndex++
        }

        if(currentAnimationIndex >= laserSprites.size - 1) {
            currentAnimationIndex = 0
            skipFrameCount = 0
        }
        currentSprite = laserSprites[currentAnimationIndex]!!
    }
}