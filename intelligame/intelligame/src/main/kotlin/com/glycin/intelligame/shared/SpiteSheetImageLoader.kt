package com.glycin.intelligame.shared

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class SpiteSheetImageLoader(
    spriteSheetPath: String,
    private val cellWidth: Int,
    private val cellHeight: Int,
    private val numSprites: Int,
    private val frameDelay: Int
){
    private val spriteSheet: BufferedImage = ImageIO.read(this.javaClass.getResource(spriteSheetPath))

    fun loadSprites(): List<BufferedImage> {
        val sprites = mutableListOf<BufferedImage>()
        val columns = spriteSheet.width / cellWidth
        for (index in 0 until numSprites) {
            val x = (index % columns) * cellWidth
            val y = (index / columns) * cellHeight
            if (x + cellWidth <= spriteSheet.width && y + cellHeight <= spriteSheet.height) {
                sprites.add(spriteSheet.getSubimage(x, y, cellWidth, cellHeight))
            }
        }
        return sprites
    }
}