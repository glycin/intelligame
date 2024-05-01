package com.glycin.intelligame.services

import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.openapi.util.IconLoader
import kotlinx.coroutines.*
import java.awt.Component
import java.awt.Graphics2D
import java.awt.Point
import javax.imageio.ImageIO
import javax.swing.Icon

private const val FPS = 25L

@Service
class PaintService(private val scope: CoroutineScope) {

    private val playerSprite = ImageIO.read(PaintService::class.java.getResourceAsStream("/Sprites/knight.png"))
    private val spriteIcon: Icon = IconLoader.getIcon("/Sprites/knight.png", PaintService::class.java)

    private var iconComponent: Component? = null
    private var iconPoint = Point(0, 0)

    fun startRenderLoop(graphics: Graphics2D) {
        println("Started render loop")
        scope.launch(Dispatchers.EDT) {
            render(graphics)
        }
    }

    fun updatePlayerPosition(component: Component, graphics: Graphics2D?, point: Point){
        if(graphics == null) return
        iconComponent = component
        iconPoint = point
        //spriteIcon.paintIcon(component, graphics, point.x, point.y)
    }

    fun showMap(graphics: Graphics2D, points: List<Point>){
        scope.launch(Dispatchers.EDT) {
            while (true){
                points.forEach {
                    graphics.drawImage(playerSprite, null, it.x, it.y)
                }
            }
        }
    }

    private suspend fun render(graphics: Graphics2D){

        while (true){
            if(iconComponent == null){ return }
            spriteIcon.paintIcon(iconComponent, graphics, iconPoint.x, iconPoint.y)
            delay(1000 / FPS)
        }
    }
}