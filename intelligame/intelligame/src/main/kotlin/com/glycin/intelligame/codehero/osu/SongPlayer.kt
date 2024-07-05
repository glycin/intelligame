package com.glycin.intelligame.codehero.osu

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class SongPlayer(
    private val osuSong: OsuSong,
    private val scope: CoroutineScope,
    private val deltaTime: Long = 8L,
    private val onShowCallback: () -> Unit,
    private val onHitWindowStartCallback: () -> Unit,
    private val onHitWindowEndCallback: () -> Unit,
) {

    private var song: Clip? = null
    private val startDelayMs = 3000L
    private val hitWindow = 250L

    fun start() {
        spawnNotes()
        checkHits()
    }

    fun stop() {
        song?.stop()
    }

    private fun spawnNotes() {
        scope.launch {
            val startTime = System.currentTimeMillis()
            while(osuSong.shows.isNotEmpty()) {
                val nextShow = osuSong.shows.peek()
                val elapsed = System.currentTimeMillis() - startTime
                if(elapsed >= nextShow.time) {
                    onShowCallback()
                    osuSong.shows.remove()
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
                    onHitWindowStartCallback()
                }else if(elapsed >= nextHit.time + hitWindow) {
                    onHitWindowEndCallback()
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