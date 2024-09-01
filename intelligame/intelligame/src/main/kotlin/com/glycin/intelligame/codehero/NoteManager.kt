package com.glycin.intelligame.codehero

import com.glycin.intelligame.shared.Vec2
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope

class NoteManager(
    val notes : MutableMap<Int, Note> = mutableMapOf(),
    private val spawnPositionLeft: Vec2,
    private val spawnPositionRight: Vec2,
    private val targetPosition: Vec2,
    private val game: CodeHeroGame,
    private val fps: Long,
    private val scope: CoroutineScope,
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
            scope = scope,
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

    fun deactivateFirst() {
        if(notes.isEmpty()) return
        val id = notes.minOf { it.key }
        deactivateNote(id)
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