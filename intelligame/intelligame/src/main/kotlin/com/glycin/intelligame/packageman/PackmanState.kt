package com.glycin.intelligame.packageman

class PackmanState(
    val player: Player,
    val ghosts: List<Ghost>,
    val mazeCells: List<MazeCell>,
    val pickups: Int,
)