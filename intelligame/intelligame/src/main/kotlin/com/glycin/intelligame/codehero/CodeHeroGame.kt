package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuParser
import com.glycin.intelligame.shared.Fec2
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager

private const val FPS = 120L

class CodeHeroGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    private lateinit var input : CodeHeroInput
    private lateinit var component : CodeHeroComponent
    private lateinit var noteManager: NoteManager

    fun initGame() {

        val song = OsuParser().parse("MasterSwordRemix_An_Acquittal.osu")
        noteManager = NoteManager(
            spawnPositionLeft = Fec2(0f, (editor.contentComponent.height / 2) + 10f),
            spawnPositionRight = Fec2(editor.contentComponent.width.toFloat(), (editor.contentComponent.height / 2) + 10f),
            targetPosition = Fec2(editor.contentComponent.width.toFloat() / 2, (editor.contentComponent.height / 2).toFloat()),
            fps = FPS
        )
        component = attachComponent(noteManager)
        val songPlayer = SongPlayer(song , scope, noteManager)
        input = CodeHeroInput(this, project)
        songPlayer.start()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(input)
    }

    fun stopGame() {

    }

    fun onInput() {
        if(noteManager.validHit()){
            component.showSucces()
        }else{
            component.showEpicFail()
        }
    }

    private fun attachComponent(noteManager: NoteManager) : CodeHeroComponent {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true
        val component = CodeHeroComponent(noteManager, scope, FPS).apply {
            bounds = contentComponent.bounds
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