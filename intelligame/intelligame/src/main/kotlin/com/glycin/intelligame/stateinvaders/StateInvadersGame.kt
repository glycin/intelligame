package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.Stalien
import com.intellij.codeInsight.completion.AllClassesGetter
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor

private const val FPS = 120L

@Service
class StateInvadersGame {

    fun initGame(project: Project, editor: Editor) {
        println("STATE INVADERS STARTED")
        val o = collectMutableFields(project).map { field ->
            Stalien(
                position = Vec2.zero,
                width = field.text.length * 5,
                height = editor.lineHeight,
                text = field.text,
            )
        }
    }

    private fun collectMutableFields(project: Project) : List<PsiField> {
        val allFields = mutableListOf<PsiField>()

        val processor = Processor<PsiClass> { psiClass ->
            psiClass.fields.forEach { field ->
                if(!field.hasModifierProperty(PsiModifier.FINAL)) {
                    allFields.add(field)
                }
            }
            true
        }

        AllClassesGetter.processJavaClasses(
            PlainPrefixMatcher(""),
            project,
            GlobalSearchScope.projectScope(project),
            processor
        )

        return allFields
    }
}