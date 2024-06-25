package com.glycin.intelligame.packageman

import java.util.*

class MazeGenerator(
    private val width: Int,
    private val height: Int,
) {
    private val maze = Array(height) { CharArray(width) { '#' } }
    private val frontier = PriorityQueue<Pair<Int, Cell>>(compareBy { it.first })

    fun generate() : Array<CharArray> {
        val startX = if (width % 2 == 0) 1 else 0
        val startY = if (height % 2 == 0) 1 else 0

        val start = Cell(startX, startY)
        maze[start.y][start.x] = ' '
        addFrontierCells(start)

        while (frontier.isNotEmpty()) {
            val next = frontier.poll().second
            val neighbors = next.neighbors(width, height).filter { maze[it.y][it.x] == ' ' }
            if (neighbors.isNotEmpty()) {
                val neighbor = neighbors[Random().nextInt(neighbors.size)]
                val inBetween = Cell((next.x + neighbor.x) / 2, (next.y + neighbor.y) / 2)
                maze[next.y][next.x] = ' '
                maze[inBetween.y][inBetween.x] = ' '
                addFrontierCells(next)
            }
        }
        return maze
    }

    private fun addFrontierCells(cell: Cell) {
        cell.neighbors(width, height).filter { maze[it.y][it.x] == '#' }.forEach {
            frontier.add(Pair(Random().nextInt(), it))
        }
    }
}

private data class Cell(val x: Int, val y: Int) {
    fun neighbors(width: Int, height: Int): List<Cell> {
        val neighbors = mutableListOf<Cell>()
        if (x > 1) neighbors.add(Cell(x - 2, y))
        if (x < width - 2) neighbors.add(Cell(x + 2, y))
        if (y > 1) neighbors.add(Cell(x, y - 2))
        if (y < height - 2) neighbors.add(Cell(x, y + 2))
        return neighbors
    }
}