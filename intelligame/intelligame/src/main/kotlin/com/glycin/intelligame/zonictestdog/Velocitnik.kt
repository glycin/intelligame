package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toDeltaTime
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class Velocitnik(
    var position: Vec2,
    val width: Int,
    val height: Int,
    fps: Long,
) {

    var active = true
    private val deltaTime = fps.toDeltaTime()
    private val speed = 0.15f
    private val idleSprites = arrayOfNulls<BufferedImage>(10)
    private val laserAttackSprites = arrayOfNulls<BufferedImage>(8)
    private val handAttackSprite = arrayOfNulls<BufferedImage>(10)
    private var currentAnimationIndex = 0
    private var skipFrameCount = 0
    private var currentSprite : BufferedImage
    private var state = VelocitnikState.FLOATING
    private var velocity = Vec2.down * (speed * deltaTime)

    init {
        val spriteLoader = SpriteSheetImageLoader(
            spriteSheetPath = "/Sprites/sheets/velocitnic.png",
            cellWidth = 100,
            cellHeight = 100,
            numSprites = 90,
        )

        val sprites = spriteLoader.loadSprites()
        (60 until 70).forEachIndexed { index, i -> idleSprites[index] = sprites[i] }
        (50 until 58).forEachIndexed { index, i -> laserAttackSprites[index] = sprites[i]  }
        (20 until 30).forEachIndexed { index, i -> handAttackSprite[index] = sprites[i] }
        currentSprite = idleSprites[0]!!

    }
    fun draw(g: Graphics2D) {
        if(!active) return

        g.drawImage(currentSprite, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
        update()
    }

    private fun update() {
        when(state) {
            VelocitnikState.FLOATING -> {
                if(position.y < 25) {
                    velocity = Vec2.down * (speed * deltaTime)
                }

                if(position.y >= 1000) {
                    velocity = Vec2.up * (speed * deltaTime)
                }

                position += velocity
                doAnimation(idleSprites, 10)
            }
            VelocitnikState.ARM -> {}
            VelocitnikState.LASER -> {}
            VelocitnikState.DEAD -> {}
        }
    }

    private fun doAnimation(sprites: Array<BufferedImage?>, frameDelay: Int) {
        skipFrameCount++
        if(skipFrameCount % frameDelay == 0) {
            currentAnimationIndex++
        }

        if(currentAnimationIndex >= sprites.size - 1) {
            currentAnimationIndex = 0
            skipFrameCount = 0
        }
        currentSprite = sprites[currentAnimationIndex]!!
    }
}

private enum class VelocitnikState {
    FLOATING,
    LASER,
    ARM,
    DEAD,
}