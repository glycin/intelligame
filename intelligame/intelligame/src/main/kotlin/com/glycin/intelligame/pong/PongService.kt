package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.*
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.util.TextRange
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.CoroutineScope
import java.awt.Graphics2D
import java.awt.KeyboardFocusManager

private const val FPS = 60L

@Service
class PongService(private val scope: CoroutineScope) {

    private var state = GameState.IDLE

    private lateinit var renderer: PongRenderer

    fun initGame(editor: Editor) {
        println("GAME STARTED!")
        if(state == GameState.STARTED) { return }

        val graphics = GraphicsUtil.safelyGetGraphics(editor.contentComponent) as Graphics2D

        val obstacles = createLevel(editor)
        val (p1, p2) = spawnPlayers(editor, obstacles.maxOf { it.width })
        val ball = spawnBall(editor, PongCollider(listOf(p1, p2), obstacles))

        renderer = PongRenderer(obstacles, ball, p1, p2, scope, graphics, FPS)

        renderer.init(editor.contentComponent)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(PongInput(p1, p2, editor.caretModel, FPS))

        //TODO: Trial implementation using a JPanel... not working atm
        /*val pr = PRenderer(obstacles, ball)
        editor.contentComponent.add(pr)
        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()
        scope.launch {
            while (true) {
                pr.updatePos()
                delay(1000 / 25)
            }
        }*/
        state = GameState.STARTED
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
                height = 1
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
        val p2 = PlayerBrick(Vec2(p1.position.x + maxWidth + 100.0f, p1.position.y))
        return p1 to p2
    }
}