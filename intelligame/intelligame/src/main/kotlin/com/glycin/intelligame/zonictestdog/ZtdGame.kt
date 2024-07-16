package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.zonictestdog.level.Tile
import com.glycin.intelligame.zonictestdog.testretrieval.TestRetriever
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager

private const val FPS = 120L

class ZtdGame(
    private val editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    lateinit var zonic: Zonic
    var currentTiles = mutableListOf<Tile>()
    var state = ZtdGameState.STARTED
    private lateinit var ztdInput: ZtdInput
    private val mapCreator = MapCreator(editor)

    fun initGame(){
        val retriever = TestRetriever(project).getAllTestMethods()
        currentTiles = mapCreator.create()
        attachComponent()
        val cm = CollisionsManager(this)
        val po = PortalOpener(editor, project)
        zonic = Zonic(Fec2(100f, 100f), 50, 50, cm, po, scope, FPS)
        ztdInput = ZtdInput(zonic, project, this)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ztdInput)
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