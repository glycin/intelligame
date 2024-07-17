package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toPoint
import com.glycin.intelligame.zonictestdog.level.Portal
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil

class PortalOpener(
    private val project: Project,
    private val colManager: CollisionsManager,
    private val ztdGame: ZtdGame,
) {
    fun isNearMethod(position: Fec2): Pair<Boolean, PsiMethod?> {
        val logicalPos = ztdGame.editor.xyToLogicalPosition(position.toPoint())
        val offset = ztdGame.editor.logicalPositionToOffset(logicalPos)
        PsiDocumentManager.getInstance(project).getPsiFile(ztdGame.editor.document)?.let { psiFile ->
            psiFile.findElementAt(offset)?.let { element ->
                PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)?.let { method ->
                    return (method.name == element.text) to method
                }
            }
        } ?: return false to null
    }

    fun openPortals(method: PsiMethod) {
        val refs = ReferencesSearch.search(method).findAll()
        println(refs.size)
        refs.forEachIndexed { index, ref ->
            val element = ref.element
            val containingFile = element.containingFile
            val textRange = ref.rangeInElement
            println("Reference found in file: ${containingFile.name} at range: ${textRange}")
            ztdGame.portals.add(
                Portal(
                    position = Vec2(100 + (150 * index), 100),
                    height = 100,
                    width = 100,
                    file = containingFile,
                    element = element,
                    textRange = textRange,
                    cm = colManager,
                    ztdGame = ztdGame,
                )
            )
        }
    }
}