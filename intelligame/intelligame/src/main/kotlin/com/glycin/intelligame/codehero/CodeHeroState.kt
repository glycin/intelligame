package com.glycin.intelligame.codehero

import kotlin.math.abs

class CodeHeroState {
    var state = CodeHeroStateEnum.STARTED
    var score = 0
    var multiplier = 0
    private var notesHit = 0
    private var notesMissed = 0
    fun getMotivationalText() = MotivationalTexts.getText(notesHit, notesMissed)

    fun onSuccess(){
        multiplier++
        multiplier = multiplier.coerceAtMost(8)
        score += 100 * multiplier
        notesHit++
    }

    fun onFail(){
        multiplier = 0
        notesMissed++
    }

    fun reset() {
        state = CodeHeroStateEnum.STARTED
        score = 0
        notesHit = 0
        notesMissed = 0
        multiplier = 0
    }
}

enum class CodeHeroStateEnum {
    STARTED,
    PLAYING,
    GAME_OVER,
}

object MotivationalTexts {
    fun getText(hit: Int, missed: Int): String {
        val diff = abs(hit - missed)
        println("Hit: $hit, Missed: $missed, diff: $diff")
        return when {
            isPerfect(hit, missed) -> perfectTexts.random()
            isWinning(hit, missed, diff) -> winningTexts.random()
            isNeutral(diff) -> neutralTexts.random()
            isLosing(hit, missed, diff) -> losingTexts.random()
            else -> horribleTexts.random() // Fallback for horrible performance
        }
    }

    private fun isPerfect(hit: Int, missed: Int) = hit > 10 && missed <= 10

    private fun isWinning(hit: Int, missed: Int, diff: Int) = hit > missed && diff > 10

    private fun isNeutral(diff: Int) = diff <= 10

    private fun isLosing(hit: Int, missed: Int, diff: Int) = missed > hit && diff > 10

    private val horribleTexts = listOf(
        "GET OFF THE STAGE!",
        "YOU SUCK!",
        "BOOOO",
        "CAN'T WATCH THIS ANYMORE...",
    )

    private val losingTexts = listOf(
        "OH NO...",
        "YOU GOTTA DO BETTER THAN THIS!",
        "HAVING TROUBLE?",
        "GET GROOVING ALREADY!",
    )

    private val neutralTexts = listOf(
        "NOT BAD, NOT BAD",
        "YOU CAN DO IT!",
        "KEEP GOING!",
        "YOU ARE ROCKING",
    )

    private val winningTexts = listOf(
        "A NEW ROCK STAR IS BORN",
        "ROCK STAR IN THE MAKING",
        "UNBELIEVABLE",
        "ROCK ON!!!",
    )

    private val perfectTexts = listOf(
        "YOU ARE AH-MAH-ZING!",
        "THIS IS PERFECTION!",
        "YOU ARE SHREDDING THAT GUITAR!",
        "BEST, CONCERT, EVER!",
    )
}