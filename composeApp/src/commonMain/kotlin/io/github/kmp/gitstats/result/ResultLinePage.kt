package io.github.kmp.gitstats.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.kmp.gitstats.shared.DateUtil.Companion.formatToString
import io.github.kmp.gitstats.shared.model.line.LineInfo
import io.github.kmp.gitstats.shared.model.line.formatLine


private enum class LineLabel(val color: Color, val label: String) {
    LINES(Color(0xFF66BB6A), "Lines"), INSERTIONS(Color(0xFF42A5F5), "Insertions"), DELETIONS(
        Color(
            0xFFEF5350
        ), "Deletions"
    )
}


@Composable
fun ShowLine(lineInfo: LineInfo) {
    if (lineInfo.stats.isEmpty()) return

    val chartData = arrayListOf<ChartData>()
    val labelLines = ChartLabel(LineLabel.LINES.color, LineLabel.LINES.label)
    val labelInsertions = ChartLabel(LineLabel.INSERTIONS.color, LineLabel.INSERTIONS.label)
    val labelDeletions = ChartLabel(LineLabel.DELETIONS.color, LineLabel.DELETIONS.label)

    val lines = arrayListOf<ChartModel>()
    val insertions = arrayListOf<ChartModel>()
    val deletions = arrayListOf<ChartModel>()

    lineInfo.stats.forEach {
        lines.add(ChartModel(it.dateRange.last, it.lines))
        insertions.add(ChartModel(it.dateRange.last, it.lineAdded))
        deletions.add(ChartModel(it.dateRange.last, it.lineRemoved))
    }
    chartData.add(ChartData(labelLines, lines))
    chartData.add(ChartData(labelInsertions, insertions))
    chartData.add(ChartData(labelDeletions, deletions))
    val title = "Lines Changed Over Time"

    Chart(title, chartData, { date ->
        date.formatToString()
    }, { lines ->
        formatLine(lines)
    })
}

