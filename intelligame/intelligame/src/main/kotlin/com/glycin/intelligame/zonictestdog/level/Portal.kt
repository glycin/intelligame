package com.glycin.intelligame.zonictestdog.level

import com.glycin.intelligame.shared.Vec2
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.swing.JLabel
import kotlin.math.roundToInt
import kotlin.random.Random

class Portal(
    var position: Vec2,
    val height: Int,
    val width: Int,
    val file: PsiFile,
    val element: PsiElement,
    val textRange: TextRange,
    private val sprites: Array<BufferedImage>,
) {
    var bounds: Rectangle = Rectangle(position.x.roundToInt(), position.y.roundToInt(), width, height)
    val color = Random.nextInt(4)
    var label: JLabel = JLabel()
    var addedLabel = false

    private var currentIndex = 0
    private var frameHoldCount: Int = 4

    init {
        label.foreground = JBColor.WHITE.brighter().brighter().brighter()
        label.text = file.name
        label.setBounds(position.x.roundToInt(), position.y.roundToInt(), width, height)
    }

    fun drawPortal(g: Graphics2D) {
        val sprite = sprites[currentIndex]
        g.drawImage(sprite, position.x.roundToInt() - (width / 2), position.y.roundToInt() - (height / 2), width, height, null)
        frameHoldCount++

        if(frameHoldCount % 4 == 0) {
            currentIndex++
        }

        if(currentIndex >= sprites.size) {
            currentIndex = 0
            frameHoldCount = 0
        }
    }

    fun close() {
        position = Vec2(-15000f, 15000f)
        bounds = Rectangle(position.x.roundToInt(), position.y.roundToInt(), 0, 0)
    }
}