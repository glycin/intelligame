package com.glycin.intelligame.pong

import com.glycin.intelligame.pong.model.PlayerBrick
import com.intellij.openapi.editor.CaretModel
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class PongInput(
    private val p1: PlayerBrick,
    private val p2: PlayerBrick,
    private val caretModel: CaretModel,
    fps: Long,
): KeyEventDispatcher {

    private val deltaTime = 1000.0f / fps
    private val originalCaretOffset = caretModel.offset

    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED) {
            when (e.keyCode) {
                KeyEvent.VK_W -> {
                    p1.moveUp(deltaTime)
                }
                KeyEvent.VK_S -> {
                    p1.moveDown(deltaTime)
                }

                KeyEvent.VK_UP -> {
                    p2.moveUp(deltaTime)
                    caretModel.moveToOffset(originalCaretOffset)
                }

                KeyEvent.VK_DOWN -> {
                    p2.moveDown(deltaTime)
                    caretModel.moveToOffset(originalCaretOffset)
                }
            }
        }
        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}