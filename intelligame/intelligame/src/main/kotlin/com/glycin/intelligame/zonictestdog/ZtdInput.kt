package com.glycin.intelligame.zonictestdog

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class ZtdInput(
    private val zonic: Zonic,
    private val project: Project,
    private val game: ZtdGame,
): KeyEventDispatcher {
    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED) {
            when (e.keyCode) {
                // Can't use this because of complications with the internal rendering loops
                KeyEvent.VK_SPACE -> {
                    zonic.jump()
                }

                KeyEvent.VK_D -> {
                    zonic.moveRight()
                }

                KeyEvent.VK_A -> {
                    zonic.moveLeft()
                }

                KeyEvent.VK_S -> {
                    zonic.crouch()
                }

                KeyEvent.VK_R -> {
                    game.resetZonic()
                }

                KeyEvent.VK_B -> {
                    game.skipToBoss()
                }

                KeyEvent.VK_ESCAPE -> {
                    project.service<ZtdService>().cleanUp()
                    return true
                }
            }
        }else if (e?.id == KeyEvent.KEY_RELEASED) {
            if(e.keyCode == KeyEvent.VK_W ||
                e.keyCode == KeyEvent.VK_A ||
                e.keyCode == KeyEvent.VK_S ||
                e.keyCode == KeyEvent.VK_D) {
                zonic.idle()
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}