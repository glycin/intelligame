package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuParser
import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.shared.TextWriter
import com.glycin.intelligame.util.toVec2
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager
import java.util.LinkedList
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

private const val FPS = 120L

class CodeHeroGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    private val viewPort = (SwingUtilities.getAncestorOfClass(JScrollPane::class.java, editor.contentComponent) as JScrollPane).viewport
    private var pasteHandler: CodeHeroPasteHandler
    private var lettersToWrite = LinkedList<Char>()

    private lateinit var input : CodeHeroInput
    private lateinit var component : CodeHeroComponent
    private lateinit var noteManager: NoteManager
    private lateinit var songPlayer: SongPlayer
    var gameState : CodeHeroState

    init {
        val editorActionManager = EditorActionManager.getInstance()
        val originalPasteHandler = editorActionManager.getActionHandler("EditorPaste")
        pasteHandler = CodeHeroPasteHandler(originalPasteHandler, this)
        editorActionManager.setActionHandler("EditorPaste", pasteHandler)
        gameState = CodeHeroState()
    }

    fun initGame(textToPaste: String) {
        if(gameState.state != CodeHeroStateEnum.STARTED) {
            println("Game is already playing!")
        }

        val textLength = textToPaste.count { c -> c.isLetterOrDigit() }
        val beatmap = if(textLength <= 30){
            "MasterSwordRemix_An_Acquittal.osu"
        }else if (textLength < 230){
            "POWERWOLF_Army_Of_The-Night.osu"
        }else {
            "DragonForce_Through_the_Fire_and_Flames.osu"
        }

        val song = OsuParser().parse(beatmap)
        noteManager = NoteManager(
            spawnPositionLeft = Fec2(0f, ((viewPort.height / 1.5).roundToInt()) + 10f),
            spawnPositionRight = Fec2(viewPort.width.toFloat(), ((viewPort.height / 1.5).roundToInt()) + 10f),
            targetPosition = Fec2(viewPort.width.toFloat() / 2, ((viewPort.height / 1.5).roundToInt()).toFloat()),
            game = this,
            fps = FPS
        )
        component = attachComponent(noteManager)
        songPlayer = SongPlayer(song , scope, noteManager)
        input = CodeHeroInput(this, project)
        songPlayer.start()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(input)
        startPaste(textToPaste)
        gameState.state = CodeHeroStateEnum.PLAYING
    }

    fun stopGame() {
        gameState.state = CodeHeroStateEnum.GAME_OVER
        lettersToWrite.clear()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(input)
        component.destroy()
        editor.contentComponent.remove(component)
        songPlayer.stop()
        noteManager.cleanup()
        EditorActionManager.getInstance().setActionHandler("EditorPaste", pasteHandler.originalHandler)
    }

    fun resetGame() {
        gameState.reset()
        lettersToWrite.clear()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(input)
        component.destroy()
        editor.contentComponent.remove(component)
        songPlayer.stop()
        noteManager.cleanup()
    }

    fun onInput(char: Char) {
        if(noteManager.validHit()){
            if(char.isWhitespace()){
                gameState.onSuccess()
                component.showSucces()
            }else if(char == lettersToWrite.peek()) {
                gameState.onSuccess()
                component.showSucces()
                writeChar()
            }else {
                noteFail()
            }
        }else{
            noteFail()
        }
    }

    fun noteFail(){
        gameState.onFail()
        component.showEpicFail()
    }

    private fun attachComponent(noteManager: NoteManager) : CodeHeroComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true
        val component = CodeHeroComponent(noteManager, scope, gameState, FPS).apply {
            bounds = viewPort.viewRect
            isOpaque = false
        }.apply { start() }

        contentComponent.add(component)
        contentComponent.revalidate()
        contentComponent.repaint()

        component.focus()
        component.requestFocusInWindow()
        return component
    }

    private fun startPaste(textToPaste: String) {
        val lines = textToPaste.lines()
        val textToAdd = lines.joinToString("") { "\n" }
        val caretPos = editor.caretModel.offset
        TextWriter.writeText(caretPos, textToAdd, editor, project)
        val position = editor.offsetToXY(caretPos)
        val maxCharsInLine = lines.maxOf { line -> line.length }
        component.showPastePreview(
            textToPaste = textToPaste,
            position = position.toVec2(),
            width = 10 * maxCharsInLine,
            height = editor.lineHeight * lines.size,
            fontName = editor.colorsScheme.editorFontName,
            fontSize = editor.colorsScheme.editorFontSize,
        )
        textToPaste.forEach {
            lettersToWrite.add(it)
        }
    }

    private fun writeChar() {
        val pos = editor.caretModel.offset
        val next = lettersToWrite.remove()
        var stringToInsert = "$next"

        while (lettersToWrite.isNotEmpty() && !lettersToWrite.peek().isLetterOrDigit()) {
            stringToInsert += lettersToWrite.remove()
        }

        // TODO: I cant write the characters because this forces the whole editor to repaint, messing up with my timings
        TextWriter.writeTextAndThen(pos, stringToInsert, editor, project) {
            editor.caretModel.moveToOffset(pos + stringToInsert.length)
            component.updatePastePreview(stringToInsert)
        }
    }
}