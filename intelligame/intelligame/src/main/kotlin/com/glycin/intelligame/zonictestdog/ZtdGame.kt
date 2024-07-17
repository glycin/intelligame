package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.zonictestdog.level.Portal
import com.glycin.intelligame.zonictestdog.level.Tile
import com.glycin.intelligame.zonictestdog.testretrieval.TestRetriever
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager
import java.awt.Point

private const val FPS = 120L

class ZtdGame(
    var editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    lateinit var zonic: Zonic
    var state = ZtdGameState.STARTED

    val portals = mutableListOf<Portal>()
    val currentTiles = mutableListOf<Tile>()

    private lateinit var ztdInput: ZtdInput
    private lateinit var component: ZtdComponent
    private val mapCreator = MapCreator()

    fun initGame(){
        val retriever = TestRetriever(project).getAllTestMethods()
        currentTiles.addAll(mapCreator.create(editor))
        attachComponent()
        val cm = CollisionsManager(this)
        val po = PortalOpener(project, cm, this)
        zonic = Zonic(Fec2(100f, 100f), 50, 50, cm, po, scope, FPS)
        ztdInput = ZtdInput(zonic, project, this)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ztdInput)
    }

    fun stopGame(){

    }

    fun travelTo(portal: Portal){
        val em = FileEditorManager.getInstance(project)
        ApplicationManager.getApplication().invokeLater {
            em.openFile(portal.file.virtualFile, true).firstOrNull()?.let { fileEditor ->
                PsiDocumentManager.getInstance(project).getDocument(portal.file)?.let {
                    em.closeFile(editor.virtualFile)
                    editor = (fileEditor as TextEditor).editor
                    val offset = portal.textRange.startOffset + portal.element.textRange.startOffset
                    editor.caretModel.moveToOffset(offset)
                    editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
                    reInitLevel(editor.offsetToXY(offset), editor)
                }
            }
        }
    }

    private fun reInitLevel(point: Point, newEditor: Editor) {
        currentTiles.clear()
        currentTiles.addAll(mapCreator.create(newEditor))
        portals.clear()
        newEditor.contentComponent.remove(component)
        attachComponent()
        zonic.position = Fec2(point.x.toFloat(), point.y.toFloat() - zonic.height)
    }

    private fun attachComponent() {
        val contentComponent = editor.contentComponent
        component = ZtdComponent(this, scope, FPS).apply {
            bounds = editor.contentComponent.bounds
            isOpaque = false
        }.apply { start() }

        contentComponent.add(component)
        contentComponent.revalidate()
        contentComponent.repaint()
        component.requestFocusInWindow()
    }
}