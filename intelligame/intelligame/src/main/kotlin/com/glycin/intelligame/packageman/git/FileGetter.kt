package com.glycin.intelligame.packageman.git

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile

object FileGetter {
    fun getFiles(project: Project) : List<VirtualFile> {
        val filePaths = mutableListOf<VirtualFile>()

        project.modules.forEach {
            it.rootManager.contentRoots.forEach { root ->
                val gradle = root.findChild("build.gradle.kts")
                if(gradle != null){
                    filePaths.add(gradle)
                }

                val maven = root.findChild("pom.xml")
                if(maven != null){
                    filePaths.add(maven)
                }

                val intellij = root.findChild("${project.name}.iml")
                if(intellij != null){
                    filePaths.add(intellij)
                }
            }
        }

        return filePaths
    }
}