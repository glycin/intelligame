package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuParser
import com.glycin.intelligame.shared.Fec2
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

private const val FPS = 120L

class CodeHeroGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    fun initGame() {

        val song = OsuParser().parse("MasterSwordRemix_An_Acquittal.osu")
        val noteManager = NoteManager(
            spawnPositionLeft = Fec2(0f, (editor.contentComponent.height / 2) + 10f),
            spawnPositionRight = Fec2(editor.contentComponent.width.toFloat(), (editor.contentComponent.height / 2) + 10f),
            targetPosition = Fec2(editor.component.width.toFloat() / 2, (editor.contentComponent.height / 2).toFloat()),
            fps = FPS
        )
        val component = attachComponent(noteManager)
        val songPlayer = SongPlayer(song , scope, noteManager)
        songPlayer.start()
    }

    fun stopGame() {

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