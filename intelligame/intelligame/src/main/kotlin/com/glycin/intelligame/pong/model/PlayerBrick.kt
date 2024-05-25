package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.CollisionObject
import com.glycin.intelligame.shared.Vec2
import kotlin.math.abs
import kotlin.math.min


class PlayerBrick(
    var position: Vec2,
    val width: Int = 20,
    val height: Int = 60,
): CollisionObject {
    fun minX() = position.x
    fun maxX() = position.x + width
    fun minY() = position.y
    fun maxY() = position.y + height

    private val speed: Float = 2f

    fun moveUp(deltaTime: Float) {
        position += Vec2.up * (deltaTime * speed)
    }

    fun moveDown(deltaTime: Float) {
        position += Vec2.down * (deltaTime * speed)
    }

    override fun getCollisionNormal(colPosition: Vec2): Vec2 {
        val distLeft = abs(colPosition.x - minX())
        val distRight = abs(colPosition.x - maxX())
        val distTop = abs(colPosition.y - minY())
        val distBottom = abs(colPosition.y - maxY())

        val minDist = min(min(distLeft, distRight), min(distTop, distBottom))

        return when (minDist) {
            distLeft -> Vec2.left
            distRight -> Vec2.right
            distTop -> Vec2.up
            else -> Vec2.down
        }
    }
}