package com.glycin.intelligame.packageman

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.impl.OrderEntryUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.jetbrains.rd.util.string.println
import kotlinx.coroutines.CoroutineScope
import java.awt.Point

class PackmanGame(
    val editor: Editor,
    val project: Project,
    val scope: CoroutineScope,
) {

    fun startGame(){
        collectDependencies()
        val maxX = editor.xyToLogicalPosition(Point(editor.contentComponent.width, 0)).column
        val maxY = editor.xyToLogicalPosition(Point(0, editor.contentComponent.height)).line
        println("$maxX, $maxY vs ${editor.contentComponent.width}, ${editor.contentComponent.height}")
        val generator = MazeGenerator(maxX, maxY)
        val maze = generator.generate()
        val sb = StringBuilder()
        maze.forEach {
            for (c in it) {
                sb.append(c)
            }
            sb.append('\n')
        }
        println(sb.toString())
    }

    private fun collectDependencies() : List<String> {
        val libStrings = mutableListOf<String>()
        println("collecting dependencies")

        project.modules.forEach { module ->
            OrderEntryUtil.getModuleLibraries(ModuleRootManager.getInstance(module)).forEach { lib ->
                /*lib.getUrls(OrderRootType.CLASSES).forEach {
                    println("Module search $it")
                    libStrings.add(it)
                }*/

                libStrings.add(lib.name ?: "mystery.library")
            }
        }

        LibraryTablesRegistrar.getInstance().getLibraryTable(project).libraries.forEach { lib ->
            /*lib.getUrls(OrderRootType.CLASSES).forEach {
                println("Project search $it")
                libStrings.add(it)
            }*/

            libStrings.add(lib.name ?: "mystery.library")
        }

        LibraryTablesRegistrar.getInstance().libraryTable.libraries.forEach { lib ->
            /*lib.getUrls(OrderRootType.CLASSES).forEach {
                println("Application search $it")
                libStrings.add(it)
            }*/

            libStrings.add(lib.name ?: "mystery.library")
        }


        OrderEnumerator.orderEntries(project).recursively().classesRoots.forEach { root ->
            libStrings.add(root.name)
        }


        println("collected dependencies")
        return libStrings
    }
}