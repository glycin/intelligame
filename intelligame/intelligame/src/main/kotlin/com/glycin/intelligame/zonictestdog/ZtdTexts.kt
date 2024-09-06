package com.glycin.intelligame.zonictestdog

object ZtdTexts {
    val zonicBanner = """
        ███████╗ ██████╗ ███╗   ██╗██╗ ██████╗
        ╚══███╔╝██╔═══██╗████╗  ██║██║██╔════╝
          ███╔╝ ██║   ██║██╔██╗ ██║██║██║     
         ███╔╝  ██║   ██║██║╚██╗██║██║██║     
        ███████╗╚██████╔╝██║ ╚████║██║╚██████╗
        ╚══════╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝ ╚═════╝
        
  ________            ______          __      __           
 /_  __/ /_  ___     /_  __/__  _____/ /_____/ /___  ____ _
  / / / __ \/ _ \     / / / _ \/ ___/ __/ __  / __ \/ __ `/
 / / / / / /  __/    / / /  __(__  ) /_/ /_/ / /_/ / /_/ / 
/_/ /_/ /_/\___/    /_/  \___/____/\__/\__,_/\____/\__, /  
                                                  /____/  
                                                              
          Type "we love scrum..." to start
    """.trimIndent()

    val cutsceneTexts = listOf(
        "Zonic: Who are you?! What have you done with all my tests?!",
        "Dr. Velocitnik: I am Dr. Velocitnik, crusher of dev teams!",
        "Dr. Velocitnik: Your dream of ever making the next sprint goals is over!",
        "Dr. Velocitnik: I have stolen your unit tests and scattered them throughout your code base, muhahaha",
        "Zonic: Oh no!",
        "Zonic: Without my unit tests, how can I ever ship my changes reliably! My estimations are going to be off!",
        "Dr. Velocitnik: Muhahahaha!!!",
        "Dr. Velocitnik: But like every evil genius, I will give you a chance to completely ruin my plans!"
        )

    fun getFinalText(bossFileName: String) = "Dr. Velocitnik: Move through your code base, and come find me in $bossFileName. There I will crush you once and for all!"
}