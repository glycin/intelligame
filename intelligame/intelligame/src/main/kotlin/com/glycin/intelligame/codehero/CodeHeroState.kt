package com.glycin.intelligame.codehero

import kotlin.math.absoluteValue

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
        val diff = hit - missed
        if(diff > -5 && diff < 5) {
            return neutralTexts.random()
        }else if(diff >= 5 && hit > 5 && (hit - diff) > 10) {
            return winningTexts.random()
        }else if((hit - diff) <= 10){
            return perfectTexts.random()
        }else if(diff <= -5 && missed > 5 && (missed - diff.absoluteValue) > 10) {
            return losingTexts.random()
        }else if((missed - diff.absoluteValue) <= 10) {
            return horribleTexts.random()
        }

        return neutralTexts.random()
    }

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