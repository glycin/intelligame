package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuParser
import com.glycin.intelligame.codehero.osu.OsuSong
import com.glycin.intelligame.shared.Fec2
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
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
    private lateinit var input : CodeHeroInput
    private lateinit var component : CodeHeroComponent
    private lateinit var noteManager: NoteManager
    private lateinit var gameState : CodeHeroState

    init {
        val editorActionManager = EditorActionManager.getInstance()
        val originalPasteHandler = editorActionManager.getActionHandler("EditorPaste")
        pasteHandler = CodeHeroPasteHandler(originalPasteHandler, this)
        editorActionManager.setActionHandler("EditorPaste", pasteHandler)
    }

    fun initGame(textToPaste: String) {
        val textLength = textToPaste.count { c -> c.isLetterOrDigit() }
        val beatmap = if(textLength <= 30){
            "MasterSwordRemix_An_Acquittal.osu"
        }else if (textLength < 230){
            "POWERWOLF_Army_Of_The-Night.osu"
        }else {
            "DragonForce_Through_the_Fire_and_Flames.osu"
        }
        val song = OsuParser().parse(beatmap)
        gameState = CodeHeroState(song.hits.size)
        noteManager = NoteManager(
            spawnPositionLeft = Fec2(0f, ((viewPort.height / 1.5).roundToInt()) + 10f),
            spawnPositionRight = Fec2(viewPort.width.toFloat(), ((viewPort.height / 1.5).roundToInt()) + 10f),
            targetPosition = Fec2(viewPort.width.toFloat() / 2, ((viewPort.height / 1.5).roundToInt()).toFloat()),
            game = this,
            fps = FPS
        )
        component = attachComponent(noteManager)
        val songPlayer = SongPlayer(song , scope, noteManager)
        input = CodeHeroInput(this, project)
        songPlayer.start()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(input)
    }

    fun stopGame() {
        gameState.state = CodeHeroStateEnum.GAME_OVER
    }

    fun onInput() {
        if(noteManager.validHit()){
            gameState.onSuccess()
            component.showSucces()
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
}