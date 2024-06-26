package com.glycin.intelligame.shared

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

object TextWriter {

    fun writeText(offset: Int, text: String, editor: Editor, project: Project) {
        val document = editor.document
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                document.insertString(offset, text)
            }
        }
    }

    fun replaceText(startOffset: Int, endOffset: Int, text: String, editor: Editor, project: Project ) {
        val document = editor.document
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(startOffset, endOffset, text)
            }
        }
    }

    fun deleteText(startOffset: Int, endOffset: Int, editor: Editor, project: Project ) {
        val document = editor.document
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                document.deleteString(startOffset, endOffset)
            }
        }
    }

    fun replaceTextAndThen(startOffset: Int, endOffset: Int, text: String, editor: Editor, project: Project, onComplete: () -> Unit) {
        val document = editor.document
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(startOffset, endOffset, text)
                onComplete()
            }
        }
    }
}