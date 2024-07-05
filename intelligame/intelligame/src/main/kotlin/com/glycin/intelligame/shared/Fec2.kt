package com.glycin.intelligame.shared

import kotlin.math.sqrt

class Fec2(
    val x: Float = 0.0F,
    val y: Float = 0.0F,
){
    companion object {
        val zero = Fec2(0f, 0f)
        val one = Fec2(1f, 1f)
        val up = Fec2(0f, -1f)
        val down = Fec2(0f, 1f)
        val left = Fec2(-1f, 0f)
        val right = Fec2(1f, 0f)

        fun distance(a: Fec2, b: Fec2 ): Float {
            val dx = (b.x - a.x).toFloat()
            val dy = (b.y - a.y).toFloat()
            return sqrt(dx * dx + dy * dy)
        }

        fun opposite(fec2: Fec2): Fec2 {
            return Fec2(-fec2.x, -fec2.y)
        }
    }

    operator fun plus(other: Fec2) = Fec2(x + other.x, y + other.y)
    operator fun minus(other: Fec2) = Fec2(x - other.x, y - other.y)
    operator fun times(scalar: Int) = Fec2(x * scalar, y * scalar)
    operator fun div(scalar: Int) = Fec2(x / scalar, y / scalar)

    operator fun plus(other: Float) = Fec2(x + other, y + other)
    operator fun minus(other: Float) = Fec2(x - other, y - other)
    operator fun times(scalar: Float) = Fec2(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Fec2(x / scalar, y / scalar)

    fun dot(other: Fec2) = x * other.x + y * other.y

    fun magnitude() = sqrt(x.toDouble() * x + y * y)

    fun normalized(): Fec2 {
        val mag = magnitude().toFloat()
        return if (mag != 0.0F) this / mag else zero
    }
}