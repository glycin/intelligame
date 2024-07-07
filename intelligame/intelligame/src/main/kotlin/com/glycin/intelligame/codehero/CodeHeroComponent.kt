package com.glycin.intelligame.codehero

import com.glycin.intelligame.boom.BoomComponent
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextPane
import kotlin.math.roundToInt

class CodeHeroComponent(
    private val noteManager: NoteManager,
    private val scope: CoroutineScope,
    private val chState: CodeHeroState,
    fps: Long
) : JComponent() {

    private val animationLoader = AnimationLoader()
    private val deltaTime = fps.toLongDeltaTime()
    private val rockerGif = ImageIcon(BoomComponent::class.java.getResource("/Sprites/guitar.gif"))
    private val effects = mutableListOf<AnimatedEffect>()
    private lateinit var rockerLabel: JLabel
    private lateinit var scoreLabel: JLabel
    private lateinit var textLabel: JLabel
    private lateinit var previewPane: JTextPane
    private var centerX = 0
    private var centerY = 0

    fun start() {
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun destroy() {
        remove(rockerLabel)
        remove(scoreLabel)
        remove(textLabel)
        effects.clear()
    }

    fun focus() {
        centerX = width / 2
        centerY = (height / 1.5).roundToInt()
        showRocker()
        showScoreLabel()
        showTextLabel()
    }


    fun showSucces() {
        effects.add(AnimatedEffect(
            position = Vec2(centerX, centerY - (animationLoader.successSprites[0].getHeight(this)) - 25),
            sprites = animationLoader.successSprites
        ))
        updateScoreLabel()
    }

    fun showEpicFail() {
        effects.add(AnimatedEffect(
            position = Vec2(centerX, centerY),
            sprites = animationLoader.failSprites
        ))
        updateScoreLabel()
    }


    fun showPastePreview(textToPaste: String, position: Vec2, width: Int, height: Int, fontName: String, game: CodeHeroGame) {
        previewPane = TextIndicatorComponent(position, width, height, fontName, textToPaste, game)
        add(previewPane)
        repaint()
    }


    fun updatePastePreview(inputString: String) {
        val text = previewPane.text
        previewPane.text = text.drop(inputString.length)
        if(text.isEmpty()) {
            remove(previewPane)
        }else {
            previewPane.text = text.toString()
            previewPane.setBounds(previewPane.x, previewPane.y, previewPane.width, previewPane.height)
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            drawMusicalLines(g)
            drawNotes(g)
            drawEffects(g)
        }
    }

    private fun drawMusicalLines(g: Graphics2D) {
        g.color = JBColor.WHITE.brighter()
        g.fillRect(0, centerY + 25, width, 5)
        g.fillRect(0, centerY + 50, width, 5)
        g.fillRect(0, centerY + 75, width, 5)

        // width = 60

        g.color = JBColor.blue.darker()
        g.fillRect(centerX, centerY, 60, 100)
        g.color = JBColor.WHITE.brighter().brighter().brighter()
        g.fillRect(centerX + 5, centerY + 5, 50, 90)
    }

    private fun drawNotes(g: Graphics2D) {
        noteManager.notes.values
            .forEach {
                it.move()
                it.draw(g)
            }
    }

    private fun showRocker() {
        val x = width - (width / 4)
        val y =  (height / 4) + 120
        rockerLabel = JLabel(rockerGif)
        rockerLabel.setBounds(x, y, rockerGif.iconWidth, rockerGif.iconHeight)
        add(rockerLabel)
        repaint()
    }

    private fun showScoreLabel() {
        val x = width - (width / 4)
        val y = height / 4
        scoreLabel = JLabel("${chState.score} (x${chState.multiplier})")
        scoreLabel.setBounds(x, y, 500, 50)
        scoreLabel.font = Font(JBFont.SANS_SERIF, JBFont.BOLD, 48)
        scoreLabel.foreground = JBColor.YELLOW.brighter()
        add(scoreLabel)
        repaint()
    }

    private fun showTextLabel() {
        val x = width - (width / 4)
        val y = (height / 4) + 60
        textLabel = JLabel(chState.getMotivationalText())
        textLabel.setBounds(x, y, 500, 50)
        textLabel.font = Font(JBFont.SANS_SERIF, JBFont.BOLD, 24)
        textLabel.foreground = JBColor.CYAN.brighter()
        add(textLabel)
        repaint()
    }

    private fun drawEffects(g: Graphics2D) {
        effects.forEach { it.draw(g) }
        val toRemove = mutableListOf<AnimatedEffect>()
        effects.filter{ it.shown }.onEach { toRemove.add(it) }
        toRemove.forEach { effects.remove(it) }
    }


    private fun updateScoreLabel() {
        scoreLabel.text = "${chState.score} (x${chState.multiplier})"
        textLabel.text = chState.getMotivationalText()
    }
}