package com.glycin.intelligame

import com.glycin.intelligame.services.GameService
import com.glycin.intelligame.services.PaintService
import com.glycin.intelligame.util.getPointAboveCaret
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.ui.GraphicsUtil
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class TypedKeyHandler: TypedHandlerDelegate() {

    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        val gameService = project.service<GameService>()

        if(gameService.fileOpened){
            val caret = editor.caretModel

            when(c){
                'd' -> caret.moveToOffset(caret.offset + 1)
                'a' -> caret.moveToOffset(caret.offset - 1)
                else -> println("Not supported")
            }

            project.service<PaintService>().updatePlayerPosition(editor.component, GraphicsUtil.safelyGetGraphics(editor.component)?.create() as Graphics2D, editor.getPointAboveCaret())

            //TODO: Do i need the font width and height
            /*GraphicsUtil.safelyGetGraphics(editor.component)?.let { graphics ->
                val g = graphics.create() as Graphics2D
                //val width = g.fontMetrics.stringWidth(editor.document.getText(TextRange(caret.offset, caret.offset + 1)))
                //val height = g.fontMetrics.height * editor.document.getLineNumber(caret.offset)
            }*/
        }

        return Result.STOP
    }
}