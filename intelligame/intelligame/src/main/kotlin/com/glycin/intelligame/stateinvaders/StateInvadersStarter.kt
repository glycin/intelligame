package com.glycin.intelligame.stateinvaders

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class StateInvadersStarter: IntentionAction, HighPriorityAction {
    override fun startInWriteAction(): Boolean = false

    override fun getText(): String = "Time to abolish the state!"

    override fun getFamilyName(): String = "IntelliGame"

    override fun isAvailable(p0: Project, p1: Editor?, p2: PsiFile?): Boolean = true

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        project.service<StateInvadersGame>().initGame(editor)
    }
}