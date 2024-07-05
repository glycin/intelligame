package com.glycin.intelligame.codehero

import com.glycin.intelligame.boom.BoomComponent
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel

class CodeHeroComponent(
    private val noteManager: NoteManager,
    private val scope: CoroutineScope,
    fps: Long
) : JComponent() {

    private val deltaTime = fps.toLongDeltaTime()
    private val rockerGif = ImageIcon(BoomComponent::class.java.getResource("/Sprites/guitar.gif"))

    private lateinit var rockerLabel: JLabel
    private var centerX = 0
    private var centerY = 0

    fun start() {
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun destroy() {
        remove(rockerLabel)
    }

    fun focus() {
        centerX = width / 2
        centerY = height / 2
        showRocker()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            drawMusicalLines(g)
            drawNotes(g)
        }
    }

    private fun drawMusicalLines(g: Graphics2D) {
        g.color = JBColor.WHITE.brighter()
        g.fillRect(0, centerY + 25, width, 5)
        g.fillRect(0, centerY + 50, width, 5)
        g.fillRect(0, centerY + 75, width, 5)

        g.color = JBColor.RED.darker()
        g.fillRect(centerX - 25, centerY, 60, 100)
        g.color = JBColor.WHITE.brighter().brighter().brighter()
        g.fillRect(centerX - 20, centerY + 5, 50, 90)
    }

    private fun drawNotes(g: Graphics2D) {
        noteManager.notes.values
            .forEach {
                it.move()
                it.draw(g)
            }
    }

    private fun showRocker() {
        val x = width - (rockerGif.iconWidth / 2) - 100
        val y = centerY - (rockerGif.iconHeight / 2) - 100
        rockerLabel = JLabel(rockerGif)
        rockerLabel.setBounds(x, y, rockerGif.iconWidth, rockerGif.iconHeight)
        add(rockerLabel)
        repaint()
    }
}