package com.glycin.intelligame.boom

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project

class ExplosionGrid(
    private val project: Project,
    private val editor: Editor,
) {
    private val document = editor.document
    private val initialDocumentText = document.text

    val grid : MutableMap<LogicalPosition,Char> = mutableMapOf()

    fun initGrid() {
        val text = editor.document.text

        for(offset in text.indices) {
            val logicalPos = editor.offsetToLogicalPosition(offset)
            val character = text[offset]
            grid[logicalPos] = character
        }
    }

    fun getDebuPositions() : List<Vec2> {
        return grid.map { (t, u) ->
            editor.logicalPositionToXY(t).toVec2()
        }
    }

    fun updateText(){

    }

    fun writeText(){
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.replaceString(0, document.textLength, initialDocumentText.map { nextChar(it) }.joinToString(""))
                //val offset = editor.logicalPositionToOffset(editor.xyToLogicalPosition(b.position.toPoint()))
                //editor.document.insertString(offset, b.char) // I need to recalculate the text of the whole document in one go and instert it at once
            }
        }
    }

    private fun nextChar(c: Char): Char {
        return when (c) {
            in 'a'..'y' -> c + 1
            'z' -> 'a'
            in 'A'..'Y' -> c + 1
            'Z' -> 'A'
            else -> c
        }
    }
}