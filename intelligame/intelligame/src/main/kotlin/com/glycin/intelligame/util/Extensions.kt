package com.glycin.intelligame.util

import com.glycin.intelligame.shared.Vec2
import com.intellij.openapi.editor.Editor
import java.awt.Point

private const val X_SNAP = 25

fun Editor.getPointOnCaret(offset: Int): Point {
    val p = offsetToXY(offset)
    val location = scrollingModel.visibleArea.location
    p.translate((-location.x) + X_SNAP, (-location.y) - 25)
    return Point(p.x, p.y)
}

fun Point.toVec2() = Vec2(x, y)

fun Point.toVec2(scrollOffset: Int) = Vec2(x, y + scrollOffset)

fun Vec2.toPoint() = Point(x, y)