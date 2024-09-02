package com.glycin.intelligame.packageman

import com.glycin.intelligame.packageman.git.GitHistoryDependency
import com.glycin.intelligame.packageman.git.GitHistoryFinder
import com.glycin.intelligame.packageman.maze.MazeGenerator
import com.glycin.intelligame.shared.TextWriter
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toDeltaTime
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.*
import java.awt.Color
import java.awt.KeyboardFocusManager
import java.awt.Point
import kotlin.math.roundToInt

private const val FPS = 120L

class PackmanGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
) {
    var gameState = GameState.MAIN_MENU
    private var mainMenuString = ""
    private val soundManager : PackmanSounds = PackmanSounds()
    private var depStrings = listOf<String>()
    private var mazeString = ""
    private var gitHistoryDependencies = listOf<GitHistoryDependency>()
    private val mainMenuInput: PackmanMainMenuInput = PackmanMainMenuInput(this)

    private lateinit var gameInput: PackmanInput
    private lateinit var component: PackmanComponent

    fun initGame(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(mainMenuInput)
        TextWriter.replaceText(0, editor.document.textLength, PackmanTexts.bannerLoading, editor, project)
        val maxX = editor.xyToLogicalPosition(Point(editor.contentComponent.width, 0)).column
        val maxY = editor.xyToLogicalPosition(Point(0, editor.contentComponent.height)).line

        soundManager.playMainMenuSound()
        scope.launch(Dispatchers.Default) {
            depStrings = collectDependencies()
            mazeString = generateMaze(depStrings.joinToString(""), maxX, maxY)
            gitHistoryDependencies = GitHistoryFinder(project, scope).getDependencyCommits(depStrings)
            TextWriter.replaceText(0, editor.document.textLength, PackmanTexts.bannerDone, editor, project)
        }
    }

    fun mainMenuTyped(c: Char) {
        mainMenuString += c
        if(mainMenuString.contains("start")){
            startGame()
        }
    }

    fun stopGame() {
        component.destroy()
        gameState = GameState.STOPPED
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(gameInput)
        editor.contentComponent.remove(component)
        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()
    }

    private fun startGame() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(mainMenuInput)
        gameState = GameState.STARTED
        TextWriter.replaceTextAndThen(0, editor.document.textLength, mazeString, editor, project) {
            val cells = createMazeObjects()
            val mazeMovementManager = MazeMovementManager(cells)
            val player = createPlayer(cells, mazeMovementManager)
            val state = PackmanState(
                player = player,
                ghosts = createGhosts(cells, gitHistoryDependencies, mazeMovementManager),
                mazeCells = cells,
                gameState = gameState,
            )
            component = attachComponent(state)
            gameInput = PackmanInput(state, soundManager, project)
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(gameInput)
        }
    }

    private fun attachComponent(state: PackmanState) : PackmanComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true
        val component = PackmanComponent(state, scope, project, FPS).apply {
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
                        (nextCharPos.x - charPos.x.roundToInt())
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

    private fun generateMaze(dependencies: String, maxX: Int, maxY: Int): String {

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

    private fun collectDependencies() : List<String> {
        val libStrings = mutableListOf<String>()
        println("collecting dependencies")

        LibraryTablesRegistrar.getInstance().getLibraryTable(project).libraries.forEach { lib ->
            /*lib.getUrls(OrderRootType.CLASSES).forEach {
                println("Project search $it")
                libStrings.add(it)
            }*/
            libStrings.add(lib.name?.replace("Gradle: ", "") ?: "mystery.library")
        }

        // This gets pretty much everything on the classpath
        /*OrderEnumerator.orderEntries(project).recursively().classesRoots.forEach { root ->
            libStrings.add(root.name)
        }*/

        println("collected dependencies")
        return libStrings
    }

    private fun createPlayer(cells: List<MazeCell>, mazeMovementManager: MazeMovementManager): Player {
        val cell = cells.first { !it.isWall}
        return Player(
            position = Vec2(cell.position.x - (cell.width / 2), cell.position.y),
            radius = cell.width * 2,
            cellX = cell.x,
            cellY = cell.y,
            mazeMoveManager = mazeMovementManager,
            sounds = soundManager,
            fps = FPS
        )
    }

    private fun createGhosts(cells: List<MazeCell>, gitHistoryDependencies: List<GitHistoryDependency>, mazeMovementManager: MazeMovementManager): MutableList<Ghost> {
        val ghosts = mutableListOf<Ghost>()
        val walls = cells.filter { !it.isWall && it.x in 5..100 && it.y in 5..50 }
        gitHistoryDependencies.forEach { dependency ->
            val cell = walls.random()
            ghosts.add(
                Ghost(
                    position = cell.position,
                    width = 10,
                    height = 20,
                    cellX = cell.x,
                    cellY = cell.y,
                    mazeMoveManager = mazeMovementManager,
                    deltaTime = FPS.toDeltaTime(),
                    color = getRandomColor(),
                    gitDependency = dependency,
                )
            )
        }
        return ghosts
    }

    private fun getRandomColor(): Color {
        val colors = listOf(
            JBColor.ORANGE.brighter(),
            JBColor.RED.brighter(),
            JBColor.GREEN.brighter(),
            JBColor.YELLOW.brighter(),
            JBColor.BLUE.brighter(),
            JBColor.CYAN.brighter(),
            JBColor.MAGENTA.brighter(),
        )

        return colors.random()
    }
}