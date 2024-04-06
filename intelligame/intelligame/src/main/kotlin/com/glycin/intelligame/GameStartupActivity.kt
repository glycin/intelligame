package com.glycin.intelligame

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class GameStartupActivity: StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        setupGame(project)
    }

    private fun setupGame(project: Project){
        println("Game started!")
    }
}