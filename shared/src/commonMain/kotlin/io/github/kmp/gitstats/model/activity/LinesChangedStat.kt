package io.github.kmp.gitstats.model.activity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class LinesChangedStat(
    val date: LocalDate, val averageLines: Long, val medianLines: Long, val userLines: Long
)