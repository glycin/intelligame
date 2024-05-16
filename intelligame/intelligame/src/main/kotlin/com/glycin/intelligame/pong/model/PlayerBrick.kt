package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import kotlin.math.abs
import kotlin.math.min


class PlayerBrick(
    var position: Vec2,
    val width: Int = 10,
    val height: Int = 40,
): CollisionObject {
    val minX = position.x - width / 2
    val maxX = position.x + width / 2
    val minY = position.y - height / 2
    val maxY = position.y + height / 2

    private val speed: Float = 0.4f

    fun moveUp(deltaTime: Float) {
        position -= Vec2.up * (deltaTime * speed)
    }

    fun moveDown(deltaTime: Float) {
        position -= Vec2.down * (deltaTime * speed)
    }

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