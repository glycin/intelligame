package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.SpiteSheetImageLoader
import java.awt.image.BufferedImage

class Zonic(
    var position: Fec2,
    val width: Int,
    val height: Int,
) {
    val standingSprites = arrayOfNulls<BufferedImage>(4)
    val runningSprites = arrayOfNulls<BufferedImage>(6)
    val jumpingSprites = arrayOfNulls<BufferedImage>(2)
    val duckSprites = arrayOfNulls<BufferedImage>(3)
    val hurtSprites = arrayOfNulls<BufferedImage>(2)

    init {
        val spriteLoader = SpiteSheetImageLoader(
            spriteSheetPath = "/Sprites/zonic/zonic.png",
            cellWidth = 33,
            cellHeight = 32,
            numSprites = 36,
            frameDelay = 4,
        )

        val sprites = spriteLoader.loadSprites()
        standingSprites.indices.forEach { index -> standingSprites[index] = sprites[index] }
        (6 until 12).forEachIndexed { index, i -> runningSprites[index] = sprites[i] }
        (18 until 21).forEachIndexed { index, i -> duckSprites[index] = sprites[i] }
        (24 until 26).forEachIndexed { index, i -> hurtSprites[index] = sprites[i] }
        (30 until 32).forEachIndexed { index, i -> jumpingSprites[index] = sprites[i] }
    }
}