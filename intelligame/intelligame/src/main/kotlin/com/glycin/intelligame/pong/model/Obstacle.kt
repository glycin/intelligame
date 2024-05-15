package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2

data class Obstacle(
    val position: Vec2,
    val width: Int,
    val height: Int,
){
    private val minX = position.x - width / 2
    private val maxX = position.x + width / 2
    private val minY = position.y - height / 2
    private val maxY = position.y + height / 2

    val topLeft = Vec2(minX, maxY)
    val topRight = Vec2(maxX, maxY)
    val bottomLeft = Vec2(minX, minY)
    val bottomRight = Vec2(maxX, minY)
}
