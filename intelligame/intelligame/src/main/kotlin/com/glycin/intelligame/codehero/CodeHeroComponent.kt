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
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.*
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextPane
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument
import kotlin.math.roundToInt

class CodeHeroComponent(
    private val noteManager: NoteManager,
    private val scope: CoroutineScope,
    private val game: CodeHeroGame,
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
    private lateinit var previewPaneDoc: StyledDocument

    private val charsToShow = LinkedList<Char>()
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
        remove(previewPane)
        effects.clear()
    }

    fun focus() {
        centerX = width / 2
        centerY = (height / 1.5).roundToInt()

        showRocker()
        showScoreLabel()
        showTextLabel()
    }


    fun checkSuccess(charTyped: Char) {
        if(charTyped.isWhitespace() || charsToShow.peek() == charTyped) {
            if(charsToShow.isEmpty()) {
                game.paste()
                showSuccess()
                noteManager.deactivateFirst()
            }else {
                charsToShow.remove()
                while (charsToShow.isNotEmpty() && !charsToShow.peek().isLetterOrDigit()) {
                    charsToShow.remove()
                }
                updateLetterIndicator()
                showSuccess()
                noteManager.deactivateFirst()
            }
        }else {
            game.gameState.onFail()
            showEpicFail()
        }
    }

    fun showEpicFail() {
        effects.add(AnimatedEffect(
            position = Vec2(centerX.toFloat(), centerY.toFloat()),
            sprites = animationLoader.failSprites,
            frameHoldCount = 2,
        ))
        updateScoreLabel()
    }


    fun showLetterIndicators(textToPaste: String, paneWidth: Int, paneHeight: Int, fontName: String) {
        textToPaste.filter { it.isLetterOrDigit() }.forEach { charsToShow.add(it.uppercaseChar()) }

        previewPane = JTextPane().also { jtp ->
            jtp.setBounds(centerX, height / 2, paneWidth, paneHeight)
            jtp.isOpaque = false

            previewPaneDoc = jtp.styledDocument
            val firstStyle = jtp.addStyle("Main", null)
            StyleConstants.setForeground(firstStyle, JBColor.CYAN.brighter().brighter().brighter().brighter())
            StyleConstants.setBold(firstStyle, true)
            StyleConstants.setFontFamily(firstStyle, fontName)
            StyleConstants.setFontSize(firstStyle, 72)

            val secondStyle = jtp.addStyle("Backing", null)
            StyleConstants.setForeground(secondStyle, JBColor.white.brighter().brighter())
            StyleConstants.setBold(secondStyle, false)
            StyleConstants.setItalic(secondStyle, true)
            StyleConstants.setFontFamily(secondStyle, fontName)
            StyleConstants.setFontSize(secondStyle, 42)

            previewPaneDoc.insertString(0, charsToShow.peek().toString(), firstStyle)
            previewPaneDoc.insertString(1, "\t\t${charsToShow[1]}", secondStyle)
        }
        add(previewPane)
        repaint()
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

        g.color = Color(44, 255, 50, 125)
        g.drawRect(centerX - 150, centerY + 15, 375, 70)
        g.color = Color(44, 255, 0, 150)
        g.fillRect(centerX - 145, centerY + 20, 365, 60)
        g.color = JBColor.red.darker().darker()
        g.fillRect(centerX, centerY, 60, 100)
        g.color = JBColor.red.brighter().brighter()
        g.fillRect(centerX + 5, centerY + 5, 50, 90)
    }

    private fun drawNotes(g: Graphics2D) {
        noteManager.notes.values
            .forEach {
                it.draw(g)
            }
    }

    private fun showRocker() {
        val x = width - (width / 4)
        val y =  (height / 4) + 120
        rockerLabel = JLabel(rockerGif).also { jl ->
            jl.setBounds(x, y, rockerGif.iconWidth, rockerGif.iconHeight)
        }
        add(rockerLabel)
        repaint()
    }

    private fun showScoreLabel() {
        val x = width - (width / 4)
        val y = height / 4
        scoreLabel = JLabel("${game.gameState.score} (x${game.gameState.multiplier})").also { jl ->
            jl.setBounds(x, y, 500, 50)
            jl.font = Font(JBFont.SANS_SERIF, JBFont.BOLD, 48)
            jl.foreground = JBColor.YELLOW.brighter()
        }
        add(scoreLabel)
        repaint()
    }

    private fun showTextLabel() {
        val x = width - (width / 4)
        val y = (height / 4) + 60
        textLabel = JLabel(game.gameState.getMotivationalText()).also { jl ->
            jl.setBounds(x, y, 500, 50)
            jl.font = Font(JBFont.SANS_SERIF, JBFont.BOLD, 24)
            jl.foreground = JBColor.CYAN.brighter()
        }
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
        scoreLabel.text = "${game.gameState.score} (x${game.gameState.multiplier})"
        textLabel.text = game.gameState.getMotivationalText()
    }

    private fun updateLetterIndicator() {
        if(charsToShow.isEmpty()) {
            remove(previewPane)
        }else {
            previewPane.text = ""
            previewPaneDoc.insertString(0, charsToShow.peek().toString(), previewPaneDoc.getStyle("Main"))
            if(charsToShow.count() >= 2){
                previewPaneDoc.insertString(1, "\t\t${charsToShow[1]}", previewPaneDoc.getStyle("Backing"))
            }
        }
    }

    private fun showSuccess(){
        effects.add(AnimatedEffect(
            position = Vec2(centerX.toFloat(), centerY - (animationLoader.successSprites[0].getHeight(this)) - 25f),
            sprites = animationLoader.successSprites,
            frameHoldCount = 2,
        ))
        updateScoreLabel()
    }
}