package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.stateinvaders.model.Bullet
import com.glycin.intelligame.util.toDeltaTime

class BulletManager(
    private val bullets: MutableList<Bullet>,
    fps : Long,
) {
    private val deltaTime = fps.toDeltaTime()

    fun getAllActiveBullets(): List<Bullet> {
        return bullets.toList()
    }

    fun moveBullets() {
        bullets.forEach { it.move(deltaTime) }
    }

    fun submitBullet(bullet: Bullet) {
        bullets.add(bullet)
    }
}