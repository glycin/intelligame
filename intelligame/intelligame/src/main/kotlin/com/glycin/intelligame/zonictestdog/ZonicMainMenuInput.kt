package com.glycin.intelligame.zonictestdog

import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class ZonicMainMenuInput(
    private val game: ZtdGame,
): KeyEventDispatcher {
    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {
        if (e?.id == KeyEvent.KEY_PRESSED && game.state == ZtdGameState.MAIN_MENU) {
            game.mainMenuTyped(e.keyChar)
        }

        return false
    }
}