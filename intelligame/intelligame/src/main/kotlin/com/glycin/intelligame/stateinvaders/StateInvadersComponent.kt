package com.glycin.intelligame.stateinvaders

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

class StateInvadersComponent(
    private val aliens: List<Stalien>,
    private val spaceShip: SpaceShip,
    private val manager: StalienManager,
    private val bulletManager: BulletManager,
    private val scope: CoroutineScope,
    fps: Long
) : JComponent() {

    private val deltaTime = fps.toLongDeltaTime()

    fun start() {
        //aliens.forEach { it.originalPsiField.delete() } //TODO: This has to be be done on the main thread
        scope.launch (Dispatchers.EDT) {
            createLabels()
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            drawSpaceShip(g)
            drawAliens(g)
            drawBullets(g)
            bulletManager.moveBullets()
            manager.moveGroup()
        }
    }

    private fun drawAliens(g: Graphics2D) {
        aliens.forEach {
            g.color = JBColor.RED
            g.drawRect(it.position.x, it.position.y, it.width, it.height)
        }
    }

    private fun drawSpaceShip(g: Graphics2D) {
        g.color = JBColor.GREEN
        g.fillRect(spaceShip.position.x, spaceShip.position.y, spaceShip.width, spaceShip.height)
    }

    private fun drawBullets(g: Graphics2D) {
        bulletManager.getAllActiveBullets().forEach {
            g.color = JBColor.yellow
            g.fillRect(it.position.x, it.position.y, it.width, it.height)
        }
    }

    private fun createLabels(){ // TODO: Generalize this
        val scheme = EditorColorsManager.getInstance().globalScheme
        val fontPreferences = scheme.fontPreferences

        aliens.forEach { alien ->
            val objLabel = JLabel(alien.text)
            objLabel.font = Font(fontPreferences.fontFamily, 0, fontPreferences.getSize(fontPreferences.fontFamily))
            objLabel.setBounds(alien.position.x, alien.position.y, alien.width, alien.height)
            add(objLabel)
            alien.label = objLabel
        }
        repaint()
    }
}