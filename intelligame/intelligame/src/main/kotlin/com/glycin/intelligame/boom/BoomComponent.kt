package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.glycin.intelligame.shared.Vec2
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class BoomComponent(
    private val boomObjects: List<ExplosionObject>,
    private val scope: CoroutineScope,
    fps : Long,
): JComponent() {

    private val deltaTime = 1000L / fps

    private val explosionForce = 1
    private val explosionRadius = 200

    fun start() {
        isFocusable = true

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                //super.mouseClicked(e)
                println("clicked on ${e.x}, ${e.y}")
                explode(Vec2(e.x, e.y))
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

    private fun drawObjects(g: Graphics2D) {
        boomObjects.forEach { boom ->
            g.color = Gray._255
            g.drawRect(boom.position.x, boom.position.y, boom.width, boom.height)
        }
    }

    private fun explode(explosionPos: Vec2) {
        val start = System.currentTimeMillis()
        val duration = 5000 //millis
        val endTime = start + duration

        scope.launch (Dispatchers.EDT) {
            while(System.currentTimeMillis() < endTime) {
                val elapsed = System.currentTimeMillis() - start
                val progress = elapsed / duration

                boomObjects.onEach { b ->
                    val centerPos = b.midPoint()
                    val distance = Vec2.distance(centerPos, explosionPos)

                    if(distance < explosionRadius) {
                        val forceMagnitude = (1 - progress) * (explosionRadius - distance) / explosionForce
                        b.moveWithForce(forceMagnitude, explosionPos)
                    }else {
                        b.force = 0.0f
                    }

                    //handleCollisions(b)
                }
                delay(deltaTime)
            }
        }
    }

    private fun handleCollisions(a: ExplosionObject) {
        val midPointA = a.midPoint()
        boomObjects
            .filter {
                it.intersects(a)
            }
            .onEach { b ->
                val midPointB = b.midPoint()
                val angle = atan2((midPointB.y - midPointA.y).toDouble(), (midPointB.x - midPointA.x).toDouble())

                val overlapX = a.maxX() - b.minX()
                val overlapY = a.maxY() - b.minY()
                val moveDistance = min(overlapX, overlapY) // / 2 ?

                val moveX = (moveDistance * cos(angle)).toInt()
                val moveY = (moveDistance * sin(angle)).toInt()

                a.position -= Vec2(moveX, moveY)
                b.position += Vec2(moveX, moveY)
                a.velocity *= -1
                b.velocity *= -1
            }
    }
}