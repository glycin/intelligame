package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.Ball
import com.glycin.intelligame.pong.model.Goal
import com.glycin.intelligame.pong.model.Obstacle
import com.glycin.intelligame.pong.model.PlayerBrick
import com.intellij.openapi.application.EDT
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import kotlin.math.roundToInt

class PongRenderer(
    private val obstacles: MutableList<Obstacle>,
    private val ball: Ball,
    private val p1Brick: PlayerBrick,
    private val p2Brick: PlayerBrick,
    private val goal1: Goal,
    private val goal2: Goal,
    private val scope: CoroutineScope,
    fps : Long,
): JComponent() {

    private val deltaTime = 1000L / fps

    fun start() {
        isFocusable = true
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            drawObstacles(g)
            drawGoals(g)
            drawPlayers(g)
            drawBall(g)
        }
    }

    private fun drawBall(g: Graphics2D) {
        g.color = JBColor.RED
        g.fillOval(ball.position.x.roundToInt(), ball.position.y.roundToInt(), ball.radius, ball.radius)
        ball.move(deltaTime.toFloat())
    }

    private fun drawObstacles(g: Graphics2D) {
        obstacles.forEach { obstacle ->
            g.color = Gray._255
            g.drawRect(obstacle.position.x.roundToInt(), obstacle.position.y.roundToInt(), obstacle.width, obstacle.height)
        }
    }

    private fun drawPlayers(g: Graphics2D) {
        g.color = JBColor.BLUE
        g.fillRect(p1Brick.position.x.roundToInt(), p1Brick.position.y.roundToInt(), p1Brick.width, p1Brick.height)

        g.color = JBColor.GREEN
        g.fillRect(p2Brick.position.x.toInt(), p2Brick.position.y.toInt(), p2Brick.width, p2Brick.height)
    }

    private fun drawGoals(g: Graphics2D) {
        g.color = goal1.color
        g.fillRect(goal1.position.x.roundToInt(), goal1.position.y.roundToInt(), goal1.width, goal1.height)

        g.color = goal2.color
        g.fillRect(goal2.position.x.toInt(), goal2.position.y.toInt(), goal2.width, goal2.height)
    }
}