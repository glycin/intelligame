package com.glycin.intelligame.packageman

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class PackmanService(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private var game: PackmanGame? = null

    fun initGame(editor: Editor){
        println("PACKAGE MAN STARTED!")
        game = PackmanGame(editor, project, scope).apply { startGame() }
    }

    fun cleanUp() {
        game = null
    }
}