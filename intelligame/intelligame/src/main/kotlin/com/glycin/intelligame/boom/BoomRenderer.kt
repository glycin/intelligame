package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.intellij.openapi.application.EDT
import com.intellij.ui.Gray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent
import kotlin.math.roundToInt

class BoomRenderer(
    val boomObjects: List<ExplosionObject>,
    private val scope: CoroutineScope,
    fps : Long,
): JComponent() {

    private val deltaTime = 1000L / fps

    fun start() {
        isFocusable = true
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            drawObjects(g)
        }
    }

    private fun drawObjects(g: Graphics2D) {
        boomObjects.forEach { boom ->
            g.color = Gray._255
            g.drawRect(boom.position.x.roundToInt(), boom.position.y.roundToInt(), boom.width, boom.height)
        }
    }
}