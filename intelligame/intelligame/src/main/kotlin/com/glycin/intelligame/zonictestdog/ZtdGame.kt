package com.glycin.intelligame.zonictestdog

import com.glycin.intelligame.shared.TextWriter
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.zonictestdog.level.Coin
import com.glycin.intelligame.zonictestdog.level.Portal
import com.glycin.intelligame.zonictestdog.level.Tile
import com.glycin.intelligame.zonictestdog.level.WalkingEnemy
import com.glycin.intelligame.zonictestdog.testretrieval.TestRetriever
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.dom.document
import java.awt.KeyboardFocusManager
import java.awt.Point

private const val FPS = 120L

class ZtdGame(
    var editor: Editor,
    private val project: Project,
    private val scope: CoroutineScope,
){
    lateinit var zonic: Zonic
    var velocitnik: Velocitnik? = null
    var state = ZtdGameState.MAIN_MENU
    val portals = mutableListOf<Portal>()
    val currentTiles = mutableListOf<Tile>()
    val currentCoins = mutableListOf<Coin>()
    val currentEnemies = mutableListOf<WalkingEnemy>()

    private lateinit var ztdInput: ZtdInput
    private lateinit var component: ZtdComponent
    private lateinit var mapCreator: MapCreator
    private lateinit var enemyManager: EnemyManager
    private lateinit var bossFile: Pair<String, VirtualFile>

    private val testMap = mutableMapOf<String, List<PsiMethod>>()
    private var startFileContent = ""
    private var mainMenuString = ""
    private val mainMenuInput: ZonicMainMenuInput = ZonicMainMenuInput(this)

    fun initGame(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(mainMenuInput)
        startFileContent = editor.document.text
        TextWriter.replaceText(0, editor.document.textLength, ZtdTexts.zonicBanner, editor, project)
    }

    private fun startGame() {
        state = ZtdGameState.STARTED
        val testMehods = TestRetriever(project).getAllTestMethods()
        val javaFiles = getJavaFileCount()

        if(testMehods.size <= javaFiles.size){
            testMehods.forEachIndexed{ index, method ->
                testMap.putIfAbsent(javaFiles[index].name, listOf(method))
            }
        } else {
            val chunks = testMehods.chunked(testMehods.size / javaFiles.size)
            javaFiles.forEachIndexed { index, file ->
                testMap.putIfAbsent(file.name, chunks[index])
            }
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
        zonic = Zonic(Vec2(100f, 100f), 50, 50, editor.contentComponent.height, cm, po, this, scope, FPS)
        ztdInput = ZtdInput(zonic, project, this)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ztdInput)
    }

    fun stopGame(){
        component.removePortalLabels()
        editor.contentComponent.remove(component)
        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()
        state = ZtdGameState.GAME_OVER
        currentTiles.clear()
        currentCoins.clear()
        currentEnemies.clear()
        portals.clear()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(ztdInput)
    }

    fun resetZonic() {
        zonic.position = Vec2(100f, 100f)
    }

    fun skipToBoss() {
        val em = FileEditorManager.getInstance(project)
        val pFile = PsiManager.getInstance(project).findFile(bossFile.second)
        component.stop()
        ApplicationManager.getApplication().invokeLater {
            em.openFile(bossFile.second, true).firstOrNull()?.let { fileEditor ->
                if(fileEditor is TextEditor) {
                    val newEditor = fileEditor.editor
                    pFile?.let { psiFile ->
                        PsiDocumentManager.getInstance(project).getDocument(psiFile)?.let {
                            if (editor.virtualFile != newEditor.virtualFile) {
                                val oldEditor = editor
                                em.closeFile(oldEditor.virtualFile)
                                oldEditor.contentComponent.remove(component)
                                oldEditor.scrollingModel.scrollVertically(0)
                                ApplicationManager.getApplication().invokeLater {
                                    this.editor = newEditor
                                    newEditor.caretModel.moveToOffset(0)
                                    newEditor.scrollingModel.scrollToCaret(ScrollType.CENTER)
                                    portals.forEach { it.close() }
                                    reInitLevel(newEditor.offsetToXY(0), newEditor, bossFile.first)
                                }
                            }
                        }
                    }
                }
            }
        }
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
        component.stop()
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
                                portals.forEach { it.close() }
                                reInitLevel(newEditor.offsetToXY(offset), newEditor, portal.file.name)
                            }
                        }
                    }
                }
            }
        }
    }

    fun mainMenuTyped(c: Char) {
        mainMenuString += c
        if(mainMenuString.contains("we love scrum...")){
            showCutscene()
        }
    }

    private fun showCutscene() {
        TextWriter.deleteText(0, editor.document.textLength, editor, project)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(mainMenuInput)
        mainMenuString = ""
        val javaFiles = getJavaFileCount()
        placeBoss(javaFiles)

        val cutsceneComponent = ZtdCutsceneComponent(scope, FPS).apply {
            bounds = editor.contentComponent.bounds
            isOpaque = false
        }.apply { start() }

        editor.contentComponent.add(cutsceneComponent)
        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()
        cutsceneComponent.requestFocusInWindow()

        scope.launch(Dispatchers.EDT) {
            ZtdTexts.cutsceneTexts.forEachIndexed { index, s ->
                if(index == 0) {
                    TextWriter.writeText(0, s, editor, project)
                }else {
                    TextWriter.replaceText(0, ZtdTexts.cutsceneTexts[index - 1].length, s, editor, project)
                }
                delay(5000L)
            }

            TextWriter.replaceText(0, ZtdTexts.cutsceneTexts.last().length, ZtdTexts.getFinalText(bossFile.first), editor, project)
            delay(10000L)
            cutsceneComponent.stop()
            editor.contentComponent.remove(cutsceneComponent)
            delay(1000L)
            TextWriter.replaceText(0, editor.document.textLength, startFileContent, editor, project)
            delay(5000L)
            startGame()
        }
    }

    private fun placeBoss(javaFiles: List<VirtualFile>) {
        FileEditorManager.getInstance(project).openFiles
            .firstOrNull()
            ?.let { file ->
                bossFile = javaFiles
                    .map { it.name to it }
                    .filter { it.first != file.name }
                    .random()
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
        if(fileName == bossFile.first){
            velocitnik = Velocitnik(Vec2(450f, 250f), 400, 400, zonic, scope, FPS)
        }
        zonic.position = Vec2(point.x.toFloat(), point.y.toFloat() - zonic.height)
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
        val scope = GlobalSearchScope.projectScope(project)
        return FileTypeIndex.getFiles(javaFileType, scope).filterNot { file ->
            file.path.contains("/test/") ||
                    file.path.contains("\\test\\") ||
                    file.path.contains("/Test/") ||
                    file.path.contains("\\Test\\")
        }
    }
}