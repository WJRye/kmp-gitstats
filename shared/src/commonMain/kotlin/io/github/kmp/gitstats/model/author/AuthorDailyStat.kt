package io.github.kmp.gitstats.model.author

import io.github.kmp.gitstats.DateUtil.Companion.localDateTime
import io.github.kmp.gitstats.model.serializer.LocalDateRange
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDailyStat(
    val avatar: String, val author: String, val dailyContributions: List<DailyContribution>
)

@Serializable
data class DailyContribution(
    val date: LocalDate, val commitCount: Int
)


@Serializable
data class AuthorContributionInfo(
    val range: LocalDateRange = LocalDateRange(
        localDateTime().date, localDateTime().date
    ),
    val yearlyRanges: List<LocalDateRange> = emptyList(),
    val dateRangeStats: List<DateRangeStat> = emptyList()
)

@Serializable
data class DateRangeStat(val dateRange: LocalDateRange, val authorDailyStats: List<AuthorDailyStat>)