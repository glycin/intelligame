package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.stateinvaders.model.Stalien
import com.glycin.intelligame.util.toDeltaTime
import kotlin.random.Random

class StalienManager(
    private val staliens: List<Stalien>,
    private val minX: Int,
    private val maxX: Int,
    private val ship: SpaceShip,
    fps: Long,
) {
    private val deltaTime = fps.toDeltaTime()
    private val firingChance = staliens.size

    private var groupDirection = Vec2.right
    private var curFrame = 0L
    private var skipTime = fps / 10

    fun moveGroup() {
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

        staliens.forEachIndexed { index, stalien ->
            val random = Random(index).nextInt(100)
            stalien.move(groupDirection, deltaTime.toFloat())

            if(random <= firingChance) {
            }
        }
    }
}