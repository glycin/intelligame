package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.Fec2
import com.glycin.intelligame.zonictestdog.level.Coin
import com.glycin.intelligame.zonictestdog.level.Portal
import com.glycin.intelligame.zonictestdog.level.Tile
import com.glycin.intelligame.zonictestdog.level.WalkingEnemy
import com.glycin.intelligame.zonictestdog.testretrieval.TestRetriever
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
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
    val currentCoins = mutableListOf<Coin>()
    val currentEnemies = mutableListOf<WalkingEnemy>()

    private lateinit var ztdInput: ZtdInput
    private lateinit var component: ZtdComponent
    private lateinit var mapCreator: MapCreator
    private lateinit var enemyManager: EnemyManager

    private val testMap = mutableMapOf<String, List<PsiMethod>>()

    fun initGame(){
        val testMehods = TestRetriever(project).getAllTestMethods()
        val javaFiles = getJavaFileCount()
        val chunkedMethods = if(testMehods.size <= javaFiles.size)
            testMehods.chunked(testMehods.size)
        else
            testMehods.chunked(testMehods.size / javaFiles.size)

        javaFiles.forEachIndexed { index, file ->
            testMap.putIfAbsent(file.name, chunkedMethods[index])
        }

        mapCreator = MapCreator(testMap, testMehods.size)
        val (tiles, coins, enemies) = mapCreator.create(editor, editor.virtualFile.name)
        currentTiles.addAll(tiles)
        currentCoins.addAll(coins)
        currentEnemies.addAll(enemies)
        attachComponent()
        val cm = CollisionsManager(this)
        val po = PortalOpener(project, this)
        enemyManager = EnemyManager(this, cm, scope, FPS)
        zonic = Zonic(Fec2(100f, 100f), 50, 50, editor.contentComponent.height, cm, po, this, scope, FPS)
        ztdInput = ZtdInput(zonic, project, this)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ztdInput)
    }

    fun stopGame(){

    }

    fun deleteTests(pickedUpCoins: MutableList<Coin>) {
        val coins = pickedUpCoins.intersect(currentCoins.toSet())
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                coins.forEach { coin -> coin.method.delete() }
            }
        }
    }

    fun travelTo(portal: Portal){
        val em = FileEditorManager.getInstance(project)
        ApplicationManager.getApplication().invokeLater {
            em.openFile(portal.file.virtualFile, true).firstOrNull()?.let { fileEditor ->
                if(fileEditor is TextEditor) {
                    val newEditor = fileEditor.editor
                    PsiDocumentManager.getInstance(project).getDocument(portal.file)?.let {
                        if(editor.virtualFile != newEditor.virtualFile) {
                            val oldEditor = editor
                            em.closeFile(oldEditor.virtualFile)
                            oldEditor.contentComponent.remove(component)
                            oldEditor.scrollingModel.scrollVertically(0)
                            ApplicationManager.getApplication().invokeLater {
                                this.editor = newEditor
                                val offset = portal.textRange.startOffset + portal.element.textRange.startOffset
                                newEditor.caretModel.moveToOffset(offset)
                                newEditor.scrollingModel.scrollToCaret(ScrollType.CENTER)
                                reInitLevel(newEditor.offsetToXY(offset), newEditor, portal.file.name)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun reInitLevel(point: Point, newEditor: Editor, fileName: String) {
        currentTiles.clear()
        currentCoins.clear()
        currentEnemies.clear()
        val (tiles, coins, enemies) = mapCreator.create(newEditor, fileName)
        currentTiles.addAll(tiles)
        currentCoins.addAll(coins)
        currentEnemies.addAll(enemies)
        component.removePortalLabels()
        portals.clear()
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

    private fun getJavaFileCount(): List<VirtualFile> {
        val javaFileType = FileTypeManager.getInstance().getFileTypeByExtension("java")
        val scope = GlobalSearchScope.projectScope(project).intersectWith(GlobalSearchScope.projectScope(project))
        return FileTypeIndex.getFiles(javaFileType, scope).filterNot { file ->
            file.path.contains("/test/") ||
                    file.path.contains("\\test\\") ||
                    file.path.contains("/Test/") ||
                    file.path.contains("\\Test\\")
        }
    }
}