package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class ZtdComponent(
    private val ztdGame: ZtdGame,
    private val scope: CoroutineScope,
    fps: Long
): JComponent() {
    private val deltaTime = fps.toLongDeltaTime()

    fun start() {
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            ztdGame.zonic.draw(g)
            drawTiles(g)
        }
    }

    private fun drawTiles(g: Graphics2D) {
        ztdGame.currentTiles.forEach { it ->
            g.color = JBColor.WHITE.brighter().brighter().brighter()
            g.drawRect(it.position.x, it.position.y, it.width, it.height)
        }
    }
}