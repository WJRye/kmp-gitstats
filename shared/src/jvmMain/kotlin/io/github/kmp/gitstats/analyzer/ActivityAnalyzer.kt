package io.github.kmp.gitstats.analyzer

import io.github.kmp.gitstats.getUserConfig
import io.github.kmp.gitstats.model.CommitInfo
import io.github.kmp.gitstats.model.TimeRange
import io.github.kmp.gitstats.model.activity.ActivityInfo
import io.github.kmp.gitstats.model.activity.CodingTimeRangeStat
import io.github.kmp.gitstats.model.activity.CodingTimeStat
import io.github.kmp.gitstats.model.activity.CommitsChangedStat
import io.github.kmp.gitstats.model.activity.FileChangesStat
import io.github.kmp.gitstats.model.activity.FileChangesTimeRangeStat
import io.github.kmp.gitstats.model.activity.LinesChangedStat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class ActivityAnalyzer(val repoPath: String) {

    fun getActivityInfo(commits: List<CommitInfo>): ActivityInfo {
        val email = getUserConfig(repoPath).email
        val filterCommitsInPastXDays =
            filterCommitsInPastXDays(commits, days = TimeRange.WEEK.days - 1)

        val linesChangedStats = computeLinesChangedStats(filterCommitsInPastXDays, email)
        val commitsChangedStats = computeCommitsChangedStats(filterCommitsInPastXDays, email)

        val filterCommitsInPast365Days =
            filterCommitsInPastXDays(commits, days = TimeRange.YEAR.days - 1)
        val codingTimeRangeStats = computeCodingTimeRangeStats(filterCommitsInPast365Days)
        val filesChangedTimeRangeStats =
            computeFilesChangedTimeRangeStats(filterCommitsInPast365Days)

        return ActivityInfo(
            linesChangedStats = linesChangedStats,
            commitsChangedStats = commitsChangedStats,
            codingTimeRangeStats = codingTimeRangeStats,
            filesChangedTimeRangeStats = filesChangedTimeRangeStats
        )
    }


    private fun computeLinesChangedStats(
        commits: Map<LocalDate, List<CommitInfo>>, authorEmail: String
    ): List<LinesChangedStat> {
        return commits.map {
            val allLines = it.value.asSequence().map { it.lines }.sorted().toList()
            val userLines =
                it.value.asSequence().filter { it.email == authorEmail }.sumOf { it.lines }
            val averageLines = allLines.average().toLong()
            val medianLines = if (allLines.isNotEmpty()) {
                val middle = allLines.size / 2
                if (allLines.size % 2 == 0) (allLines[middle - 1] + allLines[middle]) / 2 else allLines[middle]
            } else 0
            LinesChangedStat(it.key, averageLines, medianLines, userLines)
        }
    }

    private fun computeCommitsChangedStats(
        commits: Map<LocalDate, List<CommitInfo>>, authorEmail: String
    ): List<CommitsChangedStat> {
        return commits.map { (date, commitsOnDay) ->
            val myCount = commitsOnDay.count { it.email == authorEmail }
            CommitsChangedStat(date, commitsOnDay.size, myCount)
        }
    }


    private fun filterCommitsInPastXDays(
        commits: List<CommitInfo>, days: Int
    ): Map<LocalDate, List<CommitInfo>> {
        val commitsInPastXDay = mutableMapOf<LocalDate, MutableList<CommitInfo>>()
        val previousWeekEnd = commits.last().date.date
        val previousWeekStart = previousWeekEnd.minus(days, DateTimeUnit.DAY)
        for (i in commits.size - 1 downTo 0) {
            val commit = commits[i]
            val date = commit.date.date
            if (date !in previousWeekStart..previousWeekEnd) {
                break
            }
            commitsInPastXDay.getOrPut(date) { mutableListOf() }.add(commit)
        }
        //fill empty
        for (i in 0..days) {
            val date = previousWeekStart.plus(i, DateTimeUnit.DAY)
            commitsInPastXDay.getOrPut(date) { mutableListOf() }
        }
        return commitsInPastXDay.toSortedMap()
    }

    private fun computeCodingTimeRangeStats(filterCommitsInPast365Days: Map<LocalDate, List<CommitInfo>>): List<CodingTimeRangeStat> {
        val commits = filterCommitsInPast365Days.values.flatten().asReversed()
        val endDate = commits.first().date.date
        val commitsInTimeRange = hashMapOf<TimeRange, MutableList<CommitInfo>>()
        commits.forEach { commit ->
            val days = commit.date.date.daysUntil(endDate)
            if (days in (0..TimeRange.WEEK.days)) {
                commitsInTimeRange.getOrPut(TimeRange.WEEK) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.MONTH.days)) {
                commitsInTimeRange.getOrPut(TimeRange.MONTH) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.THREE_MONTHS.days)) {
                commitsInTimeRange.getOrPut(TimeRange.THREE_MONTHS) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.HALF_YEAR.days)) {
                commitsInTimeRange.getOrPut(TimeRange.HALF_YEAR) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.YEAR.days)) {
                commitsInTimeRange.getOrPut(TimeRange.YEAR) { mutableListOf() }.add(commit)
            }
        }
        return commitsInTimeRange.map { (timeRange, commits) ->
            val codingTimeStats = commits.asSequence().groupingBy {
                it.date.dayOfWeek.isoDayNumber to it.date.hour
            }.eachCount().map { (key, count) ->
                CodingTimeStat(dayOfWeek = key.first, hour = key.second, commitCount = count)
            }.toList()
            CodingTimeRangeStat(timeRange, codingTimeStats)
        }
    }

    private fun computeFilesChangedTimeRangeStats(filterCommitsInLast90Days: Map<LocalDate, List<CommitInfo>>): List<FileChangesTimeRangeStat> {
        val commits = filterCommitsInLast90Days.values.flatten().asReversed()
        val end = commits.first().date.date
        val commitsInTimeRange = hashMapOf<TimeRange, MutableList<CommitInfo>>()
        commits.forEach { commit ->
            val days = commit.date.date.daysUntil(end)
            if (days in (0..TimeRange.WEEK.days)) {
                commitsInTimeRange.getOrPut(TimeRange.WEEK) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.MONTH.days)) {
                commitsInTimeRange.getOrPut(TimeRange.MONTH) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.THREE_MONTHS.days)) {
                commitsInTimeRange.getOrPut(TimeRange.THREE_MONTHS) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.HALF_YEAR.days)) {
                commitsInTimeRange.getOrPut(TimeRange.HALF_YEAR) { mutableListOf() }.add(commit)
            }
            if (days in (0..TimeRange.YEAR.days)) {
                commitsInTimeRange.getOrPut(TimeRange.YEAR) { mutableListOf() }.add(commit)
            }
        }
        return commitsInTimeRange.map { (timeRange, commits) ->
            val fileChangesStats = commits.asSequence().map { it.fileInfo }.flatten().groupingBy {
                it.path
            }.eachCount().map { (key, count) ->
                FileChangesStat(key, count)
            }.sortedByDescending { it.count }.toList()

            FileChangesTimeRangeStat(timeRange, fileChangesStats)
        }
    }
}