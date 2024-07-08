package com.glycin.intelligame.zonictestdog

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class ZtdService(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private var game: ZtdGame? = null

    fun initGame(editor: Editor){
        println("ZONIC THE TESTDOG STARTED!")
        game = ZtdGame(editor, project, scope).apply { initGame() }
    }

    fun cleanUp() {
        game?.stopGame()
        game = null
        println("ZONIC THE TESTDOG STARTED!")
    }
}