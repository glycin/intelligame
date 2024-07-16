package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.Gravity
import com.glycin.intelligame.shared.SpiteSheetImageLoader
import com.glycin.intelligame.util.toDeltaTime
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.ui.JBColor
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
    private val colManager: CollisionsManager,
    private val portalOpener: PortalOpener,
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
    private var keyIsPressed = false
    private var isRunningJump = false
    private var facing: ZonicFacing = ZonicFacing.RIGHT
    private var currentSprite : BufferedImage
    private var velocity = Fec2.zero
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
        if(facing == ZonicFacing.LEFT) {
            g.drawImage(currentSprite, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
        }else{
            g.drawImage(currentSprite, position.x.roundToInt(), position.y.roundToInt(), width, height, null)
        }

        g.color = JBColor.GREEN.brighter()
        g.fillOval(getBottomPos().x.roundToInt(), getBottomPos().y.roundToInt(), 5, 5)
        g.fillOval(getTopPos().x.roundToInt(), getTopPos().y.roundToInt(), 5, 5)
        g.fillOval(getLeftPositions()[0].x.roundToInt(), getLeftPositions()[0].y.roundToInt(), 5, 5)
        g.fillOval(getLeftPositions()[1].x.roundToInt(), getLeftPositions()[1].y.roundToInt(), 5, 5)
        g.fillOval(getRightPositions()[0].x.roundToInt(), getRightPositions()[0].y.roundToInt(), 5, 5)
        g.fillOval(getRightPositions()[1].x.roundToInt(), getRightPositions()[1].y.roundToInt(), 5, 5)
    }

    fun jump() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) { return }
        zonicState = ZonicState.JUMPING
        isRunningJump = velocity.x != 0.0f
        velocity = Fec2(velocity.x / 4, Fec2.up.y * jumpPower)
        keyIsPressed = true
    }

    fun moveRight() {
        if(zonicState == ZonicState.FALLING || zonicState == ZonicState.JUMPING) {
            velocity += Fec2(speed, 0.0f)
        }else {
            velocity = Fec2.right * ( speed * deltaTime)
            zonicState = ZonicState.RUNNING
        }
        facing = ZonicFacing.RIGHT
        keyIsPressed = true
    }

    fun moveLeft() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) {
            velocity += Fec2(-speed, 0.0f)
        }else{
            zonicState = ZonicState.RUNNING
            velocity = Fec2.left * (speed * deltaTime)
        }
        facing = ZonicFacing.LEFT
        keyIsPressed = true
    }

    fun crouch() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) { return }
        zonicState = ZonicState.CROUCHING
        velocity = Fec2.zero
        currentSprite = crouchingSprites[2]!!
        keyIsPressed = true
        val (isNearMethod, method) = portalOpener.isNearMethod(getBottomPos())
        if(isNearMethod && method != null){
            portalOpener.openPortals(method)
        }
    }

    fun idle() {
        keyIsPressed = false
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) { return }
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

                    if(velocity.y > 0.0f){
                        val bottomPos = getBottomPos()
                        val landedY = colManager.getClosestGround(bottomPos)

                        if(landedY != null){
                            land(landedY)
                            if(isRunningJump){
                                if(facing == ZonicFacing.LEFT && keyIsPressed){
                                    moveLeft()
                                }else if (facing == ZonicFacing.RIGHT && keyIsPressed){
                                    moveRight()
                                }
                                isRunningJump = false
                            }
                        }
                    }
                }
                ZonicState.RUNNING -> {
                    if(facing == ZonicFacing.LEFT){
                        if(colManager.canRun(getLeftPositions())){
                            position += velocity
                        }
                    }else{
                        if(colManager.canRun(getRightPositions())){
                            position += velocity
                        }
                    }

                    showAnimation(runningSprites, 6)
                    if(colManager.shouldFall(getBottomPos())){
                        fall()
                    }
                }
                ZonicState.IDLE -> {
                    showAnimation(standingSprites, 10)
                }
                ZonicState.HURT -> {}
                ZonicState.CROUCHING -> {}
                ZonicState.FALLING -> {
                    velocity += Gravity.asFec2 * deltaTime
                    position += velocity * deltaTime
                    if(velocity.y > 0.0f){
                        val bottomPos = getBottomPos()
                        val landedY = colManager.getClosestGround(bottomPos)

                        if(landedY != null){
                            land(landedY)
                        }
                    }
                }
            }
            delay(delayTime)
        }
    }

    private fun fall() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) { return }
        zonicState = ZonicState.FALLING
        velocity = Gravity.asFec2
        currentSprite = jumpingSprites[1]!!
    }

    private fun land(tileY : Int) {
        if(zonicState == ZonicState.FALLING || zonicState == ZonicState.JUMPING) {
            position = Fec2(position.x, tileY - height.toFloat())
            zonicState = ZonicState.IDLE
            velocity = Fec2.zero
        }
    }

    private fun getBottomPos() = Fec2(position.x + (width / 2), position.y + height)

    private fun getTopPos() = Fec2(position.x + (width / 2), position.y)

    private fun getLeftPositions() = listOf(
        Fec2(position.x, position.y + (height / 1.5f)),
        Fec2(position.x, position.y + (height / 2.5f))
    )

    private fun getRightPositions() = listOf(
        Fec2(position.x + width, position.y + (height / 1.5f)),
        Fec2(position.x + width, position.y + (height / 2.5f))
    )

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
        FALLING,
    }

    private enum class ZonicFacing {
        LEFT,
        RIGHT
    }
}