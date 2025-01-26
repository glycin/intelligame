package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.glycin.intelligame.util.toPoint
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project

class ExplosionWriter(
    private val project: Project,
    private val editor: Editor,
) {
    private val document = editor.document

    fun writeText(objs: List<ExplosionObject>){
        val logicalPositions = objs.associateBy { editor.xyToLogicalPosition(it.position.toPoint()) }
        val maxLine = logicalPositions.keys.maxOf { it.line }
        val maxColumn = logicalPositions.keys.maxOf { it.column }
        val sb = StringBuilder()

        for (line in 0 until maxLine) {
            for (column in 0 until maxColumn) {
                val foundPos = logicalPositions[LogicalPosition(line, column)]
                sb.append(foundPos?.char ?: " ")
            }
            sb.append("\n")
        }

        val s = sb.toString()

        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.insertString(0, s)
            }
        }
    }

    fun clear() {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.deleteString(0, document.textLength)
            }
        }
    }
}