package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.StateInvadersGame
import kotlin.math.roundToInt

class Bullet(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val isHostile: Boolean = false,
    private val direction: Vec2,
    private val game: StateInvadersGame,
    private val speed: Int = 3,
) {
    val minX = position.x
    val maxX = position.x + width
    val minY = position.y
    val maxY = position.y + height

    private fun collisionPoint(): Vec2 {
        return if(isHostile) {
            Vec2(position.x - width / 2, position.y + height)
        }else{
            Vec2(position.x + width / 2, position.y)
        }
    }
    fun move(deltaTime: Float) {
        position += direction * (deltaTime * speed).roundToInt()

        if(isHostile) {
            if(game.cm.collidingPlayer(collisionPoint()) != null) {
                game.bm.removeBullet(this)
                game.gameOver()
                position = Vec2(-100, -1000)
            }
        }else{
            val collided = game.cm.collidingStaliens(collisionPoint())
            if(collided != null) {
                game.bm.removeBullet(this)
                game.destroyStalien(collided)
                position = Vec2(-100, -1000)
            }
        }
    }
}