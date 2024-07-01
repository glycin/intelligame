package com.glycin.intelligame.packageman

class PackmanState(
    val player: Player,
    val ghosts: MutableList<Ghost>,
    val mazeCells: List<MazeCell>,
)