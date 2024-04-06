package com.glycin.intelligame

import com.glycin.intelligame.services.GameService
import com.intellij.openapi.components.service
import com.intellij.openapi.components.services
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.application
import com.intellij.util.ui.GraphicsUtil
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.BufferedInputStream
import javax.imageio.ImageIO

class FileOpenedListener: FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        super.fileOpened(source, file)
        println("${file.name} opened")

        source.selectedTextEditor?.let {editor ->
            val caret = editor.caretModel.currentCaret
            source.project.service<GameService>().also {
                it.fileOpened = true
            }

            PsiManager.getInstance(source.project).findFile(file)?.children?.let {
                it.firstOrNull { e -> e.text.lowercase().contains("class") }
            }?.let {psi ->
                caret.moveToOffset(psi.startOffset - 1)
            }
        }
    }
}