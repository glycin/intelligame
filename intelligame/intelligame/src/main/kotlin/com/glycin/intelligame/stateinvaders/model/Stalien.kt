package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
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
    private val speed : Int = 1
) {

    fun move(direction: Vec2, deltaTime: Float) {
        position += direction * (deltaTime * speed).roundToInt()
        label?.setBounds(position.x, position.y, width, height)
    }
}