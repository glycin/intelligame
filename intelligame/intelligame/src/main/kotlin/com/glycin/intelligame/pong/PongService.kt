package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.*
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager

private const val FPS = 120L

@Service
class PongService(private val scope: CoroutineScope) {

    private var state = GameState.IDLE

    fun initGame(editor: Editor) {
        println("GAME STARTED!")
        if(state == GameState.STARTED) { return }

        val obstacles = createLevel(editor)
        val maxWidth = obstacles.filter { it.width != editor.contentComponent.width }.maxOf { it.width }
        val (p1, p2) = spawnPlayers(editor, maxWidth)
        val (g1, g2) = createGoals(editor)
        val ball = spawnBall(editor, PongCollider(listOf(p1, p2), listOf(g1, g2), obstacles))

        attachGameToEditor(editor, obstacles, ball, p1, p2, g1, g2)
            .apply { start() }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(PongInput(p1, p2, editor.caretModel, FPS))
        state = GameState.STARTED
    }

    private fun attachGameToEditor(
        editor: Editor, obstacles: MutableList<Obstacle>, ball: Ball, player1: PlayerBrick, player2: PlayerBrick, g1: Goal, g2: Goal
    ): PongRenderer {
        val contentComponent = editor.contentComponent

        // Create and configure the Pong game component
        val pongRenderer = PongRenderer(obstacles, ball, player1, player2, g1, g2, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }

        // Add the Pong game component as an overlay
        contentComponent.add(pongRenderer)
        contentComponent.revalidate()
        contentComponent.repaint()

        // Request focus for the Pong game to ensure it receives key events
        pongRenderer.requestFocusInWindow()
        return pongRenderer
    }

    private fun createLevel(editor: Editor) : MutableList<Obstacle>  {
        val document = editor.document
        val obstacles = mutableListOf<Obstacle>()
        val l1 = editor.visualPositionToXY(VisualPosition(0, 0))
        val l2 = editor.visualPositionToXY(VisualPosition(1, 0))
        val lineHeight = l2.y - l1.y

        for(line in 0 until document.lineCount) {
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)

            val lineTextStartIndex = document.getText(TextRange(lineStartOffset, lineEndOffset)).indexOfFirst { !it.isWhitespace() }

            if(lineTextStartIndex == -1) {
                continue
            }

            val startLogicalPosition = LogicalPosition(line, lineTextStartIndex)

            val startPos = editor.logicalPositionToXY(startLogicalPosition).toVec2()
            val endPos = editor.offsetToXY(lineEndOffset).toVec2()
            val width = endPos.x - startPos.x

            obstacles.add(
                Obstacle(
                    position = startPos,
                    width = width.toInt(),
                    height = lineHeight,
                )
            )
        }
        // Top side of the map
        obstacles.add(
            Obstacle(
                position = Vec2(0.0f, 0.0f),
                width = editor.contentComponent.width,
                height = 5
            )
        )

        // Bottom side of the map
        obstacles.add(
            Obstacle(
                position = Vec2(0.0f, (editor.component.height - 5).toFloat()),
                width = editor.contentComponent.width,
                height = 5
            )
        )

        return obstacles
    }

    private fun spawnBall(editor: Editor, collider: PongCollider): Ball {
        val caretModel = editor.caretModel
        val position = editor.offsetToXY(caretModel.offset).toVec2()
        return Ball(
            position = position,
            collider = collider,
        )
    }

    private fun spawnPlayers(editor: Editor, maxWidth: Int): Pair<PlayerBrick, PlayerBrick> {
        val document = editor.document
        val line = document.getLineNumber(editor.caretModel.offset)
        val p1BrickPosition = editor.offsetToXY(document.getLineStartOffset(line) + 1)
        val p1 = PlayerBrick(p1BrickPosition.toVec2())
        val p2 = PlayerBrick(Vec2(p1.position.x + maxWidth + 100, p1.position.y))
        return p1 to p2
    }

    private fun createGoals(editor: Editor): Pair<Goal, Goal> {
        // Left side of the map
        val g1 = Goal(
            position = Vec2(0.0f, 0.0f),
            height = editor.component.height,
            goalIndex = 0,
            color = JBColor.BLUE,
        )

        // Right side of the map
        val g2 = Goal(
            position = Vec2(editor.contentComponent.width.toFloat(), 0.0f),
            height = editor.component.height,
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
                println("FIRST EMPTY LINE is $line")
            }
        }
    }
}