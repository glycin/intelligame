package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.Bullet
import com.glycin.intelligame.util.toDeltaTime

class BulletManager(
    private val bullets: MutableList<Bullet>,
    fps : Long,
) {
    private val deltaTime = fps.toDeltaTime()
    private val bulletsToRemove = mutableSetOf<Bullet>()

    fun getAllActiveBullets(): List<Bullet> {
        return bullets.toList()
    }

    fun moveBullets() {
        bullets.forEach { it.move(deltaTime) }

        if(bulletsToRemove.isNotEmpty()) {
            bulletsToRemove.forEach { bullet -> bullets.remove(bullet) }
            bulletsToRemove.clear()
        }
    }

    fun submitBullet(bullet: Bullet) {
        bullets.add(bullet)
    }

    fun removeBullet(bullet: Bullet) {
        bullet.position = Vec2(-500f, -500f)
        bulletsToRemove.add(bullet)
    }
}