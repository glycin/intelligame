package com.glycin.intelligame.codehero

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class CodeHeroPasteHandler(
    private val originalHandler: EditorActionHandler,
    private val game: CodeHeroGame,
): EditorActionHandler() {

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val contents = clipboard.getContents(null)

        if(contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            val text = contents.getTransferData(DataFlavor.stringFlavor) as String
            game.initGame(text)
        }else {
            originalHandler.execute(editor, caret, dataContext)
        }
    }
}