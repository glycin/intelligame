package com.glycin.intelligame

import com.glycin.intelligame.services.GameService
import com.glycin.intelligame.services.PaintService
import com.glycin.intelligame.util.getPoint
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.util.ui.GraphicsUtil
import java.awt.Graphics2D

//TODO: This doesnt work at all
class GameCaretListener: CaretListener{

    override fun caretAdded(event: CaretEvent) {
        println("added")
        super.caretAdded(event)
    }
    override fun caretPositionChanged(event: CaretEvent) {


        super.caretPositionChanged(event)
    }
}