package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.CoroutineScope

@Service
class BoomService(private val scope: CoroutineScope) {

    fun kaboom(project: Project, editor: Editor){
        println("BOOM!!!")
        createLevel(editor)
    }

    fun createLevel(editor: Editor){

        val document = editor.document
        val obstacles = mutableListOf<ExplosionObject>()
        val l1 = editor.visualPositionToXY(VisualPosition(0, 0))
        val l2 = editor.visualPositionToXY(VisualPosition(1, 0))
        val lineHeight = l2.y - l1.y

        for(line in 0 until document.lineCount) {
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)

            val lineTextStartIndex = document.getText(TextRange(lineStartOffset, lineEndOffset)).indexOfFirst { !it.isWhitespace() }

            if(lineTextStartIndex == -1) {
                continue
            }

            val startLogicalPosition = LogicalPosition(line, lineTextStartIndex)

            val startPos = editor.logicalPositionToXY(startLogicalPosition).toVec2()
            val endPos = editor.offsetToXY(lineEndOffset).toVec2()
            val width = endPos.x - startPos.x
        }
    }
}