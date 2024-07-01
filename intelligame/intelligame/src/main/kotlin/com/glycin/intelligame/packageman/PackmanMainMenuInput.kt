package com.glycin.intelligame.packageman

import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class PackmanMainMenuInput(
    private val game: PackmanGame
): KeyEventDispatcher {
    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED && game.gameState == GameState.MAIN_MENU) {
            game.mainMenuTyped(e.keyChar)
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return false
    }
}