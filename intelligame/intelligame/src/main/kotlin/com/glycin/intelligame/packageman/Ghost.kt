package com.glycin.intelligame.packageman

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.roundToInt

class Ghost(
    var position: Vec2,
    val width: Int,
    val height: Int,
    private var cellX: Int,
    private var cellY: Int,
    private val color: Color,
    private val mazeMoveManager: MazeMovementManager,
    private val deltaTime: Float,
) {
    private val speed = 1
    private val eyeWidth: Int = 2
    private val eyeHeight: Int = 3
    private val eyeXOffset: Int = 2
    private val eyeYOffset: Int = 2

    private var startDelay = 0
    private var movingStartDelay = 50
    private var moving = false
    private var moveDirection = Vec2.up

    private var movingFrame = 30
    private var movingCooldown = 30

    fun moveAndRender(g: Graphics2D) {
        move()

        g.color = color
        g.fillRoundRect(position.x, position.y, width, height - 5, 5, 5)

        val xPoints = arrayOf(position.x, position.x + 2, position.x + 5, position.x + 7, position.x + width).toIntArray()
        val yPoints = arrayOf(
            position.y + height - 5,
            position.y + height,
            position.y + height - 5,
            position.y + height,
            position.y + height - 5,
        ).toIntArray()

        g.fillPolygon(xPoints, yPoints, xPoints.size)
        g.color = JBColor.WHITE.brighter().brighter().brighter().brighter()
        g.fillOval(position.x + eyeXOffset, position.y + eyeYOffset, eyeWidth, eyeHeight);
        g.fillOval(position.x + width - eyeXOffset - eyeWidth, position.y + eyeYOffset, eyeWidth, eyeHeight);
    }

    private fun move(){

        if(startDelay < movingStartDelay) {
            startDelay++
            return
        }else if(startDelay == movingStartDelay) {
            moving = true
            startDelay++
            moveDirection = Vec2.up
            setMoving()
        }

        if(movingFrame < movingCooldown) {
            movingFrame++
            return
        }

        if(moving){
            val target = mazeMoveManager.getMazeCellPosition(cellX, cellY)
            val shouldStop = when(moveDirection){
                Vec2.left -> position.x <= target.x
                Vec2.right -> position.x >= target.x
                Vec2.down -> position.y >= target.y
                Vec2.up -> position.y <= target.y
                else -> false
            }

            if(!shouldStop){
                position += moveDirection * (deltaTime * speed).roundToInt()
            }else {
                setMoving()
            }
            movingFrame = 0
        }
    }

    private fun setMoving() {
        val potentials = listOf(
            RandomDirection(cellX - 1, cellY, Vec2.left, mazeMoveManager),
            RandomDirection(cellX + 1, cellY, Vec2.right, mazeMoveManager),
            RandomDirection(cellX, cellY + 1, Vec2.down, mazeMoveManager),
            RandomDirection(cellX, cellY - 1, Vec2.up, mazeMoveManager)
        ).filter { !it.isWall }

        if(potentials.size == 1){
            val target = potentials.first()
            cellX = target.cellX
            cellY = target.cellY
            moveDirection = target.direction
        }else if(potentials.size == 2) {
            val target = potentials.first { it.direction != Vec2.opposite(moveDirection) }
            cellX = target.cellX
            cellY = target.cellY
            moveDirection = target.direction
        }else if(potentials.isNotEmpty()) {
            val target = potentials.filter { it.direction != Vec2.opposite(moveDirection) }.random()
            cellX = target.cellX
            cellY = target.cellY
            moveDirection = target.direction
        } else {
            println("THIS GHOST IS STUCK HELP")
        }
    }

    private fun getTargetCellXY() =
        when(moveDirection){
            Vec2.zero -> cellX to cellY
            Vec2.left -> cellX - 1 to cellY
            Vec2.right -> cellX + 1 to cellY
            Vec2.up -> cellX to cellY - 1
            Vec2.down -> cellX to cellY + 1
            else -> cellX to cellY
        }
}

private data class RandomDirection(
    val cellX: Int,
    val cellY: Int,
    val direction: Vec2,
    private val mazeMoveManager: MazeMovementManager,
) {
    val isWall: Boolean = !mazeMoveManager.canMoveTo(cellX, cellY)
}