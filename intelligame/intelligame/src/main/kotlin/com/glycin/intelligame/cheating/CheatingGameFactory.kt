package com.glycin.intelligame.cheating

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.BorderLayout
import javax.swing.JPanel

class CheatingGameFactory: ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (!JBCefApp.isSupported()) {
            throw IllegalStateException("JCEF is not supported on this platform.")
        }

        val browser = JBCefBrowser("https://web.archive.org/web/20230804133455/https://springone.io/history-of-spring")

        val panel = JPanel().apply {
            layout = BorderLayout()
            add(browser.component, BorderLayout.CENTER)
        }

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)

        toolWindow.contentManager.addContent(content)
    }
}