package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2

class Ball(
    var position: Vec2,
    val radius: Int = 15,
    private val speed: Float = 0.5f,
) {

    private var direction = Vec2.right

    fun move(deltaTime: Float){
        position += direction * (deltaTime * speed)
    }
}