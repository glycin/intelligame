package com.glycin.intelligame.stateinvaders.model

import com.glycin.intelligame.shared.Vec2
import com.intellij.psi.PsiField
import javax.swing.JLabel

class Stalien(
    var position: Vec2,
    val width: Int,
    val height: Int,
    val text: String,
    val originalPsiField: PsiField,
    var label: JLabel? = null,
    private val speed : Int = 2
) {


    fun move(direction: Vec2) {
        position += direction * speed
    }
}