package com.glycin.intelligame.boom

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class BoomService(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private var boom: BoomGame? = null

    fun start(editor: Editor) {
        println("KABOOM!")
        boom = BoomGame(project, scope).apply { kaboom(editor) }
    }

    fun stop() {
        boom?.stop()
        boom = null
        println("No more boom :(")
    }
}