package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuParser
import com.glycin.intelligame.codehero.osu.SongPlayer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

class CodeHeroGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    fun initGame() {

        val song = OsuParser().parse("MasterSwordRemix_An_Acquittal.osu")
        val songPlayer = SongPlayer(song , scope)
        songPlayer.start()
    }

    fun stopGame() {

    }
}