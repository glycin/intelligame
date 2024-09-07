package com.glycin.intelligame.boom

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent

class BoomInput(
    private val project: Project,
): KeyEventDispatcher {
    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {

        if (e?.id == KeyEvent.KEY_PRESSED) {
            when (e.keyCode) {

                KeyEvent.VK_ESCAPE -> {
                    project.service<BoomService>().stop()
                    return true
                }
            }
        }

        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return false
    }
}