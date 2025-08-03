package io.github.kmp.gitstats.shared.model.activity

import kotlinx.serialization.Serializable

@Serializable
data class ActivityInfo(
    val linesChangedStats: List<LinesChangedStat> = emptyList(),
    val commitsChangedStats: List<CommitsChangedStat> = emptyList(),
    val codingTimeRangeStats: List<CodingTimeRangeStat> = emptyList(),
    val filesChangedTimeRangeStats: List<FileChangesTimeRangeStat> = emptyList()
)