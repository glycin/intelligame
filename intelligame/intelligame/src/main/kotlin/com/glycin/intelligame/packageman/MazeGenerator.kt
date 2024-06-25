package com.glycin.intelligame.packageman

import java.util.*

class MazeGenerator(
    private val width: Int,
    private val height: Int,
) {
    private val maze = Array(height) { CharArray(width) { '#' } }
    private val walls = mutableListOf<Cell>()

    private val directions = arrayOf(
        Cell(-1, 0), // Up
        Cell(1, 0),  // Down
        Cell(0, -1), // Left
        Cell(0, 1)   // Right
    )

    private fun isValidCell(cell: Cell): Boolean {
        return cell.row in 0 until height && cell.col in 0 until width
    }

    private fun isWall(cell: Cell): Boolean {
        return isValidCell(cell) && maze[cell.row][cell.col] == '#'
    }

    private fun isPassage(cell: Cell): Boolean {
        return isValidCell(cell) && maze[cell.row][cell.col] == ' '
    }

    private fun getNeighbors(cell: Cell): List<Cell> {
        return directions.map { Cell(cell.row + it.row, cell.col + it.col) }.filter { isValidCell(it) }
    }

    private fun addWalls(cell: Cell) {
        getNeighbors(cell).filter { isWall(it) }.forEach { walls.add(it) }
    }

    fun generate(): Array<CharArray> {
        val start = Cell(0, 0)
        maze[start.row][start.col] = ' '
        addWalls(start)

        while (walls.isNotEmpty()) {
            val wallIndex = Random().nextInt(walls.size)
            val wall = walls.removeAt(wallIndex)

            val passages = getNeighbors(wall).filter { isPassage(it) }
            if (passages.size == 1) {
                maze[wall.row][wall.col] = ' '
                addWalls(wall)
            }
        }

        return maze
    }
}

private data class Cell(val row: Int, val col: Int)
