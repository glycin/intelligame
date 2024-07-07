package com.glycin.intelligame.codehero

import java.awt.Image
import javax.swing.ImageIcon

class AnimationLoader() {
    val successSprites = listOf<Image> (
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_0.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_1.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_2.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_3.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_4.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_5.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_6.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_7.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/thunderFrames/frame_8.png")).image
    )

    val failSprites = listOf<Image> (
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/01.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/02.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/03.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/04.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/05.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/06.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/07.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/08.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/09.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/10.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/11.png")).image,
        ImageIcon(this::class.java.getResource("/Sprites/poofFrames/12.png")).image
    )
}