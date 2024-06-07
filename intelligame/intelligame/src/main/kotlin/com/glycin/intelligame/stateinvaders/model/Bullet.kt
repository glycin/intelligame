package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
import kotlin.math.roundToInt

class Bullet(
    var position: Vec2,
    val width: Int,
    val height: Int,
    private val direction: Vec2,
    private val speed: Int = 3
) {
    fun move(deltaTime: Float) {
        position += direction * (deltaTime * speed).roundToInt()
    }
}