package com.glycin.intelligame

import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener

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