package io.github.kmp.gitstats.shared.model.tag

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TagInfo(
    val stats: List<TagStat> = emptyList()
)

@Serializable
data class TagStat(
    val name: String,
    val type: String,
    val sha: String,
    val tagger: String,
    val date: LocalDateTime,
    val message: String,
)
