package io.github.kmp.gitstats.model.line

import io.github.kmp.gitstats.model.serializer.LocalDateRange
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class LineInfo(val stats: List<LineStat> = emptyList())

@Serializable
data class LineStat(
    val dateRange: LocalDateRange, val lines: Long, val lineAdded: Long, val lineRemoved: Long
)

fun formatLine(number: Long): String {
    val absNumber = abs(number)
    return when {
        absNumber >= 1_000_000_000 -> "%.1fB".format(number / 1_000_000_000.0)
        absNumber >= 1_000_000 -> "%.1fM".format(number / 1_000_000.0)
        absNumber >= 1_000 -> "%.1fK".format(number / 1_000.0)
        else -> number.toString()
    }
}