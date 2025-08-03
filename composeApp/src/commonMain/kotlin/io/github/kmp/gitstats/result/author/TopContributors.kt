package io.github.kmp.gitstats.result.author

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.kmp.gitstats.shared.model.author.AuthorStat
import io.github.kmp.gitstats.result.Selector
import io.github.kmp.gitstats.result.TableCell
import io.github.kmp.gitstats.result.TableHeader
import io.github.kmp.gitstats.result.TopNInputBox

private enum class SortBy(val label: String) {
    COMMITS("By Commits"), LINES_CHANGED("By Lines Changed"), FILES("By Files Changed"), ACTIVE_DAYS(
        "By Active Days"
    ),

}

private const val DEFAULT_TOP_NUM = "15"

@Composable
fun TopContributors(authorStats: List<AuthorStat>) {
    var topNText by remember { mutableStateOf(DEFAULT_TOP_NUM) }
    var sortBy by remember { mutableStateOf(SortBy.COMMITS) }
    val sortedList = remember(sortBy, authorStats, topNText) {
        val comparator: Comparator<AuthorStat> = when (sortBy) {
            SortBy.COMMITS -> compareByDescending { it.commitCount }
            SortBy.LINES_CHANGED -> compareByDescending { it.linesChanged }
            SortBy.FILES -> compareByDescending { it.filesChanged }
            SortBy.ACTIVE_DAYS -> compareByDescending { it.activeDays }
        }
        authorStats.asSequence().sortedWith(comparator)
            .take(minOf(topNText.toInt(), authorStats.size)).toList()
    }
    val hasAvatar = sortedList.first().avatarUrl.isNotEmpty()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Top Contributors", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Selector<SortBy>(SortBy.entries, labelProvider = { it.label }, onSelect = { entry ->
                sortBy = entry
            })

            TopNInputBox(topNText, { value ->
                topNText = value
            })

        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (hasAvatar) {
                TableHeader("Avatar", Modifier.weight(1f))
            }
            TableHeader("Name", Modifier.weight(1f))
            TableHeader("Commits", Modifier.weight(1f))
            TableHeader("Lines Changed", Modifier.weight(2f))
            TableHeader("Files Changed", Modifier.weight(1.5f))
            TableHeader("Active Days", Modifier.weight(1f))
            TableHeader("Active Time", Modifier.weight(2f))
        }

        Spacer(modifier = Modifier.height(4.dp))

        sortedList.forEach { stat ->
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasAvatar) {
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = stat.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.wrapContentWidth().clip(CircleShape))
                    }
                }
                TableCell(stat.author, Modifier.weight(1f))
                TableCell(
                    stat.commitReadable, Modifier.weight(1f)
                )
                TableCell(
                    stat.lineReadable, Modifier.weight(2f)
                )
                TableCell(
                    stat.fileReadable, Modifier.weight(1.5f)
                )
                TableCell(stat.activeDays.toString(), Modifier.weight(1f))
                TableCell(stat.activeTime, Modifier.weight(2f))
            }
        }
    }
}


