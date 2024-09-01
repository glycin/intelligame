package com.glycin.intelligame.util

import com.glycin.intelligame.shared.Vec2
import java.awt.Point
import kotlin.math.roundToInt

private const val X_SNAP = 25

fun Point.toVec2() = Vec2(x.toFloat(), y.toFloat())

fun Point.toVec2(scrollOffset: Int) = Vec2(x.toFloat(), y.toFloat() + scrollOffset)

fun Vec2.toPoint() = Point(x.roundToInt(), y.roundToInt())

fun Long.toDeltaTime() = 1000f / this

fun Long.toLongDeltaTime() = 1000L / this