package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.CollisionObject
import com.glycin.intelligame.shared.Vec2
import kotlin.math.abs
import kotlin.math.min

class Obstacle(
    val position: Vec2,
    val width: Int,
    val height: Int,
): CollisionObject {
    val minX = position.x
    val maxX = position.x + width
    val minY = position.y
    val maxY = position.y + height

//    val topLeft = Vec2(minX, maxY)
//    val topRight = Vec2(maxX, maxY)
//    val bottomLeft = Vec2(minX, minY)
//    val bottomRight = Vec2(maxX, minY)

    override fun getCollisionNormal(colPosition: Vec2): Vec2 {
        val distLeft = abs(colPosition.x - minX)
        val distRight = abs(colPosition.x - maxX)
        val distTop = abs(colPosition.y - minY)
        val distBottom = abs(colPosition.y - maxY)

        val minDist = min(min(distLeft, distRight), min(distTop, distBottom))

        return when (minDist) {
            distLeft -> Vec2.left
            distRight -> Vec2.right
            distTop -> Vec2.up
            else -> Vec2.down
        }
    }
}
