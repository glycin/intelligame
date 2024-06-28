package com.glycin.intelligame.packageman.maze

import java.util.*

class MazeGenerator(private val width: Int, private val height: Int) {
    private val grid: Array<Array<Cell>> = Array(height) { y -> Array(width) { x -> Cell(x, y) } }
    private val directions = listOf(
        Pair(0, -1), // Up
        Pair(0, 1),  // Down
        Pair(-1, 0), // Left
        Pair(1, 0)   // Right
    )

    init {
        generateMaze()
    }

    private fun generateMaze() {
        val stack = Stack<Cell>()
        val startCell = grid[0][0]
        startCell.isWall = false
        stack.push(startCell)

        while (stack.isNotEmpty()) {
            val current = stack.peek()
            val neighbors = getUnvisitedNeighbors(current)

            if (neighbors.isNotEmpty()) {
                val next = neighbors.random()
                removeWall(current, next)
                next.isWall = false
                stack.push(next)
            } else {
                stack.pop()
            }
        }
    }

    private fun getUnvisitedNeighbors(cell: Cell): List<Cell> {
        val neighbors = mutableListOf<Cell>()

        for ((dx, dy) in directions) {
            val nx = cell.x + dx * 2
            val ny = cell.y + dy * 2
            if (nx in 0 until width && ny in 0 until height && grid[ny][nx].isWall) {
                neighbors.add(grid[ny][nx])
            }
        }

        return neighbors
    }

    private fun removeWall(current: Cell, next: Cell) {
        val dx = next.x - current.x
        val dy = next.y - current.y
        val wallX = current.x + dx / 2
        val wallY = current.y + dy / 2
        grid[wallY][wallX].isWall = false
    }

    fun getMaze(): Array<CharArray> {
        val array = Array(height) { CharArray(width) }
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                array[y][x] = if(cell.isWall) '#' else ' '
            }
        }
        return array
    }
}

private data class Cell(val x: Int, val y: Int, var isWall: Boolean = true)