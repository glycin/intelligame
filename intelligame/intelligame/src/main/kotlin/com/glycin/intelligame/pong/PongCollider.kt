package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.CollisionObject
import com.glycin.intelligame.pong.model.Obstacle
import com.glycin.intelligame.pong.model.PlayerBrick
import com.glycin.intelligame.shared.Vec2

class PongCollider(
    private val playerBricks: List<PlayerBrick>,
    private val obstacles: List<Obstacle>,
) {

    fun collidesBricks(positionToCheck: Vec2): CollisionObject? = playerBricks.firstOrNull { brick ->
        (positionToCheck.x in brick.minX..brick.maxX) && (positionToCheck.y in brick.minY..brick.maxY)
    }

    fun collidesObstacle(positionToCheck: Vec2): CollisionObject? = obstacles.firstOrNull { ob ->
        (positionToCheck.x in ob.minX..ob.maxX) && (positionToCheck.y in ob.minY..ob.maxY)
    }

    fun getBounceVector(direction: Vec2, collisionPosition: Vec2, collisionObject: CollisionObject): Vec2 {
        val colNormal = collisionObject.getCollisionNormal(collisionPosition)
        return when(colNormal) {
            Vec2.left, Vec2.right ->  Vec2(-direction.x, direction.y)
            Vec2.down, Vec2.up -> Vec2(direction.x, -direction.y)
            else -> direction
        }
    }
}