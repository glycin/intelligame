package com.glycin.intelligame.boom

import com.glycin.intelligame.boom.model.ExplosionObject
import com.glycin.intelligame.shared.Vec2
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.Gray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.math.*

class BoomComponent(
    private val boomObjects: List<ExplosionObject>,
    private val explosionWriter: ExplosionWriter,
    private val scope: CoroutineScope,
    fps : Long,
): JComponent() {

    private val explosionGif = ImageIcon(BoomComponent::class.java.getResource("/Sprites/explode.gif"))
    private val deltaTime = 1000L / fps

    private val explosionDecay = 50
    private val explosionForce = 5
    private val explosionRadius = 200

    private var active = true

    fun start() {
        isFocusable = true

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                super.mouseClicked(e)
                boomObjects.forEach { it.show() }
                explosionWriter.clear()
                explode(Vec2(e.x.toFloat(), e.y.toFloat()))
            }
        })

        createLabels()

        scope.launch (Dispatchers.EDT) {
            while(active) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun stop() {
        active = false
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            //drawObjects(g)
        }
    }

    private fun drawObjects(g: Graphics2D) {
        boomObjects.forEach { boom ->
            g.color = Gray._117
            g.drawRect(boom.position.x.roundToInt(), boom.position.y.roundToInt(), boom.width, boom.height)
        }
    }

    private fun drawExplosion(position:Vec2): JLabel{
        val pos = Vec2(position.x - explosionGif.iconWidth / 2, position.y - explosionGif.iconHeight / 2)
        val expLabel = JLabel(explosionGif)
        expLabel.setBounds(pos.x.roundToInt(), pos.y.roundToInt(), explosionGif.iconWidth, explosionGif.iconHeight)
        add(expLabel)
        repaint()
        return expLabel
    }


    private fun createLabels(){
        val scheme = EditorColorsManager.getInstance().globalScheme
        val fontPreferences = scheme.fontPreferences

        boomObjects.forEach { boom ->
            val objLabel = JLabel(boom.char)
            objLabel.font = Font(fontPreferences.fontFamily, 0, fontPreferences.getSize(fontPreferences.fontFamily))
            objLabel.setBounds(boom.position.x.roundToInt(), boom.position.y.roundToInt(), boom.width, boom.height)
            objLabel.isVisible = false
            add(objLabel)
            boom.label = objLabel
        }
        repaint()
    }

    private fun explode(explosionPos: Vec2) {
        val start = System.currentTimeMillis()
        val duration = 1000 //millis
        val endTime = start + duration

        val label = drawExplosion(explosionPos)
        scope.launch (Dispatchers.Main) {
            delay(50) // A little delay to make the effect match the gif
            while(System.currentTimeMillis() < endTime) {
                boomObjects.onEach { b ->
                    val centerPos = b.midPoint()
                    val distance = Vec2.distance(centerPos, explosionPos)

                    if(distance < explosionRadius) {
                        val forceMagnitude = explosionForce * (explosionRadius - distance) / explosionDecay
                        b.moveWithForce(forceMagnitude, explosionPos)
                    }else {
                        b.force = 0.0f
                    }

                    handleCollisions(b)
                }
                delay(deltaTime)
            }
            boomObjects.forEach { it.rest() }
            remove(label)
        }.invokeOnCompletion {
            explosionWriter.writeText(boomObjects)
        }
    }

    // This isnt perfect, but its good enough
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
                val moveDistance = min(overlapX, overlapY)  / 2

                val moveX = (moveDistance * cos(angle)).toInt()
                val moveY = (moveDistance * sin(angle)).toInt()

                a.position -= Vec2(moveX.toFloat(), moveY.toFloat())
                b.position += Vec2(moveX.toFloat(), moveY.toFloat())
                a.velocity *= -1
                b.velocity *= -1
            }
    }
}