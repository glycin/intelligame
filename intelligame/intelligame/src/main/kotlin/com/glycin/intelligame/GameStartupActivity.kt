package com.glycin.intelligame

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class GameStartupActivity: StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        println("Project started")
    }
}