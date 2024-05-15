package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2

class PlayerBrick(
    var position: Vec2,
    val width: Int = 10,
    val height: Int = 40,
) {

    private val speed: Float = 0.4f

    fun moveUp(deltaTime: Float) {
        position -= Vec2.up * (deltaTime * speed)
    }

    fun moveDown(deltaTime: Float) {
        position -= Vec2.down * (deltaTime * speed)
    }
}