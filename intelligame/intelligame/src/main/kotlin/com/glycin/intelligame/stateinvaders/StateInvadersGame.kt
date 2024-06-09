package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.TextWriter
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.GameState
import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.stateinvaders.model.Stalien
import com.intellij.codeInsight.completion.AllClassesGetter
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.KeyboardFocusManager
import kotlin.math.roundToInt

private const val FPS = 120L

@Service
class StateInvadersGame(private val scope: CoroutineScope) {

    var state = GameState.IDLE
    lateinit var cm: CollisionManager
    lateinit var bm: BulletManager
    lateinit var sm: StalienManager
    lateinit var player: SpaceShip
    private lateinit var gameComponent: StateInvadersComponent
    private lateinit var openEditor: Editor
    private lateinit var openProject: Project
    private lateinit var input: StateInvadersInput
    private var codeBlock = ""
    private var staliens = listOf<Stalien>()
    private var mainMenuTyped = ""
    private var aliveStaliens = 0;
    private var score = 100

    fun initGame(project: Project, editor: Editor) {
        println("STATE INVADERS STARTED")
        openEditor = editor
        openProject = project
        editor.settings.isVirtualSpace = true
        createMainMenu()
        state = GameState.MAIN_MENU
    }

    fun gameOver() {
        println("game over!")
    }

    fun cleanUp(){
        openEditor.contentComponent.remove(gameComponent)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(input)
        openEditor.contentComponent.revalidate()
        openEditor.contentComponent.repaint()
        score = 100
        codeBlock = ""
        state = GameState.DESTROYED
    }

    fun destroyStalien(collided: Stalien) {
        gameComponent.removeStalien(collided)
        sm.destroyAlien(collided)
        val oldTextLength = GameTexts.getScoreText(score).length
        aliveStaliens--
        score = ((aliveStaliens.toDouble() / staliens.size.toDouble()) * 100.0).roundToInt()
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(openProject) {
                collided.originalPsiField.delete()
            }
        }
        TextWriter.replaceText(15, 15 + oldTextLength, GameTexts.getScoreText(score), openEditor, openProject)
    }

    fun mainMenuTyped(char: Char) {
        mainMenuTyped += char
        if (mainMenuTyped.contains("start")) {
            state = GameState.CUTSCENE
            mainMenuTyped = ""
            doOpeningAnimation()
        }
    }

    private fun doOpeningAnimation() {
        TextWriter.deleteText(0, openEditor.document.textLength - codeBlock.length, openEditor, openProject)
        TextWriter.writeText(0, GameTexts.getCutscenePlaceholder(), openEditor, openProject)

        scope.launch (Dispatchers.Main) {
            GameTexts.openingCutsceneTexts.forEach { line ->
                var lineOffset = 0
                line.forEach { char ->
                    TextWriter.writeText(15 + lineOffset, char.toString(), openEditor, openProject)
                    lineOffset++
                }
                delay(3000)
                TextWriter.deleteText(15, 15 + line.length, openEditor, openProject)
            }
        }.invokeOnCompletion {
            state = GameState.STARTED
            startGame()
        }
    }

    private fun startGame() {
        TextWriter.deleteText(0, openEditor.document.textLength - codeBlock.length, openEditor, openProject)
        TextWriter.writeText(0, GameTexts.getCutscenePlaceholder(), openEditor, openProject)
        TextWriter.writeText(15, GameTexts.getScoreText(score), openEditor, openProject)
        bm = BulletManager(mutableListOf(), FPS)

        player = SpaceShip(
            position = Vec2(
                x = openEditor.component.width / 2,
                y = openEditor.component.height - 100
            ),
            width = 64,
            height = 64,
            mapMinX = 0,
            mapMaxX = openEditor.contentComponent.width - 50,
            game = this,
        )

        cm = CollisionManager(player, staliens.toMutableList())

        attachGameToEditor(openEditor, staliens, player)
            .apply { start() }
    }

    private fun List<Stalien>.positionAliens(editor: Editor): List<Stalien> {
        val maxWidth = (editor.contentComponent.width * 0.5).roundToInt()
        val widthSpacing = 70
        val heightSpacing = (editor.lineHeight * 2.5).roundToInt()
        var curWidth = 0
        var curHeight = -(heightSpacing * 2)
        forEach { alien ->
            alien.position = Vec2(curWidth, curHeight)
            if (curWidth + alien.width + widthSpacing > maxWidth) {
                curWidth = 0
                curHeight += heightSpacing
            } else {
                curWidth += (alien.width + widthSpacing)
            }
        }

        return this.toMutableList()
    }

    private fun collectMutableFields(project: Project): List<PsiField> {
        val allFields = mutableListOf<PsiField>()

        val processor = Processor<PsiClass> { psiClass ->
            psiClass.fields.forEach { field ->
                if (!field.hasModifierProperty(PsiModifier.FINAL)) {
                    allFields.add(field)
                }
            }
            true
        }

        AllClassesGetter.processJavaClasses(
            PlainPrefixMatcher(""),
            project,
            GlobalSearchScope.projectScope(project),
            processor
        )

        return allFields
    }

    private fun attachGameToEditor(
        editor: Editor, aliens: List<Stalien>, spaceShip: SpaceShip
    ): StateInvadersComponent {
        val contentComponent = editor.contentComponent

        sm = StalienManager(aliens.toMutableList(), 0, editor.contentComponent.width, spaceShip, FPS)
        // Create and configure the Pong game component
        gameComponent = StateInvadersComponent(spaceShip, this, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }

        // Add the Pong game component as an overlay
        contentComponent.add(gameComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        // Request focus for the Pong game to ensure it receives key events
        gameComponent.requestFocusInWindow()
        return gameComponent
    }

    private fun createMainMenu() {
        staliens = collectMutableFields(openProject).map { field ->
            Stalien(
                position = Vec2.zero,
                width = field.text.length * 8,
                height = openEditor.lineHeight,
                text = field.text,
                originalPsiField = field,
                game = this,
            )
        }.positionAliens(openEditor)

        aliveStaliens = staliens.size
        val docText = openEditor.document.text
        val flattened = docText.replace("\n", "").replace("\t", "").replace(" ", "")
        val maxCharsPerLine = 150
        val lines = flattened.length / maxCharsPerLine

        val sb = StringBuilder(flattened)
        for(i in 0 until lines) {
            sb.insert(i * maxCharsPerLine, "\n")
        }

        codeBlock = sb.toString()

        for(i in 0 until 15) {
            sb.insert(0, "\n")
        }

        sb.insert(0, "\t\t\t ${GameTexts.mainMenuMsg} \t\t\t")

        for(i in 0 until 10) {
            sb.insert(0, "\n")
        }

        sb.insert(0, GameTexts.banner)
        TextWriter.replaceText(0, openEditor.document.textLength, sb.toString(), openEditor, openProject)

        input = StateInvadersInput(this, FPS)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(input)
    }
}