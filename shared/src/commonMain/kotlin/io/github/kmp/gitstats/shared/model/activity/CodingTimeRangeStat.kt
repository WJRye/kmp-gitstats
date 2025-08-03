package io.github.kmp.gitstats.shared.model.activity

import io.github.kmp.gitstats.shared.model.TimeRange

import kotlinx.serialization.Serializable

@Serializable
data class CodingTimeRangeStat(
    val timeRange: TimeRange, val codingTimeStats: List<CodingTimeStat>
)

@Serializable
data class CodingTimeStat(
    val dayOfWeek: Int, // 1 = Monday, ..., 7 = Sunday
    val hour: Int,       // 0~23
    val commitCount: Int
)
