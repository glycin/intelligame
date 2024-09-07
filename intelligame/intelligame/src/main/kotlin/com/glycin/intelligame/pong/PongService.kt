package com.glycin.intelligame.pong

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class PongService(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private var pongGame: PongGame? = null

    fun start(editor: Editor) {
        pongGame = PongGame(project, scope).apply { initGame(editor) }
    }

    fun stop() {
        pongGame?.stop()
        pongGame = null
    }
}