package com.glycin.intelligame.packageman

import com.glycin.intelligame.packageman.git.DependencyRemover
import com.glycin.intelligame.shared.Vec2
import com.glycin.intelligame.util.toLongDeltaTime
import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class PackmanComponent(
    private val state: PackmanState,
    private val scope: CoroutineScope,
    private val project: Project,
    fps: Long
) : JComponent() {

    private val deltaTime = fps.toLongDeltaTime()

    fun start() {
        scope.launch (Dispatchers.EDT) {
            while(true) {
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun destroy() {
        state.ghosts.onEach { ghost -> ghost.kill() }
        state.player.kill()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            paintMaze(g)
            paintPlayer(g)
            paintGhost(g)
            checkCollision()
        }
    }

    private fun paintMaze(g: Graphics2D) {
        state.mazeCells.forEach { cell ->
            if(cell.isWall) {
                g.color = JBColor.WHITE.brighter().brighter()
                g.drawRect(cell.position.x, cell.position.y, cell.width, cell.height)
            }else {
                //g.fillRect(cell.position.x, cell.position.y, cell.width, cell.height)
            }
        }
    }

    private fun paintPlayer(g: Graphics2D) {
        state.player.move()
        state.player.render(g)
    }

    private fun paintGhost(g: Graphics2D) {
        state.ghosts.forEach {
            if(!it.labelAdded) {
                add(it.textPane)
                it.labelAdded = true
            }

            it.moveAndRender(g)
        }
    }

    private fun checkCollision() {
        val ghostsToRemove = mutableListOf<Ghost>()
        state.ghosts.forEach {
            if(Vec2.distance(it.position, state.player.position) < 10) {
                ghostsToRemove.add(it)
            }
        }
        ghostsToRemove.onEach {
            remove(it.textPane)
            it.kill()
            state.ghosts.remove(it)
            if(!it.gitDependency.dependencyString.startsWith("java") && !it.gitDependency.dependencyString.startsWith("jdk")) {
                DependencyRemover.removeDependency(project, it.gitDependency.dependencyString)
            }
        }
        ghostsToRemove.clear()
    }
}