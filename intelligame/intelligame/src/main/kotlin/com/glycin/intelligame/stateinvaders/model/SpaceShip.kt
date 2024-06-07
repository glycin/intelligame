package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.BulletManager
import kotlin.math.roundToInt

class SpaceShip(
    var position: Vec2,
    val width: Int,
    val height: Int,
    private val minX: Int,
    private val maxX: Int,
    private val speed: Int = 2,
    private val bm: BulletManager,
) {

    private var shootTime = 0L
    private val shootCooldown = 500L

    fun moveLeft(deltaTime: Float) {
        if(position.x > minX) {
            position += Vec2.left * (deltaTime * speed).roundToInt()
        }
    }

    fun moveRight(deltaTime: Float) {
        if(position.x < maxX) {
            position += Vec2.right * (deltaTime * speed).roundToInt()
        }
    }

    fun shoot() {
        if(System.currentTimeMillis() >= shootTime) {
            val bullet = Bullet(Vec2(position.x + width / 2, position.y), 10, 25, Vec2.up)
            bm.submitBullet(bullet)
            shootTime = System.currentTimeMillis() + shootCooldown
        }
    }
}