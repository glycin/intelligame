package com.glycin.intelligame.grandFinale

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class FinaleService(
    private val project: Project,
    private val scope: CoroutineScope
) {
    var finale : Finale? = null

    fun show(editor: Editor) {
        finale = Finale(project, scope)
        finale?.show(editor)
    }

    fun stop() {
        finale = null
    }
}