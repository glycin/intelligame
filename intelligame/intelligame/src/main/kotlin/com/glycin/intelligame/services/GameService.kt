package com.glycin.intelligame.services

import com.intellij.openapi.components.Service

@Service
class GameService {
    var score: Int = 0
    var fileOpened = false
}