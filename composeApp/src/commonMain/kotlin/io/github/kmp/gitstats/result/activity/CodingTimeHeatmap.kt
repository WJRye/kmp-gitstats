package io.github.kmp.gitstats.result.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kmp.gitstats.shared.model.TimeRange
import io.github.kmp.gitstats.shared.model.activity.CodingTimeRangeStat
import io.github.kmp.gitstats.shared.model.activity.CodingTimeStat
import io.github.kmp.gitstats.result.Selector


val DAY_OF_WEEK = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun CodingTimeHeatmap(codingTimeRangeStats: List<CodingTimeRangeStat>) {
    var selectedRange by remember { mutableStateOf(TimeRange.WEEK) }

    val codingTimeStats = remember(codingTimeRangeStats, selectedRange) {
        codingTimeRangeStats.asSequence().filter { it.timeRange == selectedRange }
            .flatMap { it.codingTimeStats }.toList()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Coding Time Heatmap", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Distribution of active coding hours over time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        Selector<TimeRange>(
            TimeRange.entries,
            labelProvider = { it.label },
            onSelect = { timeRange ->
                selectedRange = timeRange
            })

        Spacer(Modifier.height(16.dp))
        Chart(codingTimeStats)
    }
}


@Composable
private fun Chart(codingTimeStats: List<CodingTimeStat>) {
    val maxCount = codingTimeStats.maxOf { it.commitCount }
    val rows = 7
    val columns = 24
    val labelPadding = 30.dp
    val yLabels = DAY_OF_WEEK
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(250.dp)) {
        val cellWidth = (maxWidth - labelPadding) / columns
        val cellHeight = (maxHeight - labelPadding) / rows
        val startColor = Color(0xFFE0E0E0)
        val stopColor = Color(0xFF4CAF50)
        Box {
            Canvas(Modifier.fillMaxSize()) {
                codingTimeStats.forEach { cell ->
                    val colorFactor = cell.commitCount / maxCount.toFloat()
                    val color = lerp(startColor, stopColor, colorFactor)

                    val x = labelPadding.toPx() + (cell.hour) * cellWidth.toPx()
                    val y = (cell.dayOfWeek - 1) * cellHeight.toPx()

                    drawRect(
                        color = color,
                        topLeft = Offset(x, y + labelPadding.toPx()),
                        size = Size(cellWidth.toPx(), cellHeight.toPx())
                    )
                }
            }

            // x
            Column {
                Spacer(modifier = Modifier.height(labelPadding))
                Row {
                    Spacer(modifier = Modifier.width(labelPadding))
                    repeat(columns) { hour ->
                        Box(
                            modifier = Modifier.width(cellWidth).height(labelPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$hour", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // y
            Row {
                Column {
                    Spacer(modifier = Modifier.height(labelPadding))
                    repeat(rows) { rowIndex ->
                        Box(
                            modifier = Modifier.width(labelPadding).height(cellHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = yLabels[rowIndex], fontSize = 10.sp, color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}