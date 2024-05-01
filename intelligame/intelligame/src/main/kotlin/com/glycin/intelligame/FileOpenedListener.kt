package com.glycin.intelligame

import com.glycin.intelligame.services.GameService
import com.glycin.intelligame.services.InputService
import com.glycin.intelligame.services.PaintService
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.ui.GraphicsUtil
import java.awt.Graphics2D

class FileOpenedListener: FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        super.fileOpened(source, file)
        println("${file.name} opened")

        source.selectedTextEditor?.let {editor ->
            val caret = editor.caretModel.currentCaret

            GraphicsUtil.safelyGetGraphics(editor.component)?.let { graphics ->
                val g = graphics.create() as Graphics2D
                source.project.service<PaintService>().startRenderLoop(g)
                source.project.service<InputService>().init()
                source.project.service<GameService>().loadMap(editor, g, source.project.service<PaintService>())
            }

            PsiManager.getInstance(source.project).findFile(file)?.children?.let {
                it.firstOrNull { e -> e.text.lowercase().contains("class") }
            }?.let {psi ->
                caret.moveToOffset(psi.startOffset - 1)
            }
        }
    }
}