package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.Gravity
import com.glycin.intelligame.shared.SpiteSheetImageLoader
import com.glycin.intelligame.util.toDeltaTime
import com.glycin.intelligame.util.toLongDeltaTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class Zonic(
    var position: Fec2,
    val width: Int,
    val height: Int,
    scope: CoroutineScope,
    fps: Long,
) {
    var alive = true
    private val deltaTime = fps.toDeltaTime()
    private val standingSprites = arrayOfNulls<BufferedImage>(4)
    private val runningSprites = arrayOfNulls<BufferedImage>(6)
    private val jumpingSprites = arrayOfNulls<BufferedImage>(2)
    private val crouchingSprites = arrayOfNulls<BufferedImage>(3)
    private val hurtSprites = arrayOfNulls<BufferedImage>(2)
    private val jumpPower = 2.0f
    private val speed = 0.5f

    private var zonicState: ZonicState = ZonicState.IDLE
    private var currentSprite : BufferedImage
    private var velocity = Fec2.zero
    private var groundY = position.y
    private var currentAnimationIndex = 0
    private var skipFrameCount = 0

    init {
        val spriteLoader = SpiteSheetImageLoader(
            spriteSheetPath = "/Sprites/zonic/zonic.png",
            cellWidth = 33,
            cellHeight = 32,
            numSprites = 36,
        )

        val sprites = spriteLoader.loadSprites()
        standingSprites.indices.forEach { index -> standingSprites[index] = sprites[index] }
        (6 until 12).forEachIndexed { index, i -> runningSprites[index] = sprites[i] }
        (18 until 21).forEachIndexed { index, i -> crouchingSprites[index] = sprites[i] }
        (24 until 26).forEachIndexed { index, i -> hurtSprites[index] = sprites[i] }
        (30 until 32).forEachIndexed { index, i -> jumpingSprites[index] = sprites[i] }
        currentSprite = standingSprites[0]!!

        scope.launch(Dispatchers.Default) {
            zonicUpdate(fps.toLongDeltaTime())
        }
    }

    fun draw(g: Graphics2D) {
        g.drawImage(currentSprite, position.x.roundToInt(), position.y.roundToInt(), width, height, null)
    }

    fun jump() {
        if(zonicState == ZonicState.JUMPING) { return }
        zonicState = ZonicState.JUMPING
        velocity += Fec2.up * jumpPower
        groundY = position.y
    }

    fun moveRight() {
        zonicState = ZonicState.RUNNING
        velocity = Fec2.right * (speed * deltaTime)
    }

    fun moveLeft() {
        zonicState = ZonicState.RUNNING
        velocity = Fec2.left * (speed * deltaTime)
    }

    fun crouch() {
        zonicState = ZonicState.CROUCHING
        velocity = Fec2.zero
        currentSprite = crouchingSprites[2]!!
    }

    fun idle() {
        if(zonicState == ZonicState.JUMPING) { return }
        zonicState = ZonicState.IDLE
        velocity = Fec2.zero
        currentSprite = standingSprites[0]!!
    }

    fun pain(){

    }

    private suspend fun zonicUpdate(delayTime: Long) {
        while(alive){
            when(zonicState) {
                ZonicState.JUMPING -> {
                    velocity += Gravity.asFec2 * deltaTime
                    position += velocity * deltaTime
                    currentSprite = if(velocity.y < 0){
                        jumpingSprites[0]!!
                    }else {
                        jumpingSprites[1]!!
                    }

                    if(position.y >= groundY){
                        position = Fec2(position.x, groundY)
                        zonicState = ZonicState.IDLE
                        velocity = Fec2.zero
                        currentSprite = standingSprites[0]!!
                    }
                }
                ZonicState.RUNNING -> {
                    position += velocity
                    showAnimation(runningSprites, 6)
                }
                ZonicState.IDLE -> {
                    showAnimation(standingSprites, 10)
                }
                ZonicState.HURT -> {}
                ZonicState.CROUCHING -> {}
            }
            delay(delayTime)
        }
    }

    private fun showAnimation(sprites: Array<BufferedImage?>, frameDelay: Int) {
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

    private enum class ZonicState {
        IDLE,
        RUNNING,
        JUMPING,
        CROUCHING,
        HURT,
    }
}