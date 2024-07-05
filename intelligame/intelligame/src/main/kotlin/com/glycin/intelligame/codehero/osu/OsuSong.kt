package com.glycin.intelligame.codehero.osu

import com.intellij.ui.JBColor
import java.util.LinkedList

class OsuSong(
    val metadata: OsuSongMetadata,
    val filePath: String,
    val colors: List<JBColor>,
    val hits: LinkedList<OsuHit>,
    val shows: LinkedList<OsuShow>,
)