package com.glycin.intelligame.stateinvaders

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class StateInvadersService(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private var game: StateInvadersGame? = null

    fun start(editor: Editor) {
        println("STARTED STATE INVADERS")
        game = StateInvadersGame(project, scope).apply { initGame(editor) }
    }

    fun stop() {
        game?.cleanUp()
        game = null
        println("STOPPED STATE INVADERS")
    }
}