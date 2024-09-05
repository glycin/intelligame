package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.shared.Gravity
import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.util.toDeltaTime
import com.glycin.intelligame.util.toLongDeltaTime
import com.glycin.intelligame.util.toPoint
import com.glycin.intelligame.zonictestdog.level.Coin
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class Zonic(
    var position: Vec2,
    val width: Int,
    val height: Int,
    private val maxY: Int,
    private val colManager: CollisionsManager,
    private val portalOpener: PortalOpener,
    private val ztdGame: ZtdGame,
    scope: CoroutineScope,
    fps: Long,
) {
    var alive = true
    val pickedUpCoins = mutableListOf<Coin>()
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
    private var velocity = Vec2.zero
    private var currentAnimationIndex = 0
    private var skipFrameCount = 0

    init {
        val spriteLoader = SpriteSheetImageLoader(
            spriteSheetPath = "/Sprites/sheets/zonic.png",
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

        // Debug points
        /*g.color = if(zonicState == ZonicState.HURT) JBColor.RED.brighter().brighter() else JBColor.GREEN.brighter()
        g.fillOval(getBottomPos().x.roundToInt(), getBottomPos().y.roundToInt(), 5, 5)
        g.fillOval(getTopPos().x.roundToInt(), getTopPos().y.roundToInt(), 5, 5)
        g.fillOval(getLeftPositions()[0].x.roundToInt(), getLeftPositions()[0].y.roundToInt(), 5, 5)
        g.fillOval(getLeftPositions()[1].x.roundToInt(), getLeftPositions()[1].y.roundToInt(), 5, 5)
        g.fillOval(getRightPositions()[0].x.roundToInt(), getRightPositions()[0].y.roundToInt(), 5, 5)
        g.fillOval(getRightPositions()[1].x.roundToInt(), getRightPositions()[1].y.roundToInt(), 5, 5)
        g.fillOval(getMidPos().x.roundToInt(), getMidPos().y.roundToInt(), 5, 5)*/
    }

    fun jump() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING || zonicState == ZonicState.HURT) { return }
        zonicState = ZonicState.JUMPING
        isRunningJump = velocity.x != 0.0f
        velocity = Vec2(velocity.x / 4, Vec2.up.y * jumpPower)
        keyIsPressed = true
    }

    fun moveRight() {
        if(zonicState == ZonicState.HURT) return
        if(zonicState == ZonicState.FALLING || zonicState == ZonicState.JUMPING) {
            velocity += Vec2.right * (speed / 8 * deltaTime)
        }else {
            velocity = Vec2.right * ( speed * deltaTime)
            zonicState = ZonicState.RUNNING
        }
        facing = ZonicFacing.RIGHT
        keyIsPressed = true
    }

    fun moveLeft() {
        if(zonicState == ZonicState.HURT) return
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) {
            velocity += Vec2.left * (speed / 8 * deltaTime)
        }else{
            zonicState = ZonicState.RUNNING
            velocity = Vec2.left * (speed * deltaTime)
        }
        facing = ZonicFacing.LEFT
        keyIsPressed = true
    }

    fun crouch() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING || zonicState == ZonicState.HURT) { return }
        zonicState = ZonicState.CROUCHING
        velocity = Vec2.zero
        currentSprite = crouchingSprites[2]!!
        keyIsPressed = true
        val (isNearMethod, method) = portalOpener.isNearMethod(getBottomPos())
        if(isNearMethod && method != null){
            portalOpener.openPortals(method, getBottomPos())
        }
    }

    fun idle() {
        keyIsPressed = false
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING || zonicState == ZonicState.HURT) { return }
        zonicState = ZonicState.IDLE
        velocity = Vec2.zero
        currentSprite = standingSprites[0]!!
    }

    fun pain(){
        keyIsPressed = false
        zonicState = ZonicState.HURT
        velocity = Vec2(-velocity.x / 4, Vec2.up.y * (jumpPower / 2))
        currentSprite = hurtSprites[0]!!
        val coinPositions = calculateCoinPositions()
        pickedUpCoins.forEachIndexed { index, coin ->
            coin.loseCoin(position, coinPositions[index])
        }
        ztdGame.deleteTests(pickedUpCoins)
        pickedUpCoins.clear()
    }

    fun getMidPos() = Vec2(position.x + (width / 2), position.y + (height / 2))

    private suspend fun zonicUpdate(delayTime: Long) {
        while(alive){
            val zonicMidPos = getMidPos().toPoint()
            when(zonicState) {
                ZonicState.JUMPING -> {
                    velocity += Gravity.vec2 * deltaTime
                    velocity = Vec2(velocity.x.coerceAtMost(1.5f), velocity.y.coerceAtMost(2f))
                    position += velocity * deltaTime

                    if(position.y >= maxY) {
                        position = Vec2(100f, -100f)
                    }

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

                    portalCheck(zonicMidPos)
                    coinCheck(zonicMidPos)
                    enemyHurtCheck(if(velocity.y < 0) getTopPos().toPoint() else getBottomPos().toPoint())
                }
                ZonicState.RUNNING -> {
                    //TODO: Dont block zonic left and right collisions for now
                    if(facing == ZonicFacing.LEFT){
                        //if(colManager.canRun(getLeftPositions())){
                            position += velocity
                        //}
                    }else{
                        //if(colManager.canRun(getRightPositions())){
                            position += velocity
                        //}
                    }
                    showAnimation(runningSprites, 6)
                    if(colManager.shouldFall(getBottomPos())){
                        fall()
                    }

                    portalCheck(zonicMidPos)
                    coinCheck(zonicMidPos)
                    enemyHurtCheck(if(facing == ZonicFacing.LEFT) getLeftPositions().last().toPoint() else getRightPositions().last().toPoint())
                }
                ZonicState.IDLE -> {
                    showAnimation(standingSprites, 10)
                }
                ZonicState.HURT -> {
                    velocity += Gravity.vec2 * deltaTime
                    position += velocity * deltaTime
                    showAnimation(hurtSprites, 6)

                    if(velocity.y > 0.0f){
                        val bottomPos = getBottomPos()
                        val landedY = colManager.getClosestGround(bottomPos)

                        if(landedY != null){
                            land(landedY)
                        }
                    }
                }
                ZonicState.CROUCHING -> {}
                ZonicState.FALLING -> {
                    velocity += Gravity.vec2 * deltaTime
                    velocity = Vec2(velocity.x.coerceAtMost(1.5f), velocity.y.coerceAtMost(2f))
                    position += velocity * deltaTime

                    if(position.y >= maxY) {
                        position = Vec2(100f, -100f)
                    }

                    if(velocity.y > 0.0f){
                        val bottomPos = getBottomPos()
                        val landedY = colManager.getClosestGround(bottomPos)

                        if(landedY != null){
                            land(landedY)
                        }
                    }
                    portalCheck(zonicMidPos)
                    coinCheck(zonicMidPos)
                    enemyHurtCheck(zonicMidPos)
                }
            }
            delay(delayTime)
        }
    }

    private fun fall() {
        if(zonicState == ZonicState.JUMPING || zonicState == ZonicState.FALLING) { return }
        zonicState = ZonicState.FALLING
        velocity = Gravity.vec2
        currentSprite = jumpingSprites[1]!!
    }

    private fun land(tileY : Int) {
        if(zonicState == ZonicState.FALLING || zonicState == ZonicState.JUMPING || zonicState == ZonicState.HURT) {
            position = Vec2(position.x, tileY - height.toFloat())
            zonicState = ZonicState.IDLE
            velocity = Vec2.zero
        }
    }

    private fun getBottomPos() = Vec2(position.x + (width / 2), position.y + height)

    private fun getTopPos() = Vec2(position.x + (width / 2), position.y)

    private fun getLeftPositions() = listOf(
        Vec2(position.x, position.y + (height / 1.5f)),
        Vec2(position.x, position.y + (height / 2.5f))
    )

    private fun getRightPositions() = listOf(
        Vec2(position.x + width, position.y + (height / 1.5f)),
        Vec2(position.x + width, position.y + (height / 2.5f))
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

    private fun portalCheck(point: Point) {
        if(zonicState == ZonicState.IDLE) { return }

        val portal = colManager.portalCheck(point)
        if(portal != null){
            zonicState = ZonicState.IDLE
            portalOpener.travelToPortal(portal)
        }
    }

    private fun coinCheck(point: Point) {
        val coin = colManager.coinCheck(point)
        if(coin != null && !coin.pickedUp) {
            pickedUpCoins.add(coin)
            coin.pickUp()
        }
    }

    private fun enemyHurtCheck(point: Point) {
        val enemy = colManager.enemyCheck(point)
        if(enemy != null && enemy.alive){
            pain()
        }
    }

    private fun calculateCoinPositions(): List<Vec2> {
        val angleIncrement = 2 * Math.PI / pickedUpCoins.size
        val midPos = getMidPos()
        val positions = mutableListOf<Vec2>()
        for(i in 0 until pickedUpCoins.size){
            val angle = i * angleIncrement
            val x = midPos.x + (150 * cos(angle)).toInt()
            val y = midPos.y + (150 * sin(angle)).toInt()
            positions.add(Vec2(x, y))
        }
        return positions
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