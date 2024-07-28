package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.util.toPoint
import com.glycin.intelligame.zonictestdog.level.Coin
import com.glycin.intelligame.zonictestdog.level.Portal
import com.glycin.intelligame.zonictestdog.level.WalkingEnemy
import java.awt.Point
import java.awt.Rectangle

class CollisionsManager(
    private val ztdGame: ZtdGame,
) {
    fun canRun(positionsToCheck: List<Fec2>) : Boolean {
        return positionsToCheck.all { position ->
            ztdGame.currentTiles.none { it.bounds.contains(position.toPoint()) }
        }
    }

    fun shouldFall(positionToCheck: Fec2) : Boolean {
        val point = positionToCheck.toPoint()
        return ztdGame.currentTiles.none { it.bounds.contains(point) }
    }

    fun getClosestGround(positionToCheck: Fec2): Int? {
        return ztdGame.currentTiles.firstOrNull { it.bounds.contains(positionToCheck.toPoint()) }?.minY
    }

    fun portalCheck(zonicPoint: Point): Portal? {
        if(ztdGame.portals.isNotEmpty()){
            return ztdGame.portals.firstOrNull { it.bounds.contains(zonicPoint) }
        }
        return null
    }

    fun coinCheck(zonicPoint: Point): Coin? {
        if(ztdGame.currentCoins.isNotEmpty()) {
            return ztdGame.currentCoins.firstOrNull { it.bounds.contains(zonicPoint) }
        }
        return null
    }

    fun enemyCheck(zonicPoint: Point): WalkingEnemy? {
        if(ztdGame.currentEnemies.isNotEmpty()) {
            return ztdGame.currentEnemies.firstOrNull { it.getBounds().contains(zonicPoint) }
        }
        return null
    }
}