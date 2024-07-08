package com.glycin.intelligame.codehero

import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class IntroSoundPlayer {
    private var introSound: Clip? = null

    fun playSong() {
        try {
            if(introSound == null) {
                this::class.java.getResourceAsStream("/Sounds/code_hero_intro.wav")
                    ?.let { BufferedInputStream(it) }
                    ?.let { movingClipStream ->
                        introSound = AudioSystem.getClip()
                        introSound?.open(AudioSystem.getAudioInputStream(movingClipStream))
                    }
            }
            introSound?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        introSound?.stop()
    }
}