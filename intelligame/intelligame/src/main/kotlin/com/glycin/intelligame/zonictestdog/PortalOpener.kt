package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.util.toPoint
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil

class PortalOpener(
    private val editor: Editor,
    private val project: Project,
) {
    fun isNearMethod(position: Fec2): Pair<Boolean, PsiMethod?> {
        val logicalPos = editor.xyToLogicalPosition(position.toPoint())
        val offset = editor.logicalPositionToOffset(logicalPos)
        PsiDocumentManager.getInstance(project).getPsiFile(editor.document)?.let { psiFile ->
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
        refs.forEach { ref ->
            val element = ref.element
            val containingFile = element.containingFile
            val textRange = ref.rangeInElement
            println("Reference found in file: ${containingFile.name} at range: ${textRange}")

        }
    }
}