package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.util.toPoint
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

    fun isZonicInPortal(portalBounds: Rectangle) : Boolean {
        val zonicMidPos = ztdGame.zonic.getMidPos()
        return portalBounds.contains(zonicMidPos.toPoint())
    }
}