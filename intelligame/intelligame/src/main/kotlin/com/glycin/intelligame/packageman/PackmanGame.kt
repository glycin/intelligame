package com.glycin.intelligame.packageman

import com.glycin.intelligame.shared.TextWriter
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.impl.OrderEntryUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.TextRange
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
        val dependencyString = collectDependencies()
        val mazeString = generateMaze(dependencyString)
        TextWriter.replaceTextAndThen(0, editor.document.textLength, mazeString, editor, project) {
            val mazeObjects = createMazeObjects()
            val packComponent = attachComponent(mazeObjects)
        }
    }

    private fun attachComponent(mazeObjects: List<MazeCell>) : PackmanComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true
        val component = PackmanComponent(mazeObjects, scope, FPS).apply {
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
                    println("${charLogicalPosition.line} : ${charLogicalPosition.column}")
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
        val generator = MazeGenerator(maxX, maxY)
        val maze = generator.generate()
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

    private fun collectDependencies() : String {
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
        return libStrings.joinToString("")
    }
}