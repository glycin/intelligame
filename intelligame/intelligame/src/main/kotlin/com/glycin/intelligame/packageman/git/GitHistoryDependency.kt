package com.glycin.intelligame.packageman.git

import java.util.Date

data class GitHistoryDependency(
    val author: String,
    val commitHash: String,
    val commitMessage: String,
    val commitDate: Date,
    val dependencyString: String,
)