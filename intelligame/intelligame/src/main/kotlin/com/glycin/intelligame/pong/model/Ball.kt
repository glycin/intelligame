package com.glycin.intelligame.pong.model

import com.glycin.intelligame.pong.PongCollider
import com.glycin.intelligame.pong.PongGame
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toLongDeltaTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.roundToInt

class Ball(
    var position: Vec2,
    val radius: Int = 10,
    val collider: PongCollider,
    val service: PongGame,
    private val speed: Float = 0.25f,
    fps: Long,
    scope: CoroutineScope,
) {
    private val deltaTime = fps.toLongDeltaTime()
    private val immunityFrames = 10
    private var lifetime = 0
    private var direction = Vec2(1f, 1f)
    private var initialSafePosition = Vec2.zero

    private fun midPoint() = Vec2(position.x + (radius / 2), position.y + (radius / 2))

    private var active = true

    init{
        scope.launch(Dispatchers.Default) {
            while(active) {
                move(deltaTime.toFloat())
                delay(deltaTime)
            }
        }
    }

    fun stop() {
        active = false
    }

    private fun move(deltaTime: Float){
        if(lifetime <= immunityFrames) {
            lifetime++
        }else {
            val colObj = collider.collidesBricks(midPoint())
                ?: collider.collidesGoal(midPoint())
                ?: collider.collidesObstacle(midPoint())

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
            0 -> Vec2(1f, 1f)
            1 -> Vec2(-1f, 1f)
            2 -> Vec2(1f, -1f)
            3 -> Vec2(-1f, -1f)
            else -> Vec2(1f, 1f)
        }
    }
}