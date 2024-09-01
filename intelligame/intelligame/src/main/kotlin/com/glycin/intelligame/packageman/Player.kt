package com.glycin.intelligame.packageman

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toDeltaTime
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import kotlin.math.roundToInt

class Player(
    var position: Vec2,
    val radius: Int,
    private var cellX: Int = 0,
    private var cellY: Int = 0,
    private val mazeMoveManager: MazeMovementManager,
    private val sounds: PackmanSounds,
    fps: Long,
) {
    private val deltaTime = fps.toDeltaTime()

    private val speed = 1
    private val skipframes = fps / 10
    private var curFrames = 0
    private var mouthOpen = false
    private var moving = false
    private var moveDirection = Vec2.zero

    fun render(g: Graphics2D) {
        g.color = JBColor.YELLOW.brighter()
        val arcStartAngle = when(moveDirection) {
            Vec2.zero -> 45
            Vec2.left -> 225
            Vec2.right -> 45
            Vec2.down -> -45
            Vec2.up -> 135
            else -> 45
        }

        if(mouthOpen) {
            g.fillArc(position.x.roundToInt(), position.y.roundToInt(), radius, radius, arcStartAngle, 270)
        }else{
            g.fillOval(position.x.roundToInt(), position.y.roundToInt(), radius, radius)
        }

        curFrames++
        if(curFrames >= skipframes) {
            mouthOpen = !mouthOpen
            curFrames = 0
        }
    }

    fun move(){
        if(moving){
            val target = mazeMoveManager.getMazeCellMidPosition(cellX, cellY)
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
        }
    }

    fun moveDown() {
        moveDirection = Vec2.down
        setMoving()
    }

    fun moveUp() {
        moveDirection = Vec2.up
        setMoving()
    }

    fun moveRight() {
        moveDirection = Vec2.right
        setMoving()
    }

    fun moveLeft() {
        moveDirection = Vec2.left
        setMoving()
    }

    fun kill() {
        moving = false
        position = Vec2(-20000f, -20000f)
    }

    private fun setMoving() {
        val (x, y) = getTargetCellXY()
        if(mazeMoveManager.canMoveTo(x, y)) {
            cellX = x
            cellY = y
            moving = true
            sounds.playMovingSound()
        }else {
            moving = false
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