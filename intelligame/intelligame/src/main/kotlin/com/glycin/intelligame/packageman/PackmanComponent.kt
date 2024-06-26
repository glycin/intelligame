package com.glycin.intelligame.packageman

import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class PackmanComponent(
    private val cells: List<MazeCell>,
    private val scope: CoroutineScope,
    fps: Long
) : JComponent() {

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
            paintMaze(g)
        }
    }

    private fun paintMaze(g: Graphics2D) {
        cells.forEach { cell ->
            if(cell.isWall) {
                g.color = JBColor.WHITE.brighter().brighter().brighter()
                g.drawRect(cell.position.x, cell.position.y, cell.width, cell.height)
            }else {
                //g.fillRect(cell.position.x, cell.position.y, cell.width, cell.height)
            }
        }
    }
}