package com.glycin.intelligame.boom.model

import com.glycin.intelligame.shared.Vec2
import javax.swing.JLabel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val DRAG = 0.8

class ExplosionObject(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val char: String,
    var label: JLabel? = null,
    private var moving: Boolean = false
){
    fun minX() = position.x
    fun maxX() = position.x + width
    fun minY() = position.y
    fun maxY() = position.y + height
    fun midPoint() = Vec2(position.x + width / 2, position.y + height / 2)

    var velocity = Vec2.zero
    var force = 0.0f

    fun intersects(other: ExplosionObject): Boolean {
        return maxX() > other.minX() &&
                minX() < other.maxX() &&
                maxY() > other.minY() &&
                minY() < other.maxY()
    }

    fun moveWithForce(forceMagnitude: Float, explosionPos: Vec2) {

        this.force = forceMagnitude
        val angle = atan2((midPoint().y - explosionPos.y).toDouble(), (midPoint().x - explosionPos.x).toDouble())
        val deltaX = ((force * cos(angle)) * DRAG).roundToInt()
        val deltaY = ((force * sin(angle)) * DRAG).roundToInt()

        //velocity += Vec2(deltaX, deltaY)
        //position += velocity

        position = Vec2(position.x + deltaX, position.y + deltaY)
        label?.setBounds(position.x.roundToInt(), position.y.roundToInt(), width, height)
    }

    fun show() {
        label?.isVisible = true
    }

    fun rest(){
        label?.isVisible = false
    }
}