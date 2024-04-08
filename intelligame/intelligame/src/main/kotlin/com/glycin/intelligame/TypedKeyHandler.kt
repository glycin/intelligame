package com.glycin.intelligame

import com.glycin.intelligame.services.GameService
import com.glycin.intelligame.services.PaintService
import com.glycin.intelligame.util.getCaretVisualPosition
import com.glycin.intelligame.util.getPoint
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.ui.GraphicsUtil
import java.awt.Graphics2D

class TypedKeyHandler: TypedHandlerDelegate() {

    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        val gameService = project.service<GameService>()

        if(gameService.fileOpened){
            project.service<PaintService>().updatePlayerPosition(editor.getPoint())
            val caret = editor.caretModel

            when(c){
                'd' -> caret.moveToOffset(caret.offset + 1)
                'a' -> caret.moveToOffset(caret.offset - 1)
                else -> println("Not supported")
            }

        }

        return Result.STOP
    }
}