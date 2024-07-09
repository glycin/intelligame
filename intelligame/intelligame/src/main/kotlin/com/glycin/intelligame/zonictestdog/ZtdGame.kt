package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.zonictestdog.testretrieval.TestRetriever
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

private const val FPS = 120L

class ZtdGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    fun initGame(){
        val retriever = TestRetriever(project).getAllTestMethods()
        println(retriever.size)
        attachComponent()
    }

    fun stopGame(){

    }

    private fun attachComponent() : ZtdComponent {
        val contentComponent = editor.contentComponent
        val component = ZtdComponent(this, scope, FPS).apply {
            bounds = editor.contentComponent.bounds
            isOpaque = false
        }.apply { start() }

        contentComponent.add(component)
        contentComponent.revalidate()
        contentComponent.repaint()
        component.requestFocusInWindow()
        return component
    }
}