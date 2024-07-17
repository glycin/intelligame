package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.glycin.intelligame.zonictestdog.level.Tile
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.TextRange

class MapCreator() {
    fun create(editor: Editor): MutableList<Tile> {
        return createLevel(editor)
    }

    private fun createLevel(editor: Editor) : MutableList<Tile>  {
        val document = editor.document
        val tiles = mutableListOf<Tile>()
        val lineHeight = editor.lineHeight
        val scrollOffset = editor.scrollingModel.verticalScrollOffset

        for(line in 0 until document.lineCount) {
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)

            val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))
            val lineTextStartIndex = lineText.indexOfFirst { !it.isWhitespace() }

            if(lineTextStartIndex == -1) {
                continue
            }

            val firstTwoLineChars = lineText.filter { !it.isWhitespace() }.take(2)
            if(firstTwoLineChars.startsWith("//")) {
                continue
            }

            val startLogicalPosition = LogicalPosition(line, lineTextStartIndex)

            val startPos = editor.logicalPositionToXY(startLogicalPosition).toVec2(scrollOffset)
            val endPos = editor.offsetToXY(lineEndOffset).toVec2(scrollOffset)
            val width = endPos.x - startPos.x
            val tileWidth = 32
            val tileCount = width / tileWidth
            for(i in 0 until tileCount + 1) { // We prefer to have a slighter wider platform than shorter
                tiles.add(
                    Tile(
                        position = startPos + (Vec2.right * (tileWidth * i)),
                        width = tileWidth,
                        height = lineHeight,
                    )
                )
            }
        }

        // Top side of the map
        /*obstacles.add(
            Obstacle(
                position = Vec2(0, scrollOffset),
                width = editor.contentComponent.width,
                height = 5
            )
        )

        // Bottom side of the map
        obstacles.add(
            Obstacle(
                position = Vec2(0, (editor.component.height + (scrollOffset - 5))),
                width = editor.contentComponent.width,
                height = 5
            )
        )*/

        return tiles
    }
}