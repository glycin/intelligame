package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import java.awt.AlphaComposite
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JComponent

class ZtdComponent(
    private val ztdGame: ZtdGame,
    private val scope: CoroutineScope,
    fps: Long
): JComponent() {
    private val deltaTime = fps.toLongDeltaTime()
    private val tileImage: BufferedImage = ImageIO.read(this.javaClass.getResource("/Sprites/platform.png"))


    fun start() {
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun removePortalLabels() {
        ztdGame.portals.forEach { p ->
            remove(p.label)
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            ztdGame.zonic.draw(g)
            drawTiles(g)
            drawPortals(g)
            drawCoins(g)
            drawEnemies(g)
            drawUi(g)
        }
    }

    private fun drawTiles(g: Graphics2D) {
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f)
        ztdGame.currentTiles.forEach {
            g.color = JBColor.WHITE.brighter().brighter().brighter()
            g.drawImage(tileImage, it.position.x, it.position.y, it.width, it.height, null)
        }
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
    }

    private fun drawPortals(g: Graphics2D) {
        ztdGame.portals.forEach { p ->
            if(!p.addedLabel){
                add(p.label)
                p.addedLabel = true
            }
            p.drawPortal(g)
        }
    }

    private fun drawCoins(g: Graphics2D) {
        ztdGame.currentCoins.forEach {
            if(!it.pickedUp) {
                it.draw(g)
            } else if(it.dropped) {
                it.drawDrop(g)
            }
        }

        val tbr = ztdGame.currentCoins.filter { it.toBeRemoved }
        ztdGame.currentCoins.removeAll(tbr)
    }

    private fun drawEnemies(g: Graphics2D) {
        ztdGame.currentEnemies.forEach {
            if(it.alive){
                it.draw(g)
            }
        }
    }

    private fun drawUi(g: Graphics2D) {
        g.color = JBColor.YELLOW.brighter().brighter().brighter()
        g.fillOval(width - (width / 3) - 20, 20, 15, 15)

        g.color = JBColor.WHITE.brighter().brighter().brighter().brighter()
        g.font = Font("Times New Roman", Font.BOLD, 24)
        g.drawString("x${ztdGame.zonic.pickedUpCoins.size}", width - (width / 3), 35)
    }
}