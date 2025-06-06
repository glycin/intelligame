package com.glycin.intelligame.packageman

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class PackmanInput(
    private val state: PackmanState,
    private val soundManager: PackmanSounds,
    private val project: Project,
): KeyEventDispatcher {

    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED && state.gameState == GameState.STARTED) {
            when (e.keyCode) {
                KeyEvent.VK_A -> {
                    state.player.moveLeft()
                }
                KeyEvent.VK_D -> {
                    state.player.moveRight()
                }
                KeyEvent.VK_W -> {
                    state.player.moveUp()
                }

                KeyEvent.VK_S -> {
                    state.player.moveDown()
                }

                KeyEvent.VK_SPACE -> {
                    state.ghosts.forEach {
                        it.toggleLabels()
                    }
                }

                KeyEvent.VK_M -> {
                    soundManager.mute = !soundManager.mute
                    if(soundManager.mute) {
                        soundManager.stopMovingSound()
                    }
                }

                KeyEvent.VK_ESCAPE -> {
                    project.service<PackmanService>().cleanUp()
                }
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}