package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.glycin.intelligame.zonictestdog.level.Coin
import com.glycin.intelligame.zonictestdog.level.Tile
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiMethod

class MapCreator(
    private val testMap: MutableMap<String, List<PsiMethod>>,
) {
    private val chosenTiles = mutableSetOf<Tile>()

    fun create(editor: Editor, fileName: String): Pair<MutableList<Tile>, List<Coin>> {
        chosenTiles.clear()
        val tiles = createLevel(editor)
        val coins = placeCoins(tiles, testMap.getOrDefault(fileName, emptyList()))
        //val enemies = placeEnemies(tiles)
        return tiles to coins
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

        return tiles
    }


    private fun placeCoins(tiles: MutableList<Tile>, testMethods: List<PsiMethod>): List<Coin> {
        return testMethods.map {
            Coin(
                position = getPosOnRandomTile(tiles),
                width = 15,
                height = 15,
                method = it
            )
        }
    }

    private fun getPosOnRandomTile(tiles: MutableList<Tile>): Vec2 {
        val randomTile = tiles.subtract(chosenTiles).random()
        chosenTiles.add(randomTile)
        return Vec2(randomTile.position.x + (randomTile.width / 2), randomTile.position.y - (randomTile.height / 2))
    }
}