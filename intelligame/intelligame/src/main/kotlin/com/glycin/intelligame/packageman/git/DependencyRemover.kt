package com.glycin.intelligame.packageman.git

import com.glycin.intelligame.shared.TextWriter
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil

object DependencyRemover {

    fun removeDependency(project: Project, dependency: String) {
        val files = FileGetter.getFiles(project)
        files.forEach { file ->
            val lines = VfsUtil.loadText(file).lines()
            val filtered = lines.filterNot { it.contains(dependency) }.joinToString("\n")
            TextWriter.writeText(filtered, file, project)
        }
    }
}