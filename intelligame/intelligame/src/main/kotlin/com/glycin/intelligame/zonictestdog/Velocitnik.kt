package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toDeltaTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt
import kotlin.random.Random

class Velocitnik(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val zonic: Zonic,
    val scope: CoroutineScope,
    fps: Long,
) {

    var active = true
    private val deltaTime = fps.toDeltaTime()
    private val speed = 0.15f
    private val idleSprites = arrayOfNulls<BufferedImage>(10)
    private val laserAttackSprites = arrayOfNulls<BufferedImage>(8)
    private val handAttackSprites = arrayOfNulls<BufferedImage>(10)
    private var currentAnimationIndex = 0
    private var skipFrameCount = 0
    private var currentSprite : BufferedImage
    private var state = VelocitnikState.FLOATING
    private var velocity = Vec2.down * (speed * deltaTime)

    private val attackCooldownSeconds = 15
    private var onCooldown = true

    private var laser: VelocitnikLaser? = null
    private var arm: VelocitnikArm? = null
    private var usingAttack = false

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
        (20 until 30).forEachIndexed { index, i -> handAttackSprites[index] = sprites[i] }
        currentSprite = idleSprites[0]!!
        startCooldown()
    }
    fun draw(g: Graphics2D) {
        if(!active) return

        g.drawImage(currentSprite, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
        laser?.draw(g)
        arm?.draw(g)
        update()
    }

    private fun update() {
        if(!onCooldown) {
            val r = Random.nextInt(100)
            state = if(r <= 50){
                VelocitnikState.LASER
            }else {
                VelocitnikState.ARM
            }
            if(currentAnimationIndex != 0) {
                currentAnimationIndex = 0
            }
            startCooldown()
        }

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
            VelocitnikState.ARM -> {
                doAnimationOnce(handAttackSprites, 20) {
                    arm = VelocitnikArm(
                        position = position,
                        width = width,
                        height = height,
                        targetPos = zonic.position,
                        deltaTime = deltaTime,
                    )
                    doArmAttack()
                }
            }
            VelocitnikState.LASER -> {
                doAnimationOnce(laserAttackSprites, 20) {
                    laser = VelocitnikLaser(
                        position = position,
                        width = 700,
                        height = 100
                    )
                    doLaser()
                }
            }
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

    private fun doAnimationOnce(sprites: Array<BufferedImage?>, frameDelay: Int, callback: () -> Unit) {
        if(currentAnimationIndex >= sprites.size - 2) {
            if(!usingAttack) {
                callback.invoke()
                usingAttack = true
            }
        }else {
            skipFrameCount++
            if(skipFrameCount % frameDelay == 0) {
                currentAnimationIndex++
            }
            currentSprite = sprites[currentAnimationIndex]!!
        }
    }

    private fun startCooldown() {
        onCooldown = true
        scope.launch(Dispatchers.Default) {
            delay(attackCooldownSeconds * 1000L)
            onCooldown = false
        }
    }

    private fun doArmAttack() {
        scope.launch(Dispatchers.Default) {
            delay(5000L)
            state = VelocitnikState.FLOATING
            usingAttack = false
            arm = null
        }
    }

    private fun doLaser() {
        scope.launch(Dispatchers.Default) {
            delay(2500L)
            state = VelocitnikState.FLOATING
            usingAttack = false
            laser = null
        }
    }
}

private enum class VelocitnikState {
    FLOATING,
    LASER,
    ARM,
    DEAD,
}