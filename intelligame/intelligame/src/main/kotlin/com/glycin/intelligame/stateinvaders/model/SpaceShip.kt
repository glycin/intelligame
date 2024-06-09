package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.StateInvadersGame
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class SpaceShip(
    var position: Vec2,
    val width: Int,
    val height: Int,
    private val mapMinX: Int,
    private val mapMaxX: Int,
    private val speed: Int = 2,
    private val game: StateInvadersGame,
) {
    fun minX() = position.x
    fun maxX() = position.x + width
    fun minY() = position.y
    fun maxY() = position.y + height
    val spaceShipImage: BufferedImage = ImageIO.read(SpaceShip::class.java.getResource("/Sprites/spaceship.png"))

    private var shootTime = 0L
    private val shootCooldown = 500L

    fun moveLeft(deltaTime: Float) {
        if(position.x > mapMinX && game.state == GameState.STARTED) {
            position += Vec2.left * (deltaTime * speed).roundToInt()
        }
    }

    fun moveRight(deltaTime: Float) {
        if(position.x < mapMaxX && game.state == GameState.STARTED)  {
            position += Vec2.right * (deltaTime * speed).roundToInt()
        }
    }

    fun shoot() {
        if(game.state != GameState.STARTED) return
        
        if(System.currentTimeMillis() >= shootTime) {
            val bullet = Bullet(Vec2(position.x + width, position.y), 10, 25, false, Vec2.up, game)
            game.bm.submitBullet(bullet)
            shootTime = System.currentTimeMillis() + shootCooldown
        }
    }
}