package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.stateinvaders.model.GameState
import com.glycin.intelligame.util.toDeltaTime
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class StateInvadersInput(
    private val game: StateInvadersGame,
    fps: Long,
): KeyEventDispatcher {

    private val deltaTime = fps.toDeltaTime()

    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED ) {
            if(game.state == GameState.STARTED) {
                when (e.keyCode) {
                    KeyEvent.VK_A -> {
                        game.player.moveLeft(deltaTime)
                    }
                    KeyEvent.VK_D -> {
                        game.player.moveRight(deltaTime)
                    }
                    KeyEvent.VK_SPACE -> {
                        game.player.shoot()
                    }
                }
            }else if (game.state == GameState.MAIN_MENU) {
                game.mainMenuTyped(e.keyChar)
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return game.state == GameState.STARTED
    }
}