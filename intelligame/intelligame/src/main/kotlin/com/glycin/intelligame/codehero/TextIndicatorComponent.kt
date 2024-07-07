package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBFont
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*
import javax.swing.JTextPane

class TextIndicatorComponent(
    position: Vec2,
    width: Int,
    height: Int,
    fontName: String,
    textToShow: String,
    game: CodeHeroGame,
): JTextPane() {

    private val keyListener = FrameKeyListener(game)
    private val charsToShow = LinkedList<Char>()
    init {
        this.addKeyListener(keyListener)
        setBounds(position.x, position.y, width, height)
        font = Font(fontName, JBFont.BOLD, 48)
        foreground = JBColor.white.brighter().brighter().brighter()
        isOpaque = false
        textToShow.forEach { charsToShow.add(it) }
        text = charsToShow.remove().toString()
        isFocusable = true
        requestFocus()
        requestFocusInWindow()
    }

    fun updateChar(){
        text = charsToShow.remove().toString()
    }

    private class FrameKeyListener(
        private val game: CodeHeroGame,
    ) : KeyListener {
        init {
            println("ADDED KEY LISTENER")
        }
        override fun keyTyped(e: KeyEvent?) {
            println("KeyEvent ${e?.keyChar}")
        }

        override fun keyPressed(e: KeyEvent?) {
            println("KeyEvent pressed ${e?.keyChar}")
            if (e?.id == KeyEvent.KEY_PRESSED && game.gameState.state == CodeHeroStateEnum.PLAYING) {
                println(e.keyChar)
            //game.onInput(e.keyChar)
            }
        }

        override fun keyReleased(e: KeyEvent?) { }
    }
}