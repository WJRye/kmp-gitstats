package io.github.kmp.gitstats.result.author

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.kmp.gitstats.model.author.AuthorContributionInfo
import io.github.kmp.gitstats.model.author.DailyContribution
import io.github.kmp.gitstats.model.serializer.LocalDateRange
import io.github.kmp.gitstats.result.Selector
import io.github.kmp.gitstats.result.TopNInputBox
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import java.lang.Integer.min


private const val DEFAULT_TOP_NUM = "10"


@Composable
fun ContributionActivity(authorContributionInfo: AuthorContributionInfo) {
    var topNText by remember { mutableStateOf(DEFAULT_TOP_NUM) }
    val yearlyRanges = authorContributionInfo.yearlyRanges
    val authorStats = authorContributionInfo.dateRangeStats
    var selectedRange by remember(yearlyRanges) { mutableStateOf(yearlyRanges.first()) }
    val selectedStats = remember(selectedRange, topNText) {
        val selectedAuthorStats = authorStats.asSequence()
            .find { selectedRange.first == it.dateRange.first && selectedRange.last == it.dateRange.last }?.authorDailyStats
            ?: emptyList()
        selectedAuthorStats.take(min(topNText.toInt(), selectedAuthorStats.size))
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Contribution Activity", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "${authorContributionInfo.range.first} ~ ${authorContributionInfo.range.last}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Selector<LocalDateRange>(
                authorContributionInfo.yearlyRanges,
                labelProvider = { "${it.first} ~ ${it.last}" },
                onSelect = { entry ->
                    selectedRange = entry
                })

            TopNInputBox(topNText, { value ->
                topNText = value
            })

        }
        Spacer(Modifier.height(16.dp))

        selectedStats.forEach { authorStat ->
            Row {
                if (authorStat.avatar.isNotEmpty()) {
                    AsyncImage(
                        model = authorStat.avatar,
                        contentDescription = "Avatar",
                        modifier = Modifier.wrapContentWidth().clip(CircleShape),
                    )
                    Spacer(Modifier.width(16.dp))
                }
                Text(
                    text = authorStat.author,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            ContributionGrid(dailyContributions = authorStat.dailyContributions, selectedRange)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ContributionGrid(
    dailyContributions: List<DailyContribution>,
    dateRange: LocalDateRange,
    modifier: Modifier = Modifier
) {
    val startOfYear = dateRange.first
    val endOfYear = dateRange.last

    val days: List<LocalDate> = generateSequence(startOfYear) { date ->
        if (date < endOfYear) date.plus(DatePeriod(days = 1)) else null
    }.toList()

    val contributionMap = dailyContributions.associateBy({ it.date }, { it.commitCount })

    val weeks = days.chunked(7)

    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        weeks.forEach { week ->
            Column {
                week.forEach { day ->
                    val count = contributionMap[day] ?: 0
                    val color = getContributionColor(count)

                    Box(
                        modifier = Modifier.size(12.dp).padding(1.dp)
                            .background(color, shape = RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun getContributionColor(count: Int): Color {
    return when (count) {
        0 -> MaterialTheme.colorScheme.surfaceVariant
        in 1..2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        in 3..5 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        in 6..9 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.primary
    }
}
