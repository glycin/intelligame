package com.glycin.intelligame.pong.model

import com.glycin.intelligame.pong.PongCollider
import com.glycin.intelligame.shared.Vec2

class Ball(
    var position: Vec2,
    val radius: Int = 10,
    val collider: PongCollider,
    private val speed: Float = 0.25f,
) {
    private val immunityFrames = 10
    private var lifetime = 0
    private var direction = Vec2(1.0f, 1.0f)
    private var initialSafePosition = Vec2.zero

    fun move(deltaTime: Float){
        if(lifetime <= immunityFrames) {
            lifetime++
        }else {
            val colObj = collider.collidesBricks(position)
                ?: collider.collidesGoal(position)
                ?: collider.collidesObstacle(position)

            if(colObj != null) {
                if(colObj is Goal) reset() else direction = collider.getBounceVector(direction, position, colObj)
            }else {
                if(initialSafePosition == Vec2.zero) {
                    initialSafePosition = position
                }
            }
        }

        position += direction * (deltaTime * speed)
    }

    fun reset(){
        position = initialSafePosition
    }
}