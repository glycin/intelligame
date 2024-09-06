package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.SpriteSheetImageLoader
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import kotlinx.coroutines.*
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JComponent

class ZtdCutsceneComponent(
    private val scope: CoroutineScope,
    fps: Long,
): JComponent() {

    private val deltaTime = fps.toLongDeltaTime()
    private val zonicSprites = arrayOfNulls<BufferedImage>(4)
    private val velocitnikSprites = arrayOfNulls<BufferedImage>(10)

    private lateinit var currentZonicSprite : BufferedImage
    private var currentZonicAnimationIndex = 0
    private var zonicSkipFrameCount = 0

    private lateinit var currentVelocitnikSprite : BufferedImage
    private var currentVelocitnikAnimationIndex = 0
    private var velocitnikSkipFrameCount = 0

    private lateinit var coroutine : Job
    private var repaint = true

    fun start() {
        val allZonicSprites = SpriteSheetImageLoader(
            spriteSheetPath = "/Sprites/sheets/zonic.png",
            cellWidth = 33,
            cellHeight = 32,
            numSprites = 36,
        ).loadSprites()

        val allVelocitnikSprites = SpriteSheetImageLoader(
            spriteSheetPath = "/Sprites/sheets/velocitnic.png",
            cellWidth = 100,
            cellHeight = 100,
            numSprites = 90,
        ).loadSprites()

        zonicSprites.indices.forEach { index -> zonicSprites[index] = allZonicSprites[index] }
        (60 until 70).forEachIndexed { index, i -> velocitnikSprites[index] = allVelocitnikSprites[i] }

        currentZonicSprite = zonicSprites[0]!!
        currentVelocitnikSprite = velocitnikSprites[0]!!
        coroutine = scope.launch (Dispatchers.EDT) {
            while(repaint) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun stop() {
        repaint = false
        coroutine.cancel()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            g.drawImage(currentZonicSprite, 100, 250, 50, 50, null)
            g.drawImage(currentVelocitnikSprite, 600, 100, -400, 400, null)
            showZonicAnimation()
            showVelocitnikAnimation()
        }
    }

    private fun showZonicAnimation() {
        zonicSkipFrameCount++
        if(zonicSkipFrameCount % 15 == 0) {
            currentZonicAnimationIndex++
        }

        if(currentZonicAnimationIndex >= zonicSprites.size - 1) {
            currentZonicAnimationIndex = 0
            zonicSkipFrameCount = 0
        }
        currentZonicSprite = zonicSprites[currentZonicAnimationIndex]!!
    }

    private fun showVelocitnikAnimation() {
        velocitnikSkipFrameCount++
        if(velocitnikSkipFrameCount % 25 == 0) {
            currentVelocitnikAnimationIndex++
        }

        if(currentVelocitnikAnimationIndex >= velocitnikSprites.size - 1) {
            currentVelocitnikAnimationIndex = 0
            velocitnikSkipFrameCount = 0
        }
        currentVelocitnikSprite = velocitnikSprites[currentVelocitnikAnimationIndex]!!
    }
}