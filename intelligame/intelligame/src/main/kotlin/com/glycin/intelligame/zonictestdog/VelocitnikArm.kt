package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Vec2
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class VelocitnikArm(
    private var position: Vec2,
    private val targetPos: Vec2,
    private val width: Int,
    private val height: Int,
    private val deltaTime : Float,
) {
    private var velocity = Vec2.left
    private val speed = 0.25f
    private val image : BufferedImage = ImageIO.read(this.javaClass.getResource("/Sprites/zonic/arm_projectile.png"))

    fun draw(g: Graphics2D) {
        update()
        g.drawImage(image, position.x.roundToInt() + width, position.y.roundToInt(), -width, height, null)
    }

    private fun update() {
        velocity = Vec2.left * (speed * deltaTime)
        if(targetPos.y < position.y) {
            velocity += Vec2.up * ((speed / 4) * deltaTime)
        }else {
            velocity -= Vec2.down * ((speed / 4) * deltaTime)
        }

        position += velocity
    }
}