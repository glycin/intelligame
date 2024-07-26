package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.util.toLongDeltaTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EnemyManager(
    private val ztdGame: ZtdGame,
    private val collisionsManager: CollisionsManager,
    scope: CoroutineScope,
    fps: Long,
) {
    init {
        scope.launch(Dispatchers.Default) {
            enemyUpdate(fps.toLongDeltaTime())
        }
    }

    private suspend fun enemyUpdate(deltaTime: Long) {
        while (ztdGame.state == ZtdGameState.STARTED) {
            ztdGame.currentEnemies.forEach {
                if(it.alive){
                    it.update(deltaTime, collisionsManager)
                }
            }
            delay(deltaTime)
        }
    }
}