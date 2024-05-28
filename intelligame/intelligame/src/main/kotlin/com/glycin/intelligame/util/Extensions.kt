package com.glycin.intelligame.util

import com.glycin.intelligame.shared.Vec2
import com.intellij.openapi.editor.Editor
import java.awt.Point

private const val X_SNAP = 25
private const val Y_SNAP = 50

fun Editor.getCaretVisualPosition() = caretModel.currentCaret.visualPosition

fun Editor.getPointAboveCaret(): Point {
    val caretPosition = caretModel.offset
    val p = offsetToXY(caretPosition)
    val location = scrollingModel.visibleArea.location
    p.translate((-location.x) + X_SNAP, (-location.y) - Y_SNAP)
    return p
}

fun Editor.getPointOnCaret(offset: Int): Point {
    val p = offsetToXY(offset)
    val location = scrollingModel.visibleArea.location
    p.translate((-location.x) + X_SNAP, (-location.y) - 25)
    return Point(p.x, p.y)
}

fun Point.toVec2() = Vec2(x, y)