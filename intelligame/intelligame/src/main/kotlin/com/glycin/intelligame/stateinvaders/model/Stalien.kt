package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.StateInvadersGame
import com.intellij.psi.PsiField
import javax.swing.JLabel
import kotlin.math.roundToInt

class Stalien(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val text: String,
    val originalPsiField: PsiField,
    var label: JLabel? = null,
    private val game: StateInvadersGame,
    private val speed : Int = 1
) {
    fun minX() = position.x
    fun maxX() = position.x + width
    fun minY() = position.y
    fun maxY() = position.y + height

    private var shootTime = 0L
    private val shootCooldown = 2500L //ms

    fun move(direction: Vec2, deltaTime: Float) {
        position += direction * (deltaTime * speed).roundToInt()
        label?.setBounds(position.x.roundToInt(), position.y.roundToInt(), width, height)
    }

    fun shoot() {
        if(System.currentTimeMillis() >= shootTime) {
            val bullet = Bullet(Vec2(position.x + width / 2, position.y + height), 10, 25, true, Vec2.down, game, 1)
            game.bm.submitBullet(bullet)
            shootTime = System.currentTimeMillis() + shootCooldown
        }
    }

    fun die() {
        label?.isVisible = false
        label = null
        game.cm.staliens.remove(this)
        position = Vec2(-1000f, -1000f)
    }
}