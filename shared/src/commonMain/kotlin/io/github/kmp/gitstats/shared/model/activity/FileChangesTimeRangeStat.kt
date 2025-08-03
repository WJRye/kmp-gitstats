package io.github.kmp.gitstats.shared.model.activity

import io.github.kmp.gitstats.shared.model.TimeRange

import kotlinx.serialization.Serializable

@Serializable
data class FileChangesTimeRangeStat(
    val timeRange: TimeRange, val fileChangesStats: List<FileChangesStat>
)

@Serializable
data class FileChangesStat(
    val filePath: String, val count: Int
)
