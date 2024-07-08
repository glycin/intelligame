package com.glycin.intelligame.codehero

object IntroStrings {
    val introStrings = arrayOf(
        "PASTE ACTION DETECTED!",
        "TIME TO PROVE IF YOU ARE A REAL CODE ROCKSTAR!",
        "READY?!",
        "3",
        "2",
        "1",
        "ROCK ON!"
    )

    fun difficultyString(size: Int): String {
        return if(size <= 30){
            "YOU PASTED A LOW AMOUNT OF TEXT, SO YOU ARE GETTING AN EASY SONG!"
        }else if (size <= 230) {
            "YOU PASTED A MEDIUM AMOUNT OF TEXT, SO YOU ARE GETTING A MEDIUM SONG!"
        }else {
            "YOU PASTED A SIGNIFICANT AMOUNT OF TEXT, GOOD LUCK!"
        }
    }
}