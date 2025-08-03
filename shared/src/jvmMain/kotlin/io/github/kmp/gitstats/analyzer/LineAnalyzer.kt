package io.github.kmp.gitstats.analyzer

import io.github.kmp.gitstats.model.CommitInfo
import io.github.kmp.gitstats.model.line.LineInfo
import io.github.kmp.gitstats.model.line.LineStat
import io.github.kmp.gitstats.model.line.formatLine
import io.github.kmp.gitstats.model.serializer.LocalDateRange
import kotlinx.datetime.LocalDate
import kotlin.math.abs

class LineAnalyzer {

    fun getLineInfo(commits: List<CommitInfo>): LineInfo {
        val stats = computeLineStats(commits)
        return LineInfo(stats)
    }

    private fun computeLineStats(commits: List<CommitInfo>): List<LineStat> {
        val stats = arrayListOf<LineStat>()
        val start = commits.first().date
        val end = commits.last().date
        var currentYear = start.year
        var index = 0
        var lastYearLines = 0L

        fun getLines(year: Int): Triple<Long, Long, Long> {
            var lines = 0L
            var insertions = 0L
            var deletions = 0L
            val startIndex = index
            for (i in startIndex..commits.size - 1) {
                val commit = commits[i]
                if (commit.date.date.year <= year) {
                    insertions += commit.insertions
                    deletions += commit.deletions
                    lines += commit.insertions - commit.deletions
                    index++
                } else {
                    break
                }
            }
            return Triple(lines, insertions, deletions)
        }

        if (start.year == end.year) {
            stats.add(LineStat(dateRange = LocalDateRange(start.date, start.date), lines = 0, lineAdded = 0, lineRemoved = 0))
            val ret = getLines(currentYear)
            stats.add(LineStat(dateRange=LocalDateRange(start.date, end.date), lines = ret.first, lineAdded = ret.second, lineRemoved = ret.third))
            return stats
        }

        if (start.dayOfYear != 1) {
            //add first
            stats.add(LineStat(dateRange = LocalDateRange(start.date, start.date), lines = 0, lineAdded = 0, lineRemoved = 0))
            val endOfFirstYear = LocalDate(currentYear, 12, 31)
            val ret = getLines(currentYear)
            stats.add(LineStat(dateRange=LocalDateRange(start.date, endOfFirstYear), lines = ret.first, lineAdded = ret.second, lineRemoved = ret.third))
            lastYearLines = ret.first
            currentYear++
        }

        while (currentYear < end.year) {
            val startOfCurrentYear = LocalDate(currentYear, 1, 1)
            val endOfCurrentYear = LocalDate(currentYear, 12, 31)
            val ret = getLines(currentYear)
            val currentYearLines = lastYearLines + ret.first
            stats.add(LineStat(dateRange = LocalDateRange(startOfCurrentYear, endOfCurrentYear), lines = currentYearLines, lineAdded = ret.second, lineRemoved = ret.third))
            lastYearLines = currentYearLines
            currentYear++
        }

        val startOfLastYear = LocalDate(currentYear, 1, 1)
        val ret = getLines(currentYear)
        val currentYearLines = lastYearLines + ret.first
        stats.add(LineStat(dateRange = LocalDateRange(maxOf(start.date, startOfLastYear), end.date),lines =  currentYearLines, lineAdded = ret.second, lineRemoved = ret.third))
        return stats
    }


}