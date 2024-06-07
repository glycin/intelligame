package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.util.toDeltaTime
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class StateInvadersInput(
    private val player: SpaceShip,
    fps: Long,
): KeyEventDispatcher {

    private val deltaTime = fps.toDeltaTime()

    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED) {
            when (e.keyCode) {
                KeyEvent.VK_A -> {
                    player.moveLeft(deltaTime)
                }
                KeyEvent.VK_D -> {
                    player.moveRight(deltaTime)
                }
                KeyEvent.VK_SPACE -> {
                    player.shoot()
                }
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}