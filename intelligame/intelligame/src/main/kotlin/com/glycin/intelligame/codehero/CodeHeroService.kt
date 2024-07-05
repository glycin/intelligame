package com.glycin.intelligame.codehero

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class CodeHeroService(
    private val project: Project,
    private val scope: CoroutineScope
) {
    private var game: CodeHeroGame? = null

    fun initGame(editor: Editor){
        println("CODE HERO STARTED!")
        game = CodeHeroGame(editor, project, scope).apply { initGame() }
    }

    fun cleanUp() {
        game?.stopGame()
        game = null
        println("CODE HERO STOPPED!")
    }
}