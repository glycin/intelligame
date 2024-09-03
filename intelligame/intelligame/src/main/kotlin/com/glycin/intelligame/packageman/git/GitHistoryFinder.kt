package com.glycin.intelligame.packageman.git

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.history.VcsFileRevision
import com.intellij.vcsUtil.VcsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets

class GitHistoryFinder(
    private val project: Project,
    private val scope: CoroutineScope,
) {

    fun getDependencyCommits(depStrings: List<String>): List<GitHistoryDependency> {
        println("Searching for ${depStrings.size} dependency revisions")
        val revisions = runBlocking {
            scope.async(Dispatchers.IO) {
                getFileHistory()
            }.await()
        }
        println("Found ${revisions.size} revisions")
        val dependencies = mutableMapOf<String, GitHistoryDependency>()
        val sortedRevs = revisions.sortedBy { it.revisionDate }
        val firstRev = sortedRevs.first()

        depStrings.filter { it.startsWith("java") || it.startsWith("jdk") }.forEach {
            dependencies.putIfAbsent(it, GitHistoryDependency(
                author = firstRev.author ?: "Mystery Author",
                commitHash = firstRev.revisionNumber.asString(),
                commitMessage = firstRev.commitMessage ?: "No Message",
                commitDate = firstRev.revisionDate,
                dependencyString = it
            ))
        }
        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            depStrings.filter { !it.startsWith("java") && !it.startsWith("jdk") }.forEachIndexed { index, dep ->
                ProgressManager.getInstance().progressIndicator.text = "Processing $dep"
                ProgressManager.getInstance().progressIndicator.fraction = index / depStrings.size.toDouble()
                sortedRevs.forEach { rev ->
                    rev.loadContent()?.let {
                        val content = String(it, StandardCharsets.UTF_8)
                        if(content.contains(dep)) {
                            dependencies.putIfAbsent(dep, GitHistoryDependency(
                                author = rev.author ?: "Mystery Author",
                                commitHash = rev.revisionNumber.asString(),
                                commitMessage = rev.commitMessage ?: "No Message",
                                commitDate = rev.revisionDate,
                                dependencyString = dep,
                            )
                            )
                        }
                    }
                }
            }
        }, "Collecting Git History", true, project)

        //dependencies.forEach { dep -> println(dep) }
        return dependencies.values.toList()
    }


    private fun getFileHistory(): List<VcsFileRevision> {
        val files = FileGetter.getFiles(project)
        val revisions = mutableListOf<VcsFileRevision>()
        files.forEach { file ->
            val pvcsm = ProjectLevelVcsManager.getInstance(project)
            pvcsm.getVcsFor(file)?.let { vcs ->
                vcs.vcsHistoryProvider?.let { hp ->
                    hp.createSessionFor(VcsUtil.getFilePath(file))?.let { session ->
                        session.revisionList.forEach { rev ->
                            revisions.add(rev)
                        }
                    }
                }
            }
        }
        return revisions
    }
}