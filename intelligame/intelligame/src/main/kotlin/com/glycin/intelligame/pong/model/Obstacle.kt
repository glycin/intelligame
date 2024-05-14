package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2

data class Obstacle(
    val position: Vec2,
    val width: Int,
    val height: Int,
)
