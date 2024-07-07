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
    var hitOnTime = false
    val deltaTime = fps.toDeltaTime()
    private val speed = targetPos.x / (620 / deltaTime)
    private var draw = true

    fun move(){
        positionLeft += Fec2.right * speed
        positionRight += Fec2.left * speed

        if((targetPos.x + 30) - positionLeft.x < 0){
            draw = false
        }else if(positionRight.x - (targetPos.x + 30) < 0){
            draw = false
        }
    }

    fun draw(g: Graphics2D) {
        if(!draw) return

        // Used to debug the timings
        /*if(active){
            g.color = JBColor.green.brighter()
        }else{
            g.color = color
        }*/

        g.color = color
        g.fillRect(positionLeft.x.roundToInt(), positionLeft.y.roundToInt(), width, height)
        g.fillRect(positionRight.x.roundToInt(), positionRight.y.roundToInt(), width, height)
    }

    fun destroy(){
        positionLeft = Fec2(10000f, -10000f)
        positionRight = Fec2(10000f, -10000f)
        active = false
    }
}