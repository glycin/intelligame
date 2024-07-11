package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.util.toLongDeltaTime
import com.glycin.intelligame.util.toPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CollisionsManager(
    private val ztdGame: ZtdGame,
    scope: CoroutineScope,
    fps: Long,
) {
    private val deltaTime = fps.toLongDeltaTime()

    init {
        scope.launch(Dispatchers.Default) {
            while(ztdGame.state != ZtdGameState.GAME_OVER){
                checkWorldCollisions()
                delay(deltaTime)
            }
        }
    }

    private fun checkWorldCollisions() {
        val zonicBottom = ztdGame.zonic.getBottomPos().toPoint()
        if(ztdGame.currentTiles.none { it.bounds.contains(zonicBottom) }){
            ztdGame.zonic.falling()
        }else {
            val collidingTile = ztdGame.currentTiles.first { it.bounds.contains(zonicBottom) }
            ztdGame.zonic.land()
        }
    }
}