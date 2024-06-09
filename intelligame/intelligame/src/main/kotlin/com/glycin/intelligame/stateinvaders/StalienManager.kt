package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.stateinvaders.model.Stalien
import com.glycin.intelligame.util.toDeltaTime
import kotlin.random.Random

class StalienManager(
    val staliens: MutableList<Stalien>,
    private val minX: Int,
    private val maxX: Int,
    private val ship: SpaceShip,
    fps: Long,
) {
    private val deltaTime = fps.toDeltaTime()
    private val firingChance = staliens.size / 4
    private val staliensToRemove = mutableSetOf<Stalien>()
    private val shootAllowedTime = System.currentTimeMillis() + 5000

    private var groupDirection = Vec2.right
    private var curFrame = 0L
    private val skipTime = fps / 10
    private var curAnimationFrame = 0L
    private val animationSkipTime = fps

    fun moveGroup() {
        if(staliens.isEmpty()) return

        if(curFrame < skipTime) {
            curFrame++
            return
        }else {
            curFrame = 0
        }

        val rightMost = staliens.maxBy { it.position.x }
        val leftMost = staliens.minBy { it.position.x }
        val bottomMost = staliens.maxBy { it.position.y }

        if(groupDirection == Vec2.right) {
            val nextRightStep = rightMost.position.x  + rightMost.width + 25
            if(nextRightStep > maxX) {
                groupDirection = Vec2.down
            }

        }else if (groupDirection == Vec2.left) {
            val nextLeftStep = leftMost.position.x - 25
            if(nextLeftStep < minX) {
                groupDirection = Vec2.down
            }

        } else if (groupDirection == Vec2.down) {
            val nextLeftStep = leftMost.position.x - 25
            groupDirection = if(nextLeftStep < minX) Vec2.right  else Vec2.left
        }

        if(bottomMost.position.y > ship.position.y) {
            println("game over")
        }

        staliens.forEach { stalien ->
            val random = Random.nextInt(100)
            stalien.move(groupDirection, deltaTime)

            if(System.currentTimeMillis() > shootAllowedTime) {
                if (random <= firingChance) {
                    stalien.shoot()
                }
            }
        }


        if(staliensToRemove.isNotEmpty()) {
            staliensToRemove.forEach { stalien -> staliens.remove(stalien) }
            staliensToRemove.clear()
        }
    }

    fun destroyAlien(collided: Stalien) {
        staliensToRemove.add(collided)
        collided.die()
    }

    fun shouldAnimate(): Boolean {
        if(curAnimationFrame < animationSkipTime) {
            curAnimationFrame++
            return false
        }else {
            curAnimationFrame = 0
            return true
        }
    }
}