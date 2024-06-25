package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.CoroutineScope

private const val FPS = 120L

//TODO: Add cleanup
@Service(Service.Level.PROJECT)
class BoomGame(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private val explObjects = mutableListOf<ExplosionObject>()

    fun kaboom(editor: Editor){
        println("BOOM!!!")
        explObjects.addAll(createLevel(editor))
        attachGameToEditor(editor, project, explObjects).apply { start() }
    }

    private fun attachGameToEditor(
        editor: Editor, project: Project, booms: List<ExplosionObject>
    ): BoomComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true

        val state = ExplosionWriter(project, editor)
        val boomComponent = BoomComponent(booms, state, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }

        contentComponent.add(boomComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        boomComponent.requestFocusInWindow()
        return boomComponent
    }

    private fun createLevel(editor: Editor): List<ExplosionObject> {

        val document = editor.document
        val boomies = mutableListOf<ExplosionObject>()
        val lineHeight = editor.lineHeight
        val scrollOffset = editor.scrollingModel.verticalScrollOffset
        return GraphicsUtil.safelyGetGraphics(editor.component)?.let { graphics ->
            for(line in 0 until document.lineCount) {
                val lineStartOffset = document.getLineStartOffset(line)
                val lineEndOffset = document.getLineEndOffset(line)

                val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

                lineText.forEachIndexed { index, c ->
                    if(!c.isWhitespace()){
                        val charLogicalPosition = LogicalPosition(line, index)
                        val charPos = editor.logicalPositionToXY(charLogicalPosition).toVec2(scrollOffset)
                        val nextCharOffset = lineStartOffset + index + 1

                        val charWidth = if (nextCharOffset < lineEndOffset) {
                            val nextCharPos = editor.offsetToXY(nextCharOffset)
                            (nextCharPos.x - charPos.x)
                        } else {
                            graphics.fontMetrics.charWidth(c) * 2 //The charWidth() width is always 2 small so we just make it bigger ¯\_(ツ)_/¯
                        }

                        boomies.add(
                            ExplosionObject(
                                position = charPos,
                                width = charWidth,
                                height = lineHeight,
                                char = c.toString(),
                            )
                        )
                    }
                }
            }
            boomies
        } ?: emptyList()
    }
}