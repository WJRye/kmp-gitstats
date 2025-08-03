package io.github.kmp.gitstats.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.shared.DateUtil.Companion.formatToMDString
import io.github.kmp.gitstats.shared.model.activity.ActivityInfo
import io.github.kmp.gitstats.shared.model.activity.CommitsChangedStat
import io.github.kmp.gitstats.shared.model.activity.LinesChangedStat
import io.github.kmp.gitstats.result.activity.CodingTimeHeatmap
import io.github.kmp.gitstats.result.activity.TopChangedFiles


private enum class CodeHeartbeatLabel(val color: Color, val label: String) {
    AVERAGE(Color(0xFF4CAF50), "Average"), MEDIAN(
        Color(0xFFFF9800), "Median"
    ),
    MY_CODE(Color(0xFF2196F3), "My Code"),

}

private enum class CommitFrequencyLabel(val color: Color, val label: String) {
    MY_COMMITS(Color(0xFF2196F3), "My Commits"),
}

@Composable
fun ShowActivity(activityInfo: ActivityInfo) {
    if (activityInfo.linesChangedStats.isEmpty()) return
    val padding = 16.dp
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(padding)) {
        CodeHeartbeatChart(activityInfo.linesChangedStats)
        ChartDivider()
        CommitFrequencyHeatmap(activityInfo.commitsChangedStats)
        ChartDivider()
        CodingTimeHeatmap(activityInfo.codingTimeRangeStats)
        ChartDivider()
        TopChangedFiles(activityInfo.filesChangedTimeRangeStats)
    }
}

@Composable
private fun ChartDivider() {
    Spacer(Modifier.height(32.dp))
    HorizontalDivider(
        thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline
    )
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun CodeHeartbeatChart(codeHeartbeatEntryList: List<LinesChangedStat>) {
    val title = "Lines Changed"
    val chartData = arrayListOf<ChartData>()
    val labelAverage =
        ChartLabel(CodeHeartbeatLabel.AVERAGE.color, CodeHeartbeatLabel.AVERAGE.label)
    val labelMedian = ChartLabel(CodeHeartbeatLabel.MEDIAN.color, CodeHeartbeatLabel.MEDIAN.label)
    val labelMyCode = ChartLabel(CodeHeartbeatLabel.MY_CODE.color, CodeHeartbeatLabel.MY_CODE.label)
    val average = arrayListOf<ChartModel>()
    val median = arrayListOf<ChartModel>()
    val myCode = arrayListOf<ChartModel>()
    codeHeartbeatEntryList.forEach {
        average.add(ChartModel(it.date, it.averageLines))
        median.add(ChartModel(it.date, it.medianLines))
        myCode.add(ChartModel(it.date, it.userLines))
    }
    chartData.add(ChartData(labelAverage, average))
    chartData.add(ChartData(labelMedian, median))
    chartData.add(ChartData(labelMyCode, myCode))
    Chart(title, chartData, { it.formatToMDString() })
}


@Composable
private fun CommitFrequencyHeatmap(stats: List<CommitsChangedStat>) {
    val title = "Commits Changed"
    val commits = stats.map { ChartModel(it.date, it.count.toLong()) }
    val labelCommits = ChartLabel(MaterialTheme.colorScheme.primary, "Commits")
    val myCommits = stats.map { ChartModel(it.date, it.myCount.toLong()) }
    val labelMyCommits =
        ChartLabel(CommitFrequencyLabel.MY_COMMITS.color, CommitFrequencyLabel.MY_COMMITS.label)
    val chartData = arrayListOf<ChartData>()
    chartData.add(ChartData(labelCommits, commits))
    chartData.add(ChartData(labelMyCommits, myCommits))
    Chart(title, chartData, { it.formatToMDString() })
}




