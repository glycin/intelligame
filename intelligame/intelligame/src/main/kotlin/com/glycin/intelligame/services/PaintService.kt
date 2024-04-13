package com.glycin.intelligame.services

import com.glycin.intelligame.FileOpenedListener
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import kotlinx.coroutines.*
import java.awt.Graphics2D
import java.awt.Point
import javax.imageio.ImageIO

private const val FPS = 120L

@Service
class PaintService(private val scope: CoroutineScope) {

    private val playerSprite = ImageIO.read(FileOpenedListener::class.java.getResourceAsStream("/Sprites/knight.png"))
    private var playerPosition : Point = Point(0,0)

    fun startRenderLoop(graphics: Graphics2D) {
        println("Started render loop")
        scope.launch(Dispatchers.EDT) {
            draw(graphics)
        }
    }

    fun updatePlayerPosition(point: Point){
        println("updating player position to $point")
        playerPosition = point
    }

    fun showMap(graphics: Graphics2D, points: List<Point>){
        scope.launch(Dispatchers.EDT) {
            while (true){
                points.forEach {
                    graphics.drawImage(playerSprite, null, it.x, it.y)
                }
                delay(1000)
            }
        }
    }

    private suspend fun draw(graphics: Graphics2D){
        while(true) {
            graphics.drawImage(playerSprite, null, playerPosition.x, playerPosition.y)
            delay(1000 / FPS)
        }
    }
}