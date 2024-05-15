package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.Obstacle
import com.glycin.intelligame.pong.model.PlayerBrick
import com.glycin.intelligame.shared.Vec2

class CollisionManager(
    private val playerBricks: List<PlayerBrick>,
    private val obstacles: List<Obstacle>,
) {
    fun collidesWith(positionToCheck: Vec2): Boolean {
        return true
    }

    fun getBounceVector(direction: Vec2): Vec2 {
        return Vec2.zero
    }
}