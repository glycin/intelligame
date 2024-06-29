package com.glycin.intelligame.shared

import kotlin.math.roundToInt
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

        fun distance(a: Vec2, b: Vec2 ): Float {
            val dx = (b.x - a.x).toFloat()
            val dy = (b.y - a.y).toFloat()
            return sqrt(dx * dx + dy * dy)
        }

        fun opposite(vec2: Vec2): Vec2 {
            return Vec2(-vec2.x, -vec2.y)
        }
    }

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun plus(other: Float) = Vec2(x + other.roundToInt(), y + other.roundToInt())
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun minus(other: Float) = Vec2(x - other.roundToInt(), y - other.roundToInt())
    operator fun times(scalar: Int) = Vec2(x * scalar, y * scalar)
    operator fun times(scalar: Float) = Vec2(x * scalar.roundToInt(), y * scalar.roundToInt())
    operator fun div(scalar: Int) = Vec2(x / scalar, y / scalar)
    operator fun div(scalar: Float) = Vec2(x / scalar.roundToInt(), y / scalar.roundToInt())

    fun dot(other: Vec2) = x * other.x + y * other.y

    fun magnitude() = sqrt(x.toDouble() * x + y * y).toInt()

    fun normalized(): Vec2 {
        val mag = magnitude()
        return if (mag != 0) this / mag else zero
    }
}