package com.glycin.intelligame.services

import com.glycin.intelligame.FileOpenedListener
import com.intellij.openapi.components.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics2D
import java.awt.Point
import javax.imageio.ImageIO

private const val FPS = 60L

@Service
class PaintService {
    fun drawPlayer(graphics: Graphics2D, point: Point){
        GlobalScope.launch(Dispatchers.IO) {
            draw(graphics, point)
        }
    }

    private suspend fun draw(graphics: Graphics2D, point: Point){
        val input = FileOpenedListener::class.java.getResourceAsStream("/Sprites/knight.png")
        val imgBuffer = ImageIO.read(input)

        while(true) {
            graphics.drawImage(imgBuffer, null, point.x, point.y)
            delay(1000 / FPS)
        }
    }
}