package io.github.kmp.gitstats.model.activity

import io.github.kmp.gitstats.model.TimeRange

import kotlinx.serialization.Serializable

@Serializable
data class FileChangesTimeRangeStat(
    val timeRange: TimeRange, val fileChangesStats: List<FileChangesStat>
)

@Serializable
data class FileChangesStat(
    val filePath: String, val count: Int
)
