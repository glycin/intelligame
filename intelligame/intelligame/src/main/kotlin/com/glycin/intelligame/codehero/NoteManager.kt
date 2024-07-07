package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Fec2
import com.intellij.ui.JBColor

class NoteManager(
    val notes : MutableMap<Int, Note> = mutableMapOf(),
    private val spawnPositionLeft: Fec2,
    private val spawnPositionRight: Fec2,
    private val targetPosition: Fec2,
    private val game: CodeHeroGame,
    private val fps: Long,
) {

    fun addNote(id: Int, color: JBColor) {
        notes.putIfAbsent(id, Note(
            id = id,
            positionLeft = spawnPositionLeft,
            positionRight = spawnPositionRight,
            width = 20,
            height = 80,
            color = color,
            targetPos = targetPosition,
            fps = fps,
        ))
    }

    fun activateNote(id: Int) {
        notes[id]?.active = true
    }

    fun deactivateNote(id: Int) {
        notes[id]?.let { n ->
            notes.remove(n.id)
            n.destroy()
            if(!n.hitOnTime){
                game.noteFail()
            }
        }
    }

    fun validHit(): Boolean {
        val note = notes.values.firstOrNull() ?: return false
        if(note.active) {
            note.hitOnTime = true
            return true
        }
        return false
    }

    fun cleanup(){
        notes.clear()
    }
}