package com.glycin.intelligame.stateinvaders

import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.stateinvaders.model.SpaceShip
import com.glycin.intelligame.stateinvaders.model.Stalien
import com.intellij.codeInsight.completion.AllClassesGetter
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import kotlinx.coroutines.CoroutineScope
import java.awt.KeyboardFocusManager
import kotlin.math.roundToInt

private const val FPS = 120L

@Service
class StateInvadersGame(private val scope: CoroutineScope) {

    lateinit var cm: CollisionManager
    lateinit var bm: BulletManager
    lateinit var sm: StalienManager
    lateinit var gameComponent: StateInvadersComponent

    fun initGame(project: Project, editor: Editor) {
        println("STATE INVADERS STARTED")

        val staliens = collectMutableFields(project).map { field ->
            Stalien(
                position = Vec2.zero,
                width = field.text.length * 8,
                height = editor.lineHeight,
                text = field.text,
                originalPsiField = field,
                game = this,
            )
        }.positionAliens(editor)

        bm = BulletManager(mutableListOf(), FPS)

        val spaceship = SpaceShip(
            position = Vec2(
                x = editor.component.width / 2,
                y = editor.component.height - 100
            ),
            width = 50,
            height = 50,
            mapMinX = 0,
            mapMaxX = editor.contentComponent.width - 50,
            game = this,
        )

        cm = CollisionManager(spaceship, staliens.toMutableList())
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(StateInvadersInput(spaceship, FPS))

        attachGameToEditor(editor, staliens, spaceship)
            .apply { start() }
    }

    private fun List<Stalien>.positionAliens(editor: Editor): List<Stalien> {
        val maxWidth = (editor.contentComponent.width * 0.5).roundToInt()
        val widthSpacing = 50
        var curWidth = 0
        var curHeight = 0
        forEach { alien ->
            alien.position = Vec2(curWidth, curHeight)
            if(curWidth + alien.width + widthSpacing > maxWidth) {
                curWidth = 0
                curHeight += (editor.lineHeight * 1.5).roundToInt()
            }else{
                curWidth += (alien.width + widthSpacing)
            }
        }

        return this
    }

    private fun collectMutableFields(project: Project) : List<PsiField> {
        val allFields = mutableListOf<PsiField>()

        val processor = Processor<PsiClass> { psiClass ->
            psiClass.fields.forEach { field ->
                if(!field.hasModifierProperty(PsiModifier.FINAL)) {
                    allFields.add(field)
                }
            }
            true
        }

        AllClassesGetter.processJavaClasses(
            PlainPrefixMatcher(""),
            project,
            GlobalSearchScope.projectScope(project),
            processor
        )

        return allFields
    }

    private fun attachGameToEditor(
        editor: Editor, aliens: List<Stalien>, spaceShip: SpaceShip
    ): StateInvadersComponent {
        val contentComponent = editor.contentComponent

        sm = StalienManager(aliens.toMutableList(), 0, editor.contentComponent.width, spaceShip, FPS)
        // Create and configure the Pong game component
        gameComponent = StateInvadersComponent(spaceShip, this, scope, FPS).apply {
            bounds = contentComponent.bounds
            isOpaque = false
        }

        // Add the Pong game component as an overlay
        contentComponent.add(gameComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        // Request focus for the Pong game to ensure it receives key events
        gameComponent.requestFocusInWindow()
        return gameComponent
    }

    fun gameOver() {
        println("game over!")
    }

    fun destroyStalien(collided: Stalien) {
        gameComponent.removeStalien(collided)
        sm.destroyAlien(collided)
    }
}