package com.glycin.intelligame.codehero

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class CodeHeroInput(
    private val game: CodeHeroGame,
    private val project: Project,
): KeyEventDispatcher {
    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED) {
            when (e.keyCode) {

                KeyEvent.VK_SPACE -> {
                    game.onInput()
                }

                KeyEvent.VK_ESCAPE -> {
                    project.service<CodeHeroService>().cleanUp()
                }
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}