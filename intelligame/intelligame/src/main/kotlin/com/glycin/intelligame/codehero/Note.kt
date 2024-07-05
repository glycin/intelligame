package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.util.toDeltaTime
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import kotlin.math.roundToInt

class Note(
    val id: Int,
    var positionLeft: Fec2,
    var positionRight: Fec2,
    val width: Int,
    val height: Int,
    private val color: JBColor,
    private val targetPos: Fec2,
    fps: Long,
){
    var active = false
    val deltaTime = fps.toDeltaTime()
    val speed = targetPos.x / (3000 / deltaTime)
    //TODO: Calculate the speed based on the distance that i have to travel
    fun move(){
        positionLeft += Fec2.right * speed
        positionRight += Fec2.left * speed
    }

    fun draw(g: Graphics2D) {
        if(!active){
            g.color = color
        }else{
            g.color = JBColor.GREEN
        }
        g.fillRect(positionLeft.x.roundToInt(), positionLeft.y.roundToInt(), width, height)
        g.fillRect(positionRight.x.roundToInt(), positionRight.y.roundToInt(), width, height)
    }

    fun destroy(){
        active = false
    }
}