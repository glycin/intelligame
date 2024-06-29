package com.glycin.intelligame.packageman

import com.glycin.intelligame.packageman.maze.MazeGenerator
import com.glycin.intelligame.shared.TextWriter
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.impl.OrderEntryUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.CoroutineScope
import java.awt.Point

private const val FPS = 120L

class PackmanGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
) {

    fun startGame(){
        val (dependencyString, dependencyCount) = collectDependencies()
        val mazeString = generateMaze(dependencyString)
        TextWriter.replaceTextAndThen(0, editor.document.textLength, mazeString, editor, project) {
            val cells = createMazeObjects()
            val state = PackmanState(
                player = createPlayer(cells),
                ghosts = createGhosts(cells, dependencyCount),
                mazeCells = cells,
                pickups = 0,
            )
            val packComponent = attachComponent(state)
        }
    }

    private fun attachComponent(state: PackmanState) : PackmanComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true
        val component = PackmanComponent(state, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }.apply { start() }

        contentComponent.add(component)
        contentComponent.revalidate()
        contentComponent.repaint()

        component.requestFocusInWindow()
        return component
    }

    private fun createMazeObjects() : List<MazeCell> {
        val document = editor.document
        val mazeCells = mutableListOf<MazeCell>()
        val lineHeight = editor.lineHeight
        val scrollOffset = editor.scrollingModel.verticalScrollOffset
        return GraphicsUtil.safelyGetGraphics(editor.component)?.let { graphics ->
            for(line in 0 until document.lineCount) {
                val lineStartOffset = document.getLineStartOffset(line)
                val lineEndOffset = document.getLineEndOffset(line)

                val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))
                lineText.forEachIndexed { index, c ->
                    val charLogicalPosition = LogicalPosition(line, index)

                    val charPos = editor.logicalPositionToXY(charLogicalPosition).toVec2(scrollOffset)
                    val nextCharOffset = lineStartOffset + index + 1

                    val charWidth = if (nextCharOffset < lineEndOffset) {
                        val nextCharPos = editor.offsetToXY(nextCharOffset)
                        (nextCharPos.x - charPos.x)
                    } else {
                        graphics.fontMetrics.charWidth(c) * 2 //The charWidth() width is always 2 small so we just make it bigger ¯\_(ツ)_/¯
                    }

                    mazeCells.add(
                        MazeCell(
                            x = charLogicalPosition.column,
                            y = charLogicalPosition.line,
                            position = charPos,
                            width = charWidth,
                            height = lineHeight,
                            isWall =  c != ' '
                        )
                    )
                }
            }
            mazeCells
        } ?: emptyList()
    }

    private fun generateMaze(dependencies: String): String {
        val maxX = editor.xyToLogicalPosition(Point(editor.contentComponent.width, 0)).column
        val maxY = editor.xyToLogicalPosition(Point(0, editor.contentComponent.height)).line
        val maze = MazeGenerator(maxX, maxY).getMaze()

        val sb = StringBuilder()
        var dependencyStringIndex = 0
        maze.forEach { line ->
            line.forEach { column ->
                if(column == '#') {
                    sb.append(dependencies[dependencyStringIndex])
                    dependencyStringIndex++
                    if(dependencyStringIndex >= dependencies.length) {
                        dependencyStringIndex = 0
                    }
                } else {
                    sb.append(column)
                }
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    private fun collectDependencies() : Pair<String, Int> {
        val libStrings = mutableListOf<String>()
        println("collecting dependencies")

        project.modules.forEach { module ->
            OrderEntryUtil.getModuleLibraries(ModuleRootManager.getInstance(module)).forEach { lib ->
                /*lib.getUrls(OrderRootType.CLASSES).forEach {
                    println("Module search $it")
                    libStrings.add(it)
                }*/

                libStrings.add(lib.name ?: "mystery.library")
            }
        }

        LibraryTablesRegistrar.getInstance().getLibraryTable(project).libraries.forEach { lib ->
            /*lib.getUrls(OrderRootType.CLASSES).forEach {
                println("Project search $it")
                libStrings.add(it)
            }*/

            libStrings.add(lib.name ?: "mystery.library")
        }

        LibraryTablesRegistrar.getInstance().libraryTable.libraries.forEach { lib ->
            /*lib.getUrls(OrderRootType.CLASSES).forEach {
                println("Application search $it")
                libStrings.add(it)
            }*/

            libStrings.add(lib.name ?: "mystery.library")
        }


        OrderEnumerator.orderEntries(project).recursively().classesRoots.forEach { root ->
            libStrings.add(root.name)
        }


        println("collected dependencies")
        return libStrings.joinToString("") to libStrings.size
    }

    private fun createPlayer(cells: List<MazeCell>): Player {
        val cell = cells.first { !it.isWall}
        return Player(
            position = Vec2(cell.position.x - (cell.width / 2), cell.position.y),
            radius = cell.width * 2,
            cellX = cell.x,
            cellY = cell.y,
            fps = FPS
        )
    }

    private fun createGhosts(cells: List<MazeCell>, count: Int): List<Ghost> {
        val ghosts = mutableListOf<Ghost>()
        val walls = cells.filter { !it.isWall && it.x > 10 && it.y > 10 }
        for(i in 0 until count) {
            val cell = walls.random()
            println("spawning ghost: ${cell.position} and ${cell.x}, ${cell.y} with width ${cell.width} and height ${cell.height}")
            ghosts.add(
                Ghost(
                    position = cell.position,
                    width = 10,
                    height = 20,
                    cellX = cell.x,
                    cellY = cell.y,
                    color = if(i % 2 == 0) JBColor.BLUE.brighter() else JBColor.ORANGE.brighter()
                )
            )
        }
        return ghosts
    }
}