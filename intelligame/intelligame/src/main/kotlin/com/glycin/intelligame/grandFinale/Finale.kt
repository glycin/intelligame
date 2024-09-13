package com.glycin.intelligame.grandFinale

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import mochadoom.Engine

class Finale(
    private val project: Project,
    private val scope: CoroutineScope
) {

    fun show(editor: Editor) {
        Engine.runFromFrame(editor.contentComponent)
    }
}