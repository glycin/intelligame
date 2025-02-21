package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics2D
import kotlin.math.roundToInt

class Note(
    val id: Int,
    var positionLeft: Vec2,
    var positionRight: Vec2,
    val width: Int,
    val height: Int,
    private val color: JBColor,
    private val targetPos: Vec2,
    scope: CoroutineScope,
    fps: Long,
){
    var active = false
    var hitOnTime = false
    val deltaTime = fps.toLongDeltaTime()
    private var draw = true

    init {
        val totalSteps = 1 * fps
        val distancePerStep = (targetPos.x - positionLeft.x) / totalSteps

        scope.launch(Dispatchers.Default) {
            val frameDurationNanos = 1_000_000_000L / fps
            var nextFrameTime = System.nanoTime()

            repeat(totalSteps.toInt()) {
                positionLeft += Vec2.right * distancePerStep
                positionRight += Vec2.left * distancePerStep
                nextFrameTime += frameDurationNanos
                val sleepTime = nextFrameTime - System.nanoTime()
                if (sleepTime > 0) {
                    delay(sleepTime / 1_000_000L)
                } else {
                    // Compensate for missed frames if the sleep time is negative
                    nextFrameTime = System.nanoTime()
                }
            }
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
        checkCollision()

        g.color = color.brighter().brighter()
        g.fillRect(positionLeft.x.roundToInt(), positionLeft.y.roundToInt(), width, height)
        g.fillRect(positionRight.x.roundToInt(), positionRight.y.roundToInt(), width, height)
    }

    fun destroy(){
        positionLeft = Vec2(10000f, -10000f)
        positionRight = Vec2(10000f, -10000f)
        active = false
    }

    private fun checkCollision(){
        if((targetPos.x + 30) - positionLeft.x < 0){
            draw = false
        }else if(positionRight.x - (targetPos.x + 30) < 0){
            draw = false
        }
    }
}