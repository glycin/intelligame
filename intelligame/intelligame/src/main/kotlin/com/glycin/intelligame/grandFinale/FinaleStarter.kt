package com.glycin.intelligame.grandFinale

import com.glycin.intelligame.codehero.CodeHeroService
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class FinaleStarter: IntentionAction, HighPriorityAction {
    override fun startInWriteAction(): Boolean = false

    override fun getText(): String = "Grand finale"

    override fun getFamilyName(): String = "IntelliGame"

    override fun isAvailable(p0: Project, p1: Editor?, p2: PsiFile?): Boolean = true

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        project.service<FinaleService>().show(editor)
    }
}