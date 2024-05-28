package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.CoroutineScope

private const val FPS = 120L

@Service
class BoomService(private val scope: CoroutineScope) {

    fun kaboom(project: Project, editor: Editor){
        println("BOOM!!!")
        val booms = createLevel(editor)
        attachGameToEditor(editor, booms).apply { start() }
    }

    private fun attachGameToEditor(
        editor: Editor, booms: List<ExplosionObject>
    ): BoomRenderer {
        val contentComponent = editor.contentComponent

        val boomRenderer = BoomRenderer(booms, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }

        contentComponent.add(boomRenderer)
        contentComponent.revalidate()
        contentComponent.repaint()

        boomRenderer.requestFocusInWindow()
        return boomRenderer
    }

    private fun createLevel(editor: Editor): List<ExplosionObject> {

        val document = editor.document
        val boomies = mutableListOf<ExplosionObject>()
        val l1 = editor.visualPositionToXY(VisualPosition(0, 0))
        val l2 = editor.visualPositionToXY(VisualPosition(1, 0))
        val lineHeight = l2.y - l1.y
        return GraphicsUtil.safelyGetGraphics(editor.component)?.let { graphics ->
            for(line in 0 until document.lineCount) {
                val lineStartOffset = document.getLineStartOffset(line)
                val lineEndOffset = document.getLineEndOffset(line)

                val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

                lineText.forEachIndexed { index, c ->
                    if(!c.isWhitespace()){
                        val charLogicalPosition = LogicalPosition(line, index)
                        val charPos = editor.logicalPositionToXY(charLogicalPosition).toVec2()
                        val nextCharOffset = lineStartOffset + index + 1

                        val charWidth = if (nextCharOffset < lineEndOffset) {
                            val nextCharPos = editor.offsetToXY(nextCharOffset)
                            (nextCharPos.x - charPos.x).toInt()
                        } else {
                            graphics.fontMetrics.charWidth(c) * 2 //The charWidth() width is always 2 small so we just make it bigger ¯\_(ツ)_/¯
                        }

                        boomies.add(
                            ExplosionObject(
                                position = charPos,
                                width = charWidth,
                                height = lineHeight,
                            )
                        )
                    }
                }
            }
            boomies
        } ?: emptyList()
    }
}