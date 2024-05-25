package com.glycin.intelligame.shared

interface CollisionObject {
    fun getCollisionNormal(colPosition: Vec2): Vec2
}