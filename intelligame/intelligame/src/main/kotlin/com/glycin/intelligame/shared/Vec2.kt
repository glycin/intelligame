package com.glycin.intelligame.shared

import kotlin.math.sqrt

data class Vec2(
    val x: Float = 0f,
    val y: Float = 0f,
) {
    
    companion object {
        val zero = Vec2(0f, 0f)
        val one = Vec2(1f, 1f)
        val up = Vec2(0f, 1f)
        val down = Vec2(0f, -1f)
        val left = Vec2(-1f, 0f)
        val right = Vec2(1f, 0f)
    }

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vec2(x / scalar, y / scalar)

    fun dot(other: Vec2) = x * other.x + y * other.y

    fun magnitude() = sqrt(x * x + y * y)

    fun normalized(): Vec2 {
        val mag = magnitude()
        return if (mag != 0f) this / mag else zero
    }
}