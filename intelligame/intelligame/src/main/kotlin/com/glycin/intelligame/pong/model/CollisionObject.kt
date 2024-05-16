package com.glycin.intelligame.pong.model

import com.glycin.intelligame.shared.Vec2

interface CollisionObject {
    fun getCollisionNormal(colPosition: Vec2): Vec2
}