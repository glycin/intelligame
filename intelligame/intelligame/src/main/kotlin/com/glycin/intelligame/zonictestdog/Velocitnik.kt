package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toDeltaTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.roundToInt
import kotlin.random.Random

class Velocitnik(
    var position: Vec2,
    private val width: Int,
    private val height: Int,
    private val zonic: Zonic,
    private val scope: CoroutineScope,
    fps: Long,
) {

    var active = true
    private val deltaTime = fps.toDeltaTime()
    private val speed = 0.15f
    private val idleSprites = arrayOfNulls<BufferedImage>(10)
    private val laserAttackSprites = arrayOfNulls<BufferedImage>(8)
    private val handAttackSprites = arrayOfNulls<BufferedImage>(10)
    private val deathSprites = arrayOfNulls<BufferedImage>(14)
    private var currentAnimationIndex = 0
    private var skipFrameCount = 0
    private var currentSprite : BufferedImage
    private var state = VelocitnikState.FLOATING
    private var velocity = Vec2.down * (speed * deltaTime)

    private val attackCooldownSeconds = 5
    private var onCooldown = true

    private var laser: VelocitnikLaser? = null
    private var arm: VelocitnikArm? = null
    private var usingAttack = false
    private var dying = false

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
        (70 until 84).forEachIndexed { index, i -> deathSprites[index] = sprites[i]  }
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

    fun kill() {
        dying = true
        state = VelocitnikState.DEAD
    }

    fun getKillRect() = Rectangle(position.x.roundToInt() + 180, position.y.roundToInt() + 100, 40, 50)

    private fun update() {
        if(!onCooldown && !dying) {
            val r = Random.nextInt(100)
            state = if(r <= 50){
                VelocitnikState.ARM
            }else {
                VelocitnikState.LASER
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
                doAnimation(idleSprites)
            }
            VelocitnikState.ARM -> {
                doAnimationOnce(handAttackSprites) {
                    arm = VelocitnikArm(
                        position = position,
                        width = width,
                        height = height,
                        deltaTime = deltaTime,
                        zonic = zonic,
                    )
                    doArmAttack()
                }
            }
            VelocitnikState.LASER -> {
                doAnimationOnce(laserAttackSprites) {
                    val x = (position.x + (width / 4)) - 520
                    val y = position.y + (height / 4)
                    laser = VelocitnikLaser(
                        position = Vec2(x, y),
                        width = 700,
                        height = 100,
                        zonic = zonic,
                    )
                    doLaser()
                }
            }
            VelocitnikState.DEAD -> {
                doAnimationOnce(deathSprites) {
                    death()
                }
            }
        }
    }

    private fun doAnimation(sprites: Array<BufferedImage?>) {
        skipFrameCount++
        if(skipFrameCount % 10 == 0) {
            currentAnimationIndex++
        }

        if(currentAnimationIndex >= sprites.size - 1) {
            currentAnimationIndex = 0
            skipFrameCount = 0
        }
        currentSprite = sprites[currentAnimationIndex]!!
    }

    private fun doAnimationOnce(sprites: Array<BufferedImage?>, callback: () -> Unit) {
        if(currentAnimationIndex >= sprites.size - 2) {
            if(!usingAttack) {
                callback.invoke()
                usingAttack = true
            }
        }else {
            skipFrameCount++
            if(skipFrameCount % 20 == 0) {
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
            delay(3000L)
            state = VelocitnikState.FLOATING
            usingAttack = false
            arm = null
        }
    }

    private fun doLaser() {
        scope.launch(Dispatchers.Default) {
            delay(2200L)
            state = VelocitnikState.FLOATING
            usingAttack = false
            laser = null
        }
    }

    private fun death() {
        scope.launch(Dispatchers.Default) {
            delay(5000L)
            active = false
        }
    }
}

private enum class VelocitnikState {
    FLOATING,
    LASER,
    ARM,
    DEAD,
}