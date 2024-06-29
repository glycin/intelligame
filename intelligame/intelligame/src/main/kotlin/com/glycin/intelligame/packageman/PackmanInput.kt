package com.glycin.intelligame.packageman

import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class PackmanInput(
    private val player: Player,
): KeyEventDispatcher {

    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED ) {
            when (e.keyCode) {
                KeyEvent.VK_A -> {
                    player.moveLeft()
                }
                KeyEvent.VK_D -> {
                    player.moveRight()
                }
                KeyEvent.VK_W -> {
                    player.moveUp()
                }

                KeyEvent.VK_S -> {
                    player.moveDown()
                }

                KeyEvent.VK_ESCAPE -> {
                    //game.cleanUp()
                }
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}