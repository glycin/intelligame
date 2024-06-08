package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.stateinvaders.model.Stalien

class CollisionManager(
    private val spaceship: SpaceShip,
    val staliens: MutableList<Stalien>
) {
    fun collidingStaliens(position: Vec2): Stalien? {
        return staliens.firstOrNull { alien ->
            (position.x in alien.minX()..alien.maxX()) && (position.y in alien.minY()..alien.maxY())
        }
    }

    fun collidingPlayer(position: Vec2): SpaceShip? {
        return if((position.x in spaceship.minX()..spaceship.maxX()) && (position.y in spaceship.minY()..spaceship.maxY())){
            spaceship
        }else null
    }
}