package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toPoint
import com.glycin.intelligame.zonictestdog.level.Portal
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import java.awt.image.BufferedImage
import kotlin.math.roundToInt
import kotlin.random.Random

class PortalOpener(
    private val project: Project,
    private val ztdGame: ZtdGame,
) {
    private val maxPortals = 10
    private val portalSheet = SpriteSheetImageLoader("/Sprites/sheets/portals.png", 32, 48, 16).loadSprites()

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

    fun openPortals(method: PsiMethod, playerPos: Fec2) {
        val refs = ReferencesSearch.search(method).findAll()

        refs.forEachIndexed { index, ref ->
            val element = ref.element
            val containingFile = element.containingFile
            val textRange = ref.rangeInElement
            if(index < maxPortals) {
                val x = (playerPos.x + 150) * (if (index > 5) 1 else 2)
                val y = (playerPos.y - 400) + (150 * (index % 5))
                ztdGame.portals.add(
                    Portal(
                        position = Vec2(x.roundToInt(), y.roundToInt()),
                        height = 96,
                        width = 64,
                        file = containingFile,
                        element = element,
                        textRange = textRange,
                        sprites = getColoredPortal()
                    )
                )
            }
        }
    }

    fun travelToPortal(p : Portal) {
        ztdGame.travelTo(p)
    }

    private fun getColoredPortal(): Array<BufferedImage> {
        val randomColor = Random.nextInt(4)
        return when (randomColor) {
            0 -> portalSheet.take(4).toTypedArray()
            1 -> portalSheet.drop(4).take(4).toTypedArray()
            2 -> portalSheet.drop(8).take(4).toTypedArray()
            3 -> portalSheet.takeLast(4).toTypedArray()
            else -> portalSheet.take(4).toTypedArray()
        }
    }
}