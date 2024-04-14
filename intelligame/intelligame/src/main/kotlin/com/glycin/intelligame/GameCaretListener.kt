package com.glycin.intelligame

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.ex.EditorMarkupModel
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.IconLoader
import java.awt.Graphics
import javax.swing.Icon

class GameCaretListener(private val editor: Editor): CaretListener{
    private val spriteIcon: Icon = IconLoader.getIcon("/Sprites/knight.png", GameHighlighter::class.java)

    init {
        editor.caretModel.addCaretListener(this)
    }

    override fun caretPositionChanged(event: CaretEvent) {
        val markupModel = event.editor.markupModel as EditorMarkupModel
        val caretPos = event.caret!!.offset
        println("Caret changed")
        markupModel.removeAllHighlighters()
        val pos = event.editor.offsetToXY(caretPos)
        markupModel.addRangeHighlighter(caretPos, caretPos + 1, HighlighterLayer.LAST, null, HighlighterTargetArea.EXACT_RANGE).customRenderer = GameHighlighter(spriteIcon, pos.x, pos.y)
    }
}

private class GameHighlighter(
    private val icon: Icon,
    private val x: Int,
    private val y: Int,
): CustomHighlighterRenderer {
    override fun paint(editor: Editor, rangeHighlighter: RangeHighlighter, g: Graphics) {
        icon.paintIcon(editor.component, g, x, y)
    }
}