package com.glycin.intelligame.services

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

@Service
class InputService(private val scope: CoroutineScope) {

    fun init(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(MyKeyEventDispatcher())
    }
}

//TODO Add a list of invokable actions that can be done on A or D
class MyKeyEventDispatcher : KeyEventDispatcher {
    override fun dispatchKeyEvent(e: KeyEvent?): Boolean {
        if (e?.id == KeyEvent.KEY_PRESSED) {
            when (e.keyCode) {
                KeyEvent.VK_A -> {
                    println("A key pressed")
                }
                KeyEvent.VK_D -> {
                    println("D key pressed")
                }
            }
        }
        // Returning false allows the event to be redispatched to the target component
        // Returning true consumes the event, preventing it from being redispatched
        return true
    }
}