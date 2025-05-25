package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuParser
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.shared.TextWriter
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.KeyboardFocusManager
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
    private var textToPaste = ""

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

    fun initIntro(textToPaste: String) {
        if(gameState.state != CodeHeroStateEnum.STARTED) {
            println("Game is already playing!")
        }
        val textLength = textToPaste.count { c -> c.isLetterOrDigit() }
        val pasteOffset = editor.caretModel.offset
        scope.launch (Dispatchers.Main) {
            TextWriter.writeText(pasteOffset, IntroStrings.introStrings[0], editor, project)
            delay(3000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.introStrings[0].length, IntroStrings.introStrings[1], editor, project)
            delay(4000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.introStrings[1].length, IntroStrings.difficultyString(textLength), editor, project)
            delay(500)
            val isp = IntroSoundPlayer().also { it.playSong() }
            delay(5000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.difficultyString(textLength).length, IntroStrings.introStrings[2], editor, project)
            delay(2000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.introStrings[2].length, IntroStrings.introStrings[3], editor, project)
            delay(1000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.introStrings[3].length, IntroStrings.introStrings[4], editor, project)
            delay(1000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.introStrings[4].length, IntroStrings.introStrings[5], editor, project)
            delay(1000)
            TextWriter.replaceText(pasteOffset, pasteOffset + IntroStrings.introStrings[5].length, IntroStrings.introStrings[6], editor, project)
            isp.stop()
            TextWriter.deleteText(pasteOffset, pasteOffset + IntroStrings.introStrings[6].length, editor, project)
            initGame(textToPaste, textLength)
        }
    }

    fun initGame(textToPaste: String, textLength: Int) {
        if(gameState.state != CodeHeroStateEnum.STARTED) {
            println("Game is already playing!")
        }
        this.textToPaste = textToPaste
        val beatmap = if(textLength <= 30){
            "MasterSwordRemix_An_Acquittal.osu"
        }else if (textLength < 230){
            "POWERWOLF_Army_Of_The-Night.osu"
        }else {
            "DragonForce_Through_the_Fire_and_Flames.osu"
        }

        val song = OsuParser().parse(beatmap)
        noteManager = NoteManager(
            spawnPositionLeft = Vec2(0f, ((viewPort.height / 1.5).roundToInt()) + 10f),
            spawnPositionRight = Vec2(viewPort.width.toFloat(), ((viewPort.height / 1.5).roundToInt()) + 10f),
            targetPosition = Vec2(viewPort.width.toFloat() / 2, ((viewPort.height / 1.5).roundToInt()).toFloat()),
            game = this,
            fps = FPS,
            scope = scope
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
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(input)
        component.destroy()
        editor.contentComponent.remove(component)
        editor.contentComponent.repaint()
        editor.contentComponent.revalidate()
        songPlayer.stop()
        noteManager.cleanup()
        EditorActionManager.getInstance().setActionHandler("EditorPaste", pasteHandler.originalHandler)
    }

    fun resetGame() {
        gameState.reset()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(input)
        component.destroy()
        editor.contentComponent.remove(component)
        songPlayer.stop()
        noteManager.cleanup()
    }

    fun onInput(char: Char) {
        if(noteManager.validHit() && (char.isLetterOrDigit() || char.isWhitespace())){
            gameState.onSuccess()
            component.checkSuccess(char.uppercaseChar())
        }else{
            noteFail()
        }
    }

    fun noteFail(){
        gameState.onFail()
        component.showEpicFail()
    }

    fun paste() {
        val offset = editor.caretModel.offset
        TextWriter.writeText(offset, textToPaste, editor, project)
        textToPaste = ""
    }

    private fun attachComponent(noteManager: NoteManager) : CodeHeroComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true
        val component = CodeHeroComponent(noteManager, scope, this, FPS).apply {
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
        component.showLetterIndicators(
            textToPaste = textToPaste,
            paneWidth = 250,
            paneHeight = 250,
            fontName = editor.colorsScheme.editorFontName,
        )
    }
}