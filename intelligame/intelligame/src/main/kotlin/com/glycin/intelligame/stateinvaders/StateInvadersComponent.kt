package com.glycin.intelligame.stateinvaders

import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class StateInvadersComponent(

): JComponent() {

    fun start() {

    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {

        }
    }
}