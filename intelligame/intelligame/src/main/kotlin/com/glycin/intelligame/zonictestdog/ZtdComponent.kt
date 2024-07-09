package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import kotlin.math.roundToInt

class ZtdComponent(
    private val ztdGame: ZtdGame,
    private val scope: CoroutineScope,
    fps: Long
): JComponent() {
    private val deltaTime = fps.toLongDeltaTime()
    private val zonic = Zonic(Fec2.one, 100, 100)
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
            testDraw(g)
        }
    }

    private fun testDraw(g: Graphics2D) {
        zonic.standingSprites.forEachIndexed { index, it ->
            g.drawImage(it, zonic.position.x.roundToInt() + (zonic.width * index), zonic.position.y.roundToInt(), zonic.width, zonic.height, null)
        }

        zonic.runningSprites.forEachIndexed { index, it ->
            g.drawImage(it, zonic.position.x.roundToInt() + (zonic.width * index), zonic.position.y.roundToInt() + 150, zonic.width, zonic.height, null)
        }

        zonic.duckSprites.forEachIndexed { index, it ->
            g.drawImage(it, zonic.position.x.roundToInt() + (zonic.width * index), zonic.position.y.roundToInt() + 300, zonic.width, zonic.height, null)
        }

        zonic.jumpingSprites.forEachIndexed { index, it ->
            g.drawImage(it, zonic.position.x.roundToInt() + (zonic.width * index), zonic.position.y.roundToInt() + 450, zonic.width, zonic.height, null)
        }

        zonic.hurtSprites.forEachIndexed { index, it ->
            g.drawImage(it, zonic.position.x.roundToInt() + (zonic.width * index), zonic.position.y.roundToInt() + 600, zonic.width, zonic.height, null)
        }
    }
}