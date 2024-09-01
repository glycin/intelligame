package com.glycin.intelligame.shared

import kotlin.math.sqrt

class Vec2(
    val x: Float = 0.0F,
    val y: Float = 0.0F,
){
    companion object {
        val zero = Vec2(0f, 0f)
        val one = Vec2(1f, 1f)
        val up = Vec2(0f, -1f)
        val down = Vec2(0f, 1f)
        val left = Vec2(-1f, 0f)
        val right = Vec2(1f, 0f)

        fun distance(a: Vec2, b: Vec2): Float {
            val dx = (b.x - a.x).toFloat()
            val dy = (b.y - a.y).toFloat()
            return sqrt(dx * dx + dy * dy)
        }

        fun opposite(vec2: Vec2): Vec2 {
            return Vec2(-vec2.x, -vec2.y)
        }
    }

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Int) = Vec2(x * scalar, y * scalar)
    operator fun div(scalar: Int) = Vec2(x / scalar, y / scalar)

    operator fun plus(other: Float) = Vec2(x + other, y + other)
    operator fun minus(other: Float) = Vec2(x - other, y - other)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vec2(x / scalar, y / scalar)

    fun dot(other: Vec2) = x * other.x + y * other.y

    fun magnitude() = sqrt(x.toDouble() * x + y * y)

    fun normalized(): Vec2 {
        val mag = magnitude().toFloat()
        return if (mag != 0.0F) this / mag else zero
    }

    override fun toString(): String {
        return "Vec2($x,$y)"
    }
}