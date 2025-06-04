package com.glycin.intelligame.starwars

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.*
import javax.swing.*

class StarWarsScrollFactory: ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (!JBCefApp.isSupported()) {
            throw IllegalStateException("JCEF is not supported on this platform.")
        }

        val htmlContent = this::class.java.getResource("/starwars/text.html")?.readText() ?: ""

        val browser = JBCefBrowser().apply {
            loadHTML(htmlContent)
        }

        val panel = JPanel().apply {
            layout = BorderLayout()
            add(browser.component, BorderLayout.CENTER)
        }

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)

        toolWindow.contentManager.addContent(content)
    }
}