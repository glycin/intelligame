package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.Obstacle
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.getPointOnCaret
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.childrenOfType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.Gray
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FPS = 25L

@Service
class PongService(private val scope: CoroutineScope) {

    fun initGame(editor: Editor) {
        println("GAME STARTED!")
        val obstacles = createLevel(editor)
        val graphics = GraphicsUtil.safelyGetGraphics(editor.contentComponent)
        val renderer = PongRenderer()

        scope.launch {
            while (true) {
                obstacles.forEach { obstacle ->
                    graphics?.color = Gray._255
                    graphics?.drawRect(obstacle.position.x.toInt(), obstacle.position.y.toInt(), obstacle.width, obstacle.height)
                }
                delay(1000L / FPS)
            }
        }
    }

    private fun createLevel(editor: Editor) : List<Obstacle>  {
        val document = editor.document
        val obstacles = mutableListOf<Obstacle>()
        val l1 = editor.visualPositionToXY(VisualPosition(0, 0))
        val l2 = editor.visualPositionToXY(VisualPosition(1, 0))
        val lineHeight = l2.y - l1.y

        for(line in 0 until document.lineCount) {
            val startOffset = document.getLineStartOffset(line)
            val endOffset = document.getLineEndOffset(line)

            val startPos = editor.offsetToXY(startOffset).toVec2()
            val endPos = editor.offsetToXY(endOffset).toVec2()
            val width = endPos.x - startPos.x

            obstacles.add(
                Obstacle(
                    position = startPos,
                    width = width.toInt(),
                    height = lineHeight,
                )
            )
        }
        return obstacles
    }
}