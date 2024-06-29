package com.glycin.intelligame.packageman

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor
import java.awt.Graphics2D

class Player(
    val position: Vec2,
    val radius: Int,
    val cellX: Int = 0,
    val cellY: Int = 0,
    fps: Long,
) {

    val skipframes = fps / 4
    var curFrames = 0
    var mouthOpen = false

    fun render(g: Graphics2D) {
        g.color = JBColor.YELLOW.brighter()
        if(mouthOpen) {
            g.fillArc(position.x, position.y, radius, radius, 45, 270)
        }else{
            g.fillOval(position.x, position.y, radius, radius)
        }

        curFrames++
        if(curFrames >= skipframes) {
            mouthOpen = !mouthOpen
            curFrames = 0
        }
    }
}