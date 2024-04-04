package com.glycin.intelligame

import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.VisualPosition
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
    fun drawPlayer(graphics: Graphics2D, caret: Caret, editor: Editor){
        val point = getPoint(caret.visualPosition, editor)
        println(point)
        GlobalScope.launch(Dispatchers.IO) {
            draw(graphics, caret, point)
        }
    }

    private suspend fun draw(graphics: Graphics2D, caret: Caret, point: Point){
        val input = FileOpenedListener::class.java.getResourceAsStream("/Sprites/knight.png")
        val imgBuffer = ImageIO.read(input)

        while(true) {
            graphics.drawImage(imgBuffer, null, point.x, point.y)
            delay(1000 / FPS)
        }
    }

    private fun getPoint(position: VisualPosition, editor: Editor): Point {
        val p = editor.visualPositionToXY(position)
        val location = editor.scrollingModel.visibleArea.location
        p.translate(-location.x, - location.y)
        return p
    }
}