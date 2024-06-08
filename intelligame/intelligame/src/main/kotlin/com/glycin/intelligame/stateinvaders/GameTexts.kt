package com.glycin.intelligame.stateinvaders

object GameTexts {

    val banner = """        
           _____ _______    _______ ______   _____ _   ___      __     _____  ______ _____   _____ 
          / ____|__   __|/\|__   __|  ____| |_   _| \ | \ \    / /\   |  __ \|  ____|  __ \ / ____|
         | (___    | |  /  \  | |  | |__      | | |  \| |\ \  / /  \  | |  | | |__  | |__) | (___  
          \___ \   | | / /\ \ | |  |  __|     | | | . ` | \ \/ / /\ \ | |  | |  __| |  _  / \___ \ 
          ____) |  | |/ ____ \| |  | |____   _| |_| |\  |  \  / ____ \| |__| | |____| | \ \ ____) |
         |_____/   |_/_/    \_\_|  |______| |_____|_| \_|   \/_/    \_\_____/|______|_|  \_\_____/ 
    """.trimIndent()

    val mainMenuMsg = "Type 'start' to start the game!"

    val openingCutsceneTexts = arrayOf(
        "The year is 2101",
        "War was beginning",
        "Captain: WHAT HAPPEN?",
        "Mechanic: SOMEBODY SET UP US THE BOMB.",
        "Mechanic: We get signal...",
        "Captain: WHAT!",
        "Operator: Main screen turn on.",
        "Captain: It's you!",
        "Evil Genius: HOW ARE YOU GENTLEMEN!!!",
        "Evil Genius: ALL YOUR STATE ARE BELONG TO US",
        "Evil Genius: YOU ARE ON THE WAY TO BUGSTRUCTION",
        "Captain: What you say?!",
        "Evil Genius: YOU HAVE NO CHANCE TO FIX MAKE YOUR TIME!",
        "Evil Genius: HA HA HA",
    )

    fun getScoreText(score: Int): String {
        return "${"\t"}${"\t"}${"\t"}${"\t"}${"\t"} Chance of production crashing is $score %"
    }

    fun getCutscenePlaceholder(): String {
        val sb = StringBuilder()
        for(i in 0..35 ) {
            sb.append("\n")
        }
        return sb.toString()
    }
}