package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.glycin.intelligame.zonictestdog.level.Coin
import com.glycin.intelligame.zonictestdog.level.Tile
import com.glycin.intelligame.zonictestdog.level.WalkingEnemy
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiMethod
import kotlin.random.Random

class MapCreator(
    private val testMap: MutableMap<String, List<PsiMethod>>,
    private val totalTests: Int,
) {
    private val coinChosenTiles = mutableSetOf<Tile>()
    private val enemyChosenTiles = mutableSetOf<Tile>()

    fun create(editor: Editor, fileName: String): Triple<MutableList<Tile>, List<Coin>, List<WalkingEnemy>> {
        coinChosenTiles.clear()
        val tiles = createLevel(editor)
        val coins = placeCoins(tiles, testMap.getOrDefault(fileName, emptyList()))
        val enemies = placeEnemies(tiles)
        return Triple(tiles,coins, enemies)
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
        val coinWidth = 15
        val coinHeight = 15

        return testMethods.map {
            val midPosAboveTile = getPosOnRandomTile(tiles, coinChosenTiles)
            val pos = Vec2(
                x = midPosAboveTile.x - (coinWidth / 2),
                y = midPosAboveTile.y - coinHeight - 10
            )

            Coin(
                position = pos,
                width = 15,
                height = 15,
                method = it
            )
        }
    }

    private fun placeEnemies(tiles: MutableList<Tile>): List<WalkingEnemy> {
        val enemies = mutableListOf<WalkingEnemy>()
        val enemyWidth = 16
        val enemyHeight = 16

        for(i in 0 until getEnemyCount()){
            val midPosAboveTile = getPosOnRandomTile(tiles, enemyChosenTiles)
            val pos = Fec2(
                x = midPosAboveTile.x - (enemyWidth / 2.0f),
                y = midPosAboveTile.y - enemyHeight.toFloat()
            )
            enemies.add(
                WalkingEnemy(
                    position = pos,
                    width = 25,
                    height = 25,
                )
            )
        }

        return enemies
    }

    private fun getPosOnRandomTile(tiles: MutableList<Tile>, chosenTiles: MutableSet<Tile>): Vec2 {
        val randomTile = tiles.subtract(chosenTiles).random()
        chosenTiles.add(randomTile)
        return Vec2(randomTile.position.x + (randomTile.width / 2), randomTile.position.y)
    }

    private fun getEnemyCount(): Int {
        return when(totalTests){
            in 0 .. 10 -> Random.nextInt(25, 50)
            in 10 .. 100 -> Random.nextInt(15, 25)
            in 100 .. 500 -> Random.nextInt(10, 20)
            else -> Random.nextInt(3, 8)
        }
    }
}