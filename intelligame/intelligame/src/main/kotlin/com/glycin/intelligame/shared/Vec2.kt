package com.glycin.intelligame.shared

import kotlin.math.sqrt

data class Vec2(
    val x: Int = 0,
    val y: Int = 0,
) {
    
    companion object {
        val zero = Vec2(0, 0)
        val one = Vec2(1, 1)
        val up = Vec2(0, -1)
        val down = Vec2(0, 1)
        val left = Vec2(-1, 0)
        val right = Vec2(1, 0)
    }

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Int) = Vec2(x * scalar, y * scalar)
    operator fun div(scalar: Int) = Vec2(x / scalar, y / scalar)

    fun dot(other: Vec2) = x * other.x + y * other.y

    fun magnitude() = sqrt(x.toDouble() * x + y * y).toInt()

    fun normalized(): Vec2 {
        val mag = magnitude()
        return if (mag != 0) this / mag else zero
    }
}