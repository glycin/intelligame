package com.glycin.intelligame.grandFinale

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import mochadoom.Engine
import java.io.File

class Finale(
    private val project: Project,
    private val scope: CoroutineScope
) {

    fun show(editor: Editor) {
        val wad = resolveWad()
        if (wad == null) {
            thisLogger().warn("doom1.wad not found; Doom may fail to start. Expected it under <projectDir>/target/.")
        }
        Engine.runFromFrame(editor.contentComponent, wad?.absolutePath)
    }

    private fun resolveWad(): File? {
        return listOf(
            CANONICAL_WAD,
            File(System.getProperty("user.dir"), "target/doom1.wad"),
            File("target/doom1.wad"),
        ).firstOrNull { it.isFile }
    }

    companion object {
        val CANONICAL_WAD: File = File("/Users/glycin/Projects/intelligame_doom_wad/doom1.wad")
    }
}