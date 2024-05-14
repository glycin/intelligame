package com.glycin.intelligame.pong

import com.intellij.ui.Gray
import java.awt.Graphics
import java.awt.Graphics2D

class PongRenderer {

    fun paint(g: Graphics?) {
        if(g != null && g is Graphics2D) {
            println("drawing")
            g.color = Gray._255
            g.clearRect(0, 0, 500, 500)
            g.drawRect(0, 0, 150, 150)
        }
    }
}