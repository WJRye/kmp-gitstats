package io.github.kmp.gitstats.result.activity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.model.TimeRange
import io.github.kmp.gitstats.model.activity.FileChangesStat
import io.github.kmp.gitstats.model.activity.FileChangesTimeRangeStat
import io.github.kmp.gitstats.result.Selector
import io.github.kmp.gitstats.result.TopNInputBox

private const val DEFAULT_TOP_NUM = "20"

@Composable
fun TopChangedFiles(fileChangesStats: List<FileChangesTimeRangeStat>) {
    var topNText by remember { mutableStateOf(DEFAULT_TOP_NUM) }
    var selectedRange by remember { mutableStateOf(TimeRange.WEEK) }
    val fileChanges = remember(fileChangesStats, selectedRange, topNText) {
        fileChangesStats.asSequence().filter { it.timeRange == selectedRange }
            .flatMap { it.fileChangesStats }.take(topNText.toInt()).toList()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Top Changed Files", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Selector<TimeRange>(
                TimeRange.entries,
                labelProvider = { it.label },
                onSelect = { timeRange ->
                    selectedRange = timeRange
                })

            TopNInputBox(topNText, { value ->
                topNText = value
            })

        }
        Spacer(Modifier.height(16.dp))
        Chart(fileChanges)
    }
}

@Composable
private fun Chart(fileChanges: List<FileChangesStat>) {
    val maxChange = fileChanges.maxOf { it.count }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            fileChanges.forEach {
                FileChangeRow(it, maxChange)
            }
        }
    }
}

@Composable
private fun FileChangeRow(file: FileChangesStat, maxCount: Int) {
    val fraction = (file.count.toFloat() / maxCount).coerceIn(0f, 1f)
    val startColor = MaterialTheme.colorScheme.primaryContainer
    val endColor = MaterialTheme.colorScheme.tertiary
    val animatedFraction by animateFloatAsState(targetValue = fraction)
    val color = lerp(startColor, endColor, animatedFraction)

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                file.filePath,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(12.dp))
            Text("${file.count} changes")
        }

        LinearProgressIndicator(
            progress = { fraction },
            color = color,
            trackColor = Color.LightGray,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp))
        )
    }
}
