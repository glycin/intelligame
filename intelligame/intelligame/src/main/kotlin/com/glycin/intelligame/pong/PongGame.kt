package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.*
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager

private const val FPS = 120L

//TODO: Add cleanup
@Service(Service.Level.PROJECT)
class PongGame(
    private val project: Project,
    private val scope: CoroutineScope
) {

    private var state = GameState.IDLE
    private var score1 = 0
    private var score2 = 0

    private var textLine: Int = 0
    private lateinit var openDocument: Document
    private lateinit var openProject: Project

    fun initGame(editor: Editor) {
        println("PONG STARTED!")
        if(state == GameState.STARTED) { return }
        openProject = project

        val obstacles = createLevel(editor)
        val maxWidth = obstacles.filter { it.width != editor.contentComponent.width }.maxOf { it.width }
        val (p1, p2) = spawnPlayers(editor, maxWidth)
        val (g1, g2) = createGoals(editor)
        val ball = spawnBall(editor, PongCollider(listOf(p1, p2), listOf(g1, g2), obstacles))
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(PongInput(p1, p2, editor.caretModel, FPS))

        attachGameToEditor(editor, obstacles, ball, p1, p2, g1, g2)
            .apply { start() }

        writeScore(editor)
        openDocument = editor.document
        state = GameState.STARTED
    }

    fun updateScore(index: Int) {
        openDocument.getLineStartOffset(textLine)
        if(index == 0) score1++ else score2++
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(openProject) {
                openDocument.replaceString(openDocument.getLineStartOffset(textLine), openDocument.getLineEndOffset(textLine), """
                        // **************************************** Player One: $score1 - $score2 :Player Two **************************************** // 
                    """.trimIndent())
            }
        }
    }

    private fun attachGameToEditor(
        editor: Editor, obstacles: MutableList<Obstacle>, ball: Ball, player1: PlayerBrick, player2: PlayerBrick, g1: Goal, g2: Goal
    ): PongComponent {
        val contentComponent = editor.contentComponent

        // Create and configure the Pong game component
        val pongComponent = PongComponent(obstacles, ball, player1, player2, g1, g2, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }

        // Add the Pong game component as an overlay
        contentComponent.add(pongComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        // Request focus for the Pong game to ensure it receives key events
        pongComponent.requestFocusInWindow()
        return pongComponent
    }

    private fun createLevel(editor: Editor) : MutableList<Obstacle>  {
        val document = editor.document
        val obstacles = mutableListOf<Obstacle>()
        val lineHeight = editor.lineHeight
        val scrollOffset = editor.scrollingModel.verticalScrollOffset

        for(line in 0 until document.lineCount) {
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)

            val lineTextStartIndex = document.getText(TextRange(lineStartOffset, lineEndOffset)).indexOfFirst { !it.isWhitespace() }

            if(lineTextStartIndex == -1) {
                continue
            }

            val startLogicalPosition = LogicalPosition(line, lineTextStartIndex)

            val startPos = editor.logicalPositionToXY(startLogicalPosition).toVec2(scrollOffset)
            val endPos = editor.offsetToXY(lineEndOffset).toVec2(scrollOffset)
            val width = endPos.x - startPos.x

            obstacles.add(
                Obstacle(
                    position = startPos,
                    width = width,
                    height = lineHeight,
                )
            )
        }

        // Top side of the map
        obstacles.add(
            Obstacle(
                position = Vec2(0, scrollOffset), //TODO: For some reason here the offset resets to 0 or something
                width = editor.contentComponent.width,
                height = 5
            )
        )

        // Bottom side of the map
        obstacles.add(
            Obstacle(
                position = Vec2(0, (editor.component.height + (scrollOffset - 5))),
                width = editor.contentComponent.width,
                height = 5
            )
        )

        return obstacles
    }

    private fun spawnBall(editor: Editor, collider: PongCollider): Ball {
        val caretModel = editor.caretModel
        val scrollOffset = editor.scrollingModel.verticalScrollOffset
        val position = editor.offsetToXY(caretModel.offset).toVec2(scrollOffset)
        return Ball(
            position = position,
            collider = collider,
            service = this,
        )
    }

    private fun spawnPlayers(editor: Editor, maxWidth: Int): Pair<PlayerBrick, PlayerBrick> {
        val document = editor.document
        val scrollOffset = editor.scrollingModel.verticalScrollOffset
        val line = document.getLineNumber(editor.caretModel.offset)
        val p1BrickPosition = editor.offsetToXY(document.getLineStartOffset(line) + 1)
        val p1 = PlayerBrick(p1BrickPosition.toVec2(scrollOffset))
        val p2 = PlayerBrick(Vec2(p1.position.x + maxWidth + 100, p1.position.y))
        return p1 to p2
    }

    private fun createGoals(editor: Editor): Pair<Goal, Goal> {
        //val scrollOffset = editor.component.bounds.height

        // Left side of the map
        val g1 = Goal(
            position = Vec2.zero,
            height = editor.contentComponent.height,
            goalIndex = 0,
            color = JBColor.BLUE,
        )

        // Right side of the map
        val g2 = Goal(
            position = Vec2(editor.contentComponent.width - 50, 0),
            height = editor.contentComponent.height,
            goalIndex = 1,
            color = JBColor.GREEN,
        )
        return g1 to g2
    }

    private fun writeScore(editor: Editor){
        val document = editor.document
        for(line in 0 until document.lineCount) {
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)

            if(document.getText(TextRange(lineStartOffset, lineEndOffset)).isEmpty()){
                textLine = line
                WriteCommandAction.runWriteCommandAction(openProject) {
                    document.insertString(lineStartOffset, """
                        // **************************************** Player One: $score1 - $score2 :Player Two **************************************** // 
                    """.trimIndent())
                }
                return
            }
        }
    }
}