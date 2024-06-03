package com.glycin.intelligame.pong.model

import com.glycin.intelligame.pong.PongCollider
import com.glycin.intelligame.pong.PongGame
import com.glycin.intelligame.shared.Vec2
import java.util.Random
import kotlin.math.roundToInt

class Ball(
    var position: Vec2,
    val radius: Int = 10,
    val collider: PongCollider,
    val service: PongGame,
    private val speed: Float = 0.25f,
) {
    private val immunityFrames = 10
    private var lifetime = 0
    private var direction = Vec2(1, 1)
    private var initialSafePosition = Vec2.zero

    fun move(deltaTime: Float){
        if(lifetime <= immunityFrames) {
            lifetime++
        }else {
            val colObj = collider.collidesBricks(position)
                ?: collider.collidesGoal(position)
                ?: collider.collidesObstacle(position)

            if(colObj != null) {
                if(colObj is Goal) reset(colObj.goalIndex) else direction = collider.getBounceVector(direction, position, colObj)
            }else {
                if(initialSafePosition == Vec2.zero) {
                    initialSafePosition = position
                }
            }
        }

        position += direction * (deltaTime * speed).roundToInt()
    }

    private fun reset(index: Int){
        service.updateScore(index)
        position = initialSafePosition

        val rand = Random()
        val num = rand.nextInt(4)
        direction = when(num) {
            0 -> Vec2(1, 1)
            1 -> Vec2(-1, 1)
            2 -> Vec2(1, -1)
            3 -> Vec2(-1, -1)
            else -> Vec2(1, 1)
        }
    }
}