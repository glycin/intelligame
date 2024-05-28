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
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import kotlin.math.roundToInt

class BoomRenderer(
    private val boomObjects: List<ExplosionObject>,
    private val scope: CoroutineScope,
    fps : Long,
): JComponent() {

    private val deltaTime = 1000L / fps

    fun start() {
        isFocusable = true

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                super.mouseClicked(e)

            }
        })

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

    private fun explode(explosionX: Int, explosionY: Int, force: Int) {

    }

    private fun drawObjects(g: Graphics2D) {
        boomObjects.forEach { boom ->
            g.color = Gray._255
            g.drawRect(boom.position.x, boom.position.y, boom.width, boom.height)
        }
    }
}