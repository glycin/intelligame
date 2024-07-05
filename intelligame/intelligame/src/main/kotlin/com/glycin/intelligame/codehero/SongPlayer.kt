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
    private val startDelayMs = 1000L
    private val hitWindow = 250L

    fun start() {
        spawnNotes()
    }

    fun stop() {
        song?.stop()
        osuSong.hits.clear()
        osuSong.shows.clear()
    }

    private fun spawn() {
        scope.launch {
            val startTime = System.currentTimeMillis()
            while(osuSong.shows.isNotEmpty()) {
                val nextShow = osuSong.shows.peek()
                val elapsed = System.currentTimeMillis() - startTime
                if(elapsed >= nextShow.time) {
                    noteManager.addNote(nextShow.id, osuSong.colors.random())
                    osuSong.shows.remove()
                }
                delay(deltaTime)
            }
        }
    }

    private fun spawnNotes() {
        scope.launch {
            playSong()
            val startTime = System.currentTimeMillis()
            while(osuSong.shows.isNotEmpty()) {
                val nextShow = osuSong.shows.peek()
                val nextHit = osuSong.hits.peek()

                val elapsed = System.currentTimeMillis() - startTime
                val elapsedForShows = elapsed - 1000
                if(elapsedForShows >= nextShow.time) {
                    noteManager.addNote(nextShow.id, osuSong.colors.random())
                    osuSong.shows.remove()
                }

                if(elapsed >= nextHit.time && elapsed < nextHit.time + hitWindow) {
                    noteManager.activateNote(nextHit.id)
                }else if(elapsed >= nextHit.time + hitWindow) {
                    noteManager.deactivateNote(nextHit.id)
                    osuSong.hits.remove()
                }
                delay(deltaTime)
            }
        }
    }

    private fun checkHits() {
        scope.launch {
            delay(startDelayMs)
            val startTime = System.currentTimeMillis()
            playSong()
            while(osuSong.hits.isNotEmpty()) {
                val nextHit = osuSong.hits.peek()
                val elapsed = System.currentTimeMillis() - startTime
                if(elapsed >= nextHit.time && elapsed < nextHit.time + hitWindow) {
                    noteManager.activateNote(nextHit.id)
                }else if(elapsed >= nextHit.time + hitWindow) {
                    noteManager.deactivateNote(nextHit.id)
                    osuSong.hits.remove()
                }
                delay(deltaTime)
            }
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