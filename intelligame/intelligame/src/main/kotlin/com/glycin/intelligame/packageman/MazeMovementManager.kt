package com.glycin.intelligame.packageman

import com.glycin.intelligame.shared.Vec2

class MazeMovementManager(
    cells: List<MazeCell>,
) {
    private val maze = cells.associateBy { "${it.x}-${it.y}" }

    fun canMoveTo(x: Int, y: Int): Boolean {
        val cell = maze["${x}-${y}"] ?: return false
        return !cell.isWall
    }

    fun getMazeCellMidPosition(x: Int, y: Int): Vec2 {
        val cell = maze["${x}-${y}"] ?: return Vec2.zero
        return cell.toCellMidPos()
    }

    fun getMazeCellPosition(x: Int, y: Int): Vec2 {
        val cell = maze["${x}-${y}"] ?: return Vec2.zero
        return cell.position
    }

    private fun MazeCell.toCellMidPos() = Vec2(position.x - (width / 2), position.y)

}