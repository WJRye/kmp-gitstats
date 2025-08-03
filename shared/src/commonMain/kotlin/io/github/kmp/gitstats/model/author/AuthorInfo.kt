package io.github.kmp.gitstats.model.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorInfo(
    val authorStats: List<AuthorStat> = emptyList(),
    val authorContributionInfo: AuthorContributionInfo = AuthorContributionInfo()
)