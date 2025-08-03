package io.github.kmp.gitstats.model.activity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CommitsChangedStat(
    val date: LocalDate, val count: Int, val myCount: Int
)
