package com.glycin.intelligame.pong

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service
class PongService(private val scope: CoroutineScope) {
    fun initGame(){
        println("GAME STARTED!")
    }
}