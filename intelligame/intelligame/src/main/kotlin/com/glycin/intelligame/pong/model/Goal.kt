package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor

class Goal(
    var position: Vec2,
    val width: Int = 10,
    val height: Int = 60,
    val goalIndex: Int = 0,
    val color: JBColor,
):CollisionObject{
    val minX = position.x
    val maxX = position.x + width
    val minY = position.y
    val maxY = position.y + height

    override fun getCollisionNormal(colPosition: Vec2): Vec2 {
        // We won't bounce so we can just return zero
        return Vec2.zero
    }
}