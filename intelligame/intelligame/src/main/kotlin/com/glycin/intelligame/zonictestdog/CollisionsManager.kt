package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.util.toPoint

class CollisionsManager(
    private val ztdGame: ZtdGame,
) {
    fun canRun(positionsToCheck: List<Fec2>) : Boolean {
        return positionsToCheck.none { position ->
            ztdGame.currentTiles.none { it.bounds.contains(position.toPoint()) }
        }
    }

    fun shouldFall(positionToCheck: Fec2) : Boolean {
        val point = positionToCheck.toPoint()
        return ztdGame.currentTiles.none { it.bounds.contains(point) }
    }

    fun getClosestGround(positionToCheck: Fec2): Int {
        val point = positionToCheck.toPoint()
        val xTiles = ztdGame.currentTiles.filter {
            it.minX >= point.x && point.x <= it.maxX && it.minY > point.y
        }
        if (xTiles.isEmpty()) return -10000 // TODO: Stop at bottom of the map
        return xTiles.minBy { it.minY - point.y }.minY
    }
}