package com.glycin.intelligame.codehero

import com.glycin.intelligame.codehero.osu.OsuSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class SongPlayer(
    private val osuSong: OsuSong,
    private val scope: CoroutineScope,
    private val noteManager: NoteManager,
    private val deltaTime: Long = 8L,
) {

    private var song: Clip? = null
    private val hitWindow = 250L

    fun start() {
        spawnNotes()
    }

    fun stop() {
        song?.stop()
        osuSong.hits.clear()
        osuSong.shows.clear()
    }

    private fun spawnNotes() {
        scope.launch {
            playSong()
            val startTime = System.currentTimeMillis()
            while(osuSong.shows.isNotEmpty() && osuSong.hits.isNotEmpty()) {
                val nextShow = osuSong.shows.peek()
                val nextHit = osuSong.hits.peek()
                val hitWindowStart = nextHit.time - (hitWindow / 2)
                val hitWindowEnd = nextHit.time + (hitWindow / 2)
                val elapsed = System.currentTimeMillis() - startTime

                if(elapsed >= nextShow.time) {
                    noteManager.addNote(nextShow.id, osuSong.colors.random())
                    osuSong.shows.remove()
                }

                if(elapsed >= hitWindowStart && elapsed <= hitWindowEnd) {
                    noteManager.activateNote(nextHit.id)
                }

                if(elapsed > hitWindowEnd) {
                    noteManager.deactivateNote(nextHit.id)
                    osuSong.hits.remove()
                }
                delay(deltaTime)
            }

            println("Song Finished")
        }
    }

    private fun playSong() {
        try {
            if(song == null) {
                this::class.java.getResourceAsStream("/Music/${osuSong.filePath}")
                    ?.let { BufferedInputStream(it) }
                    ?.let { movingClipStream ->
                        song = AudioSystem.getClip()
                        song?.open(AudioSystem.getAudioInputStream(movingClipStream))
                    }
            }
            song?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}