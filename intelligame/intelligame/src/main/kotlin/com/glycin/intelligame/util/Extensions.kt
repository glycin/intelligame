package com.glycin.intelligame.util

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.Vec2
import com.intellij.openapi.editor.Editor
import java.awt.Point
import kotlin.math.roundToInt

private const val X_SNAP = 25

fun Point.toVec2() = Vec2(x, y)

fun Point.toVec2(scrollOffset: Int) = Vec2(x, y + scrollOffset)

fun Vec2.toPoint() = Point(x, y)

fun Fec2.toPoint() = Point(x.roundToInt(), y.roundToInt())

fun Long.toDeltaTime() = 1000f / this

fun Long.toLongDeltaTime() = 1000L / this