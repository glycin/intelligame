package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toPoint
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class VelocitnikArm(
    private var position: Vec2,
    private val width: Int,
    private val height: Int,
    private val deltaTime : Float,
    private val zonic: Zonic,
) {
    private var velocity = Vec2.left
    private val speed = 0.8f
    private val image : BufferedImage = ImageIO.read(this.javaClass.getResource("/Sprites/zonic/arm_projectile.png"))
    private var bounds = Rectangle(position.x.roundToInt() + 20, position.y.roundToInt() + 120, 125, 50)
    private var hitZonic = false

    fun draw(g: Graphics2D) {
        update()
        g.drawImage(image, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
    }

    private fun update() {
        velocity = Vec2.left * (speed * deltaTime)
        velocity += if(zonic.position.y < position.y) {
            Vec2.up * ((speed / 3) * deltaTime)
        }else {
            Vec2.down * ((speed / 3) * deltaTime)
        }

        position += velocity

        bounds = Rectangle(position.x.roundToInt() + 20, position.y.roundToInt() + 120, 125, 50)
        if(!hitZonic && bounds.contains(zonic.getMidPos().toPoint())) {
            zonic.pain()
            hitZonic = true
        }
    }
}