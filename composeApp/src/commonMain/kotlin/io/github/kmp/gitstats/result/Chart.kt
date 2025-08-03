package io.github.kmp.gitstats.result

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate

class ChartModel(val date: LocalDate, val value: Long)

class ChartLabel(val color: Color, val label: String)

data class ChartData(val label: ChartLabel, val dataPoints: List<ChartModel>)

fun interface ValueFormatter<in T> {
    fun format(value: T): String
}

class DefaultXValueFormatter : ValueFormatter<LocalDate> {
    override fun format(value: LocalDate): String {
        return value.toString()
    }
}

class DefaultYValueFormatter : ValueFormatter<Long> {
    override fun format(value: Long): String {
        return value.toString()
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Chart(
    title: String,
    chartData: List<ChartData>,
    xValueFormatter: ValueFormatter<LocalDate> = DefaultXValueFormatter(),
    yValueFormatter: ValueFormatter<Long> = DefaultYValueFormatter()
) {
    if (chartData.isEmpty()) return

    val chartHeight = 200.dp

    Column(modifier = Modifier.fillMaxWidth()) {
        //title
        ChartTitle(chartData, title)

        //center label
        ChartCenterLabel(chartData)

        Row(modifier = Modifier.fillMaxWidth()) {
            // Y label
            ChartYLabel(chartData, yValueFormatter, chartHeight)

            // main chart
            val hoverState = remember { mutableStateOf<Offset?>(null) }
            val hoveredEntry = remember { mutableStateOf<Pair<Offset, Int>?>(null) }
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(chartHeight)
                    .onPointerEvent(PointerEventType.Move) {
                        val position = it.changes.first().position
                        hoverState.value = position
                    }.onPointerEvent(PointerEventType.Exit) {
                        hoverState.value = null
                        hoveredEntry.value = null
                    }) {

                    ChartContent(chartData, hoverState.value) { newHoveredEntry ->
                        hoveredEntry.value = newHoveredEntry
                    }
                    hoveredEntry.value?.let { entry ->
                        ChartTooltipOverlay(chartData, entry, yValueFormatter)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        // x label
        ChartXLabel(chartData, xValueFormatter)
    }
}


@Composable
private fun ChartContent(
    chartData: List<ChartData>,
    hoverState: Offset?,
    onHoverEntryChanged: (Pair<Offset, Int>) -> Unit
) {
    val allPoints = chartData.asSequence().flatMap { it.dataPoints }.sortedBy { it.value }.toList()
    val maxY = allPoints.last().value.toFloat().coerceAtLeast(1f)
    val minY = allPoints.first().value.toFloat()
    val showNegative = minY < 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacingX = size.width / (chartData[0].dataPoints.size - 1)
        val height = size.height

        val yRange = maxY - if (showNegative) minY else 0f
        val yOffset = if (showNegative) minY else 0f

        fun scaleY(value: Float): Float {
            return height - ((value - yOffset) / yRange) * height
        }

        for (data in chartData) {

            val label = data.label

            val points = data.dataPoints.mapIndexed { index, entry ->
                val x = spacingX * index
                val y = scaleY(entry.value.toFloat())
                Offset(x, y)
            }

            val path = Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) moveTo(point.x, point.y)
                    else lineTo(point.x, point.y)
                }
            }

            val radius = 4.dp.toPx()
            points.forEachIndexed { index, point ->
                drawCircle(color = label.color, radius = radius, center = point)

                hoverState?.let { hoverStateValue ->
                    val distance = (hoverStateValue - point).getDistance()
                    val minDistance = radius * 2f
                    if (distance < minDistance) {
                        onHoverEntryChanged.invoke(Pair(point, index))
                        // highlight
                        drawCircle(
                            color = label.color, radius = minDistance, center = point, style = Fill
                        )
                    }
                }
            }

            drawPath(
                path = path, color = label.color, style = Stroke(
                    width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round
                )
            )
        }

        // y = 0
        val centerY = scaleY(0f)
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
private fun ChartXLabel(chartData: List<ChartData>, xValueFormatter: ValueFormatter<LocalDate>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        chartData.first().dataPoints.forEach {
            Text(
                text = xValueFormatter.format(it.date), fontSize = 10.sp, color = Color.Gray
            )
        }
    }
}

@Composable
private fun ChartYLabel(
    chartData: List<ChartData>, yValueFormatter: ValueFormatter<Long>, chartHeight: Dp
) {
    val yLabels = getYLabels(chartData, yValueFormatter)
    Column(
        modifier = Modifier.defaultMinSize(40.dp).height(chartHeight),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        yLabels.forEach { yLabel ->
            Text(
                text = yLabel, fontSize = 10.sp, color = Color.Gray
            )
        }
    }
}

@Composable
private fun ChartCenterLabel(chartData: List<ChartData>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        chartData.forEachIndexed { index, data ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(data.label.color)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = data.label.label, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
private fun ChartTitle(chartData: List<ChartData>, title: String) {
    val dateRange =
        "${chartData.first().dataPoints.first().date} ~ ${chartData.first().dataPoints.last().date}"
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Text(
        dateRange,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))
}


@Composable
private fun ChartTooltipOverlay(
    chartData: List<ChartData>, entry: Pair<Offset, Int>, yValueFormatter: ValueFormatter<Long>
) {
    val hoverPoint = entry.first
    val index = entry.second
    val date = chartData[0].dataPoints[index].date
    BoxWithConstraints {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxValue = maxWidthPx.toInt() - 160
        val dx = 12
        val dy = 40

        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 4.dp,
            modifier = Modifier.offset {
                IntOffset(
                    (hoverPoint.x + dx).toInt().coerceAtMost(maxValue),
                    (hoverPoint.y - dy).toInt().coerceAtLeast(0)
                )
            }) {
            Column(modifier = Modifier.padding(12.dp)) {

                Text(
                    "Date: $date",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    maxLines = 1
                )

                for (data in chartData) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${data.label.label}: ${yValueFormatter.format(data.dataPoints[index].value)}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


private fun getYLabels(
    chartData: List<ChartData>, valueFormatter: ValueFormatter<Long>
): List<String> {
    val allPoints = chartData.asSequence().flatMap { it.dataPoints }.sortedBy { it.value }.toList()
    val maxY = allPoints.last().value.toFloat().coerceAtLeast(1f)
    val minY = allPoints.first().value.toFloat()
    val showNegative = minY < 0f
    return if (showNegative) {
        listOf(maxY, maxY / 2, 0f, minY / 2, minY).map { it.toInt().toString() }
    } else {
        val partSize = when (maxY) {
            1f -> {
                1
            }

            2f -> {
                2
            }

            in 3f..100f -> {
                3
            }

            else -> {
                5
            }
        }
        (0..partSize).map {
            if (it == 0) {
                0.toString()
            } else {
                valueFormatter.format((maxY * it / partSize).toLong())
            }
        }.asReversed()
    }
}