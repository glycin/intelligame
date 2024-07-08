package com.glycin.intelligame.codehero

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class CodeHeroPasteHandler(
    val originalHandler: EditorActionHandler,
    private val game: CodeHeroGame,
): EditorActionHandler() {

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val contents = clipboard.getContents(null)

        if(contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor) && game.gameState.state == CodeHeroStateEnum.STARTED) {
            val text = contents.getTransferData(DataFlavor.stringFlavor) as String
            game.initIntro(text)
        } else if(game.gameState.state == CodeHeroStateEnum.PLAYING) {
          //Do nothing for now
        } else {
            originalHandler.execute(editor, caret, dataContext)
        }
    }
}