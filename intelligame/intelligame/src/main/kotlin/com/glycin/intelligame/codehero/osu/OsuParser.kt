package com.glycin.intelligame.codehero.osu

import com.intellij.ui.JBColor
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayDeque

class OsuParser {

    fun parse(filename: String): OsuSong {
        println("parsing $filename")

        val sections = readFile(filename)

        println("parsed ${sections.size} sections")

        val audioFilePath = parseGeneral(sections["General"]!!)
        val metadata = parseMetadata(sections["Metadata"]!!)
        val colors = parseColors(sections["Colours"]!!)
        val (hitObjects, showObjects) = parseHitObjects(sections["HitObjects"]!!)
        return OsuSong(
            metadata = metadata,
            filePath = audioFilePath,
            colors = colors,
            hits = hitObjects,
            shows = showObjects,
        )
    }

    private fun parseGeneral(section: OsuFileSection): String {
        section.content.forEach {
            val split = it.split(":")
            if(split[0].trim() == "AudioFilename"){
                return split[1].trim()
            }
        }
        return "missing file"
    }

    private fun parseMetadata(section: OsuFileSection): OsuSongMetadata {
        var title = "Unknown"
        var author = "Unknown"
        var creator = "Unknown"

        section.content.forEach {
            val split = it.split(":")
            when(split[0].trim()){
                "Title" -> title = split[1].trim()
                "Artist" -> author = split[1].trim()
                "Creator" -> creator = split[1].trim()
            }
        }

        return OsuSongMetadata(title, author, creator)
    }

    private fun parseColors(section: OsuFileSection): List<JBColor> {
        val colors = mutableListOf<JBColor>()

        section.content.forEach {
            val split = it.split(":")
            if(split[0].trim().startsWith("Combo")){
                val (r,g,b) = split[1].trim().split(',')
                val c = Color(r.toInt(), g.toInt(), b.toInt())
                colors.add(JBColor(c, c))
            }
        }

        return colors
    }

    private fun parseHitObjects(section: OsuFileSection): Pair<LinkedList<OsuHit>, LinkedList<OsuShow>> {
        val hitObjs = LinkedList<OsuHit>()
        val showObjs = LinkedList<OsuShow>()
        section.content.forEachIndexed { index, s ->
            val split = s.split(",")
            val time = split[2].toLong()
            val type = split[3].toInt() // TODO: If we have a slider or a spinner create multiple hit objects between start and end time
            hitObjs.add(
                OsuHit(
                    id = index,
                    time = time,
                )
            )

            showObjs.add(OsuShow(
                id = index,
                time = time - 1000L,
            ))
        }
        return Pair(hitObjs, showObjs)
    }

    private fun readFile(filename: String): MutableMap<String, OsuFileSection> {
        val sections = mutableMapOf<String, OsuFileSection>()
        var currentSection: OsuFileSection? = null

        this::class.java.getResourceAsStream("/Beatmaps/${filename}")?.let { inputStream ->
            inputStream.bufferedReader().forEachLine { line ->
                when{
                    line.trim().isEmpty() -> {}
                    line.startsWith("[") && line.endsWith("]") -> {
                        val sName = line.substringAfter("[").substringBefore("]")
                        currentSection = OsuFileSection(sName)
                        sections.putIfAbsent(currentSection!!.name, currentSection!!)
                    }
                    else -> {
                        currentSection?.content?.add(line.trim())
                    }
                }
            }
        }

        return sections
    }
}