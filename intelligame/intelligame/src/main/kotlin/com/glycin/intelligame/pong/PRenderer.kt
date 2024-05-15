package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.Ball
import com.glycin.intelligame.pong.model.Obstacle
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import javax.swing.JPanel

private const val FPS = 60L

// TODO: Investigate if i can overlay the original jpanel with a new one to create the illusion we are in the same component but in reality we are not
class PRenderer(
    val obstacles: MutableList<Obstacle>,
    val ball: Ball,
): JPanel() {

    init {
        // Set preferred size to ensure the component's size is adequate for drawing
        preferredSize = java.awt.Dimension(1500, 1500)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        println("painting")
        if (g == null) return
        g.clearRect(0, 0, 1500, 1500)
        drawBall(g as Graphics2D)
        drawObstacles(g)
    }

    fun updatePos(){
        println("updating")
        ball.move((1000L/ FPS).toFloat())
        repaint()
    }

    private fun drawBall(g: Graphics2D) {
        g.color = JBColor.RED
        g.fillOval(ball.position.x.toInt(), ball.position.y.toInt(), ball.radius, ball.radius)
    }

    private fun drawObstacles(g: Graphics2D) {
        obstacles.forEach { obstacle ->
            g.color = Gray._255
            g.drawRect(obstacle.position.x.toInt(), obstacle.position.y.toInt(), obstacle.width, obstacle.height)
        }
    }
}