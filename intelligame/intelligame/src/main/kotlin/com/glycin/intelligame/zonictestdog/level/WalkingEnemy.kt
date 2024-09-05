package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.zonictestdog.CollisionsManager
import com.intellij.ui.JBColor
import org.jetbrains.intellij.build.SPACE_REPO_HOST
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class WalkingEnemy(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val sprites: List<BufferedImage>,
) {
    var alive = true
    private var state = EnemyState.WALKING_RIGHT
    private var speed = 0.05f

    private var currentIndex = 0
    private var frameHoldCount: Int = 0

    fun update(deltaTime: Long, colManager: CollisionsManager){
        when(state){
            EnemyState.WALKING_RIGHT -> {
                position += Vec2.right * (deltaTime * speed)
                if(colManager.shouldFall(getBottomPos())){
                    state = EnemyState.WALKING_LEFT
                }
            }

            EnemyState.WALKING_LEFT -> {
                position += Vec2.left * (deltaTime * speed)
                if(colManager.shouldFall(getBottomPos())){
                    state = EnemyState.WALKING_RIGHT
                }
            }

            EnemyState.DEAD -> {

            }
        }
    }

    fun draw(g: Graphics2D) {
        if(alive) {
            val sprite = sprites[currentIndex]
            if(state == EnemyState.WALKING_LEFT) {
                g.drawImage(sprite, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
            }else{
                g.drawImage(sprite, position.x.roundToInt(), position.y.roundToInt(), width, height, null)
            }

            frameHoldCount++

            if(frameHoldCount % 8 == 0) {
                currentIndex++
            }

            if(currentIndex >= sprites.size) {
                currentIndex = 0
                frameHoldCount = 0
            }

            //g.color = JBColor.GREEN.brighter()
            //g.fillOval(getBottomPos().x.roundToInt(), getBottomPos().y.roundToInt(), 5, 5)
        }
    }

    fun getBounds() = Rectangle(position.x.roundToInt(), position.y.roundToInt(), width, height)

    private fun getBottomPos() = Vec2(position.x + (width / 2), position.y + height)

    private enum class EnemyState{
        WALKING_LEFT,
        WALKING_RIGHT,
        DEAD,
    }
}