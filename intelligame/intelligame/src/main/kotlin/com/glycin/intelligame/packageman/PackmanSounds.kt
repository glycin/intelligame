package com.glycin.intelligame.packageman

import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class PackmanSounds {

    var mute = true

    private var movingClip: Clip? = null
    private var mainMenuClip: Clip? = null

    fun playMovingSound() {
        if(mute) {
            return
        }

        if(movingClip != null && movingClip!!.isOpen){
            return
        }

        try {
            if(movingClip == null) {
                this::class.java.getResourceAsStream("/Sounds/java_sound.wav")
                    ?.let { BufferedInputStream(it) }
                    ?.let { movingClipStream ->
                        movingClip = AudioSystem.getClip()
                        movingClip?.open(AudioSystem.getAudioInputStream(movingClipStream))
                    }
            }
            movingClip?.loop(Clip.LOOP_CONTINUOUSLY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopMovingSound() {
        movingClip?.stop()
        movingClip?.close()
        movingClip = null
    }

    fun playMainMenuSound() {
        try {
            if(mainMenuClip == null) {
                this::class.java.getResourceAsStream("/Sounds/packman_mainmenu.wav")
                    ?.let { BufferedInputStream(it) }
                    ?.let { movingClipStream ->
                        mainMenuClip = AudioSystem.getClip()
                        mainMenuClip?.open(AudioSystem.getAudioInputStream(movingClipStream))
                    }
            }
            mainMenuClip?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}