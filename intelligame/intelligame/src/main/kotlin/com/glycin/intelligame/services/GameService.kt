package com.glycin.intelligame.services

import com.glycin.intelligame.GameCaretListener
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.CaretModel

@Service
class GameService {
    var score: Int = 0
    var fileOpened = false
}