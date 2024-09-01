package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.stateinvaders.model.Stalien
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.math.roundToInt

class StateInvadersComponent(
    private val spaceShip: SpaceShip,
    private val game: StateInvadersGame,
    private val scope: CoroutineScope,
    fps: Long
) : JComponent() {

    private val deltaTime = fps.toLongDeltaTime()
    private var aliensHorizontal = false

    fun start() {
        scope.launch (Dispatchers.EDT) {
            createLabels()
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun removeStalien(stalien: Stalien) {
        remove(stalien.label)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            drawSpaceShip(g)
            drawAliens(g)
            drawBullets(g)
            game.bm.moveBullets()
            game.sm.moveGroup()
        }
    }

    private fun drawAliens(g: Graphics2D) {
        val shouldSwitch = game.sm.shouldAnimate()
        if (shouldSwitch) {
            aliensHorizontal = !aliensHorizontal
        }

        game.sm.staliens.forEach {
            g.color = JBColor.RED
            g.drawRect(it.position.x.roundToInt(), it.position.y.roundToInt(), it.width, it.height)

            val stMinX = it.minX().roundToInt()
            val stMinY = it.minY().roundToInt()
            val stMaxX = it.maxX().roundToInt()
            val stMaxY = it.maxY().roundToInt()
            if(aliensHorizontal) {
                g.fillRect(stMinX - 10, stMaxY, 10, 5)
                g.fillRect(stMinX - 10, stMinY, 10, 5)
                g.fillRect(stMaxX, stMaxY, 10, 5)
                g.fillRect(stMaxX, stMinY, 10, 5)
            } else {
                g.fillRect(stMinX, stMaxY, 5, 10)
                g.fillRect(stMinX, stMinY - 10, 5, 10)
                g.fillRect(stMaxX, stMaxY, 5, 10)
                g.fillRect(stMaxX, stMinY - 10, 5, 10)
            }

            g.color = JBColor.MAGENTA
            val leftEyePos = Vec2((it.position.x + it.width / 2) - 30, it.position.y - 15)
            val rightEyePos = Vec2((it.position.x + it.width / 2) + 30, it.position.y - 15)
            g.fillOval(leftEyePos.x.roundToInt(), leftEyePos.y.roundToInt(), 15, 15)
            g.fillOval(rightEyePos.x.roundToInt(), rightEyePos.y.roundToInt(), 15, 15)
        }
    }

    private fun drawSpaceShip(g: Graphics2D) {
        val x = spaceShip.position.x + spaceShip.width / 2
        val y = spaceShip.position.y + spaceShip.height / 2
        g.drawImage(spaceShip.spaceShipImage, x.roundToInt(), y.roundToInt(), this)
    }

    private fun drawBullets(g: Graphics2D) {
        game.bm.getAllActiveBullets().forEach {
            g.color = if(it.isHostile) JBColor.pink else JBColor.yellow
            g.fillRect(it.position.x.roundToInt(), it.position.y.roundToInt(), it.width, it.height)
        }
    }

    private fun createLabels(){
        val scheme = EditorColorsManager.getInstance().globalScheme
        val fontPreferences = scheme.fontPreferences

        game.sm.staliens.forEach { alien ->
            val objLabel = JLabel(alien.text)
            objLabel.font = Font(fontPreferences.fontFamily, 0, fontPreferences.getSize(fontPreferences.fontFamily))
            objLabel.setBounds(alien.position.x.roundToInt(), alien.position.y.roundToInt(), alien.width, alien.height)
            add(objLabel)
            alien.label = objLabel
        }
        repaint()
    }
}