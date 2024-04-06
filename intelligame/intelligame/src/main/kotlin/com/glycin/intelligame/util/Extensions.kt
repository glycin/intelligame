package com.glycin.intelligame.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import java.awt.Point

fun Editor.getCaretVisualPosition() = caretModel.currentCaret.visualPosition

fun Editor.getPoint(): Point {
    val caretPosition = caretModel.offset
    val p = offsetToXY(caretPosition)
    val location = scrollingModel.visibleArea.location
    p.translate(-location.x, - location.y)
    return p
}