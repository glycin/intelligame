package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.zonictestdog.CollisionsManager
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import kotlin.math.roundToInt

class WalkingEnemy(
    var position: Fec2,
    val width: Int,
    val height: Int,
) {
    var alive = true
    private var state = EnemyState.WALKING_RIGHT
    private var speed = 0.05f

    fun update(deltaTime: Long, colManager: CollisionsManager){
        when(state){
            EnemyState.WALKING_RIGHT -> {
                position += Fec2.right * (deltaTime * speed)
                if(colManager.shouldFall(position)){
                    state = EnemyState.WALKING_LEFT
                }
            }

            EnemyState.WALKING_LEFT -> {
                position += Fec2.left * (deltaTime * speed)
                if(colManager.shouldFall(position)){
                    state = EnemyState.WALKING_RIGHT
                }
            }

            EnemyState.DEAD -> {

            }
        }
    }

    fun draw(g: Graphics2D) {
        if(alive) {
            g.color = JBColor.RED
            g.fillRect(position.x.roundToInt(), position.y.roundToInt(), width, height)

            g.color = JBColor.GREEN.brighter()
            g.fillOval(getBottomPos().x.roundToInt(), getBottomPos().y.roundToInt(), 5, 5)
        }
    }

    private fun getBottomPos() = Fec2(position.x + (width / 2), position.y + height)

    private enum class EnemyState{
        WALKING_LEFT,
        WALKING_RIGHT,
        DEAD,
    }
}