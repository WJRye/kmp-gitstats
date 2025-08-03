package io.github.kmp.gitstats.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.DateUtil.Companion.formatToString
import io.github.kmp.gitstats.model.file.FileInfo
import io.github.kmp.gitstats.model.file.FileTypeStat
import io.github.kmp.gitstats.model.file.formatSize

private enum class SortBy(val label: String) {
    COUNT("By Count"), TOTAL_SIZE("By Total Size")
}


@Composable
fun ShowFile(fileInfo: FileInfo) {
    if (fileInfo.fileTypeStats.isEmpty()) return
    val scrollState = rememberScrollState()
    Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
        FileYearStats(fileInfo)
        Spacer(Modifier.height(32.dp))
        HorizontalDivider(
            thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(32.dp))
        FileTypeStats(fileInfo)
    }

}

@Composable
private fun FileYearStats(fileInfo: FileInfo) {
    val data = fileInfo.fileYearSizeStats.map {
        ChartModel(it.year, it.sizeInBytes)
    }
    val chartData = arrayListOf<ChartData>()
    chartData.add(ChartData(ChartLabel(MaterialTheme.colorScheme.primary, "Size"), data))
    val title = "Size Changed Over Time"
    Chart(title, chartData, { date ->
        date.formatToString()
    }, { bytes ->
        formatSize(bytes)
    })
}

@Composable
private fun FileTypeStats(fileInfo: FileInfo) {
    val stats = fileInfo.fileTypeStats
    var sortBy by remember { mutableStateOf(SortBy.COUNT) }
    val sortedStats = remember(sortBy, stats) {
        val comparator: Comparator<FileTypeStat> = when (sortBy) {
            SortBy.COUNT -> compareByDescending { it.count }
            SortBy.TOTAL_SIZE -> compareByDescending { it.totalSizeInBytes }
        }
        stats.sortedWith(comparator)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Text("File Type Summary", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "Total Count: ${fileInfo.totalCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "Total Size: ${fileInfo.totalSizeReadable}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        Selector(SortBy.entries, { it.label }, { sortBy = it })

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableHeader("Type", Modifier.weight(1f))
            TableHeader("Count", Modifier.weight(1f))
            TableHeader("Total Size", Modifier.weight(1f))
        }
        sortedStats.forEach { stat ->
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(
                    stat.ext(), modifier = Modifier.weight(1f)
                )
                TableCell(
                    stat.countReadable, modifier = Modifier.weight(1f)
                )
                TableCell(
                    stat.totalSizeReadable, modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

