package com.glycin.intelligame.services

import com.glycin.intelligame.util.getPointOnCaret
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import java.awt.Graphics2D
import java.awt.Point

@Service
class GameService {
    var score: Int = 0
    var fileOpened = false

    fun loadMap(editor: Editor, graphics: Graphics2D, paintService: PaintService) {
        val points = mutableListOf<Point>()

        for (offset in 0 until editor.document.textLength){
            points.add(editor.getPointOnCaret(offset))
        }

        fileOpened = true
        //debugMap(graphics, paintService, points)
    }

    private fun debugMap(graphics: Graphics2D, paintService: PaintService, points: List<Point>){
        paintService.showMap(graphics, points)
    }
}