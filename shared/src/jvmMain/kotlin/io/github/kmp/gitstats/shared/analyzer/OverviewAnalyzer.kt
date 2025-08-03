package io.github.kmp.gitstats.shared.analyzer

import io.github.kmp.gitstats.shared.DateUtil
import io.github.kmp.gitstats.shared.DateUtil.Companion.formatToString
import io.github.kmp.gitstats.shared.GIT_LS_FILES
import io.github.kmp.gitstats.shared.model.CommitInfo
import io.github.kmp.gitstats.shared.model.overview.OverviewInfo
import io.github.kmp.gitstats.shared.processed
import kotlinx.datetime.daysUntil
import java.io.File

class OverviewAnalyzer(private val repoPath: String) {


    fun getOverviewInfo(costStartTime: Long, commits: List<CommitInfo>): OverviewInfo {
        val start = commits.first().date
        val end = commits.last().date
        val ageDays = start.date.daysUntil(end.date)
        val activeDays = getActiveDays(commits)
        val files = countFilesInDirectory()

        val (insertions, deletions) = computeInsertionsDeletions(commits)
        val totalLines = insertions - deletions

        val projectName = File(repoPath).name
        val reportPeriod = "${start.formatToString()} to ${end.formatToString()}"
        val age =
            "$ageDays days, $activeDays active days (${"%.2f".format(activeDays * 100.0 / ageDays)}%)"

        val commitSize = commits.size.toDouble()
        val commitsPerActiveDay = "%.1f".format(commitSize / activeDays)
        val commitsPerAllDays = "%.1f".format(commitSize / ageDays)
        val totalAuthors = getAuthorCount(commits)
        val avgCommitsPerAuthor = "%.1f".format(commitSize / totalAuthors)

        val costEndTime = DateUtil.currentMilliseconds()
        val costTime = costEndTime - costStartTime
        val generatedTime = "${
            DateUtil.localDateTime().formatToString()
        } (in ${DateUtil.formatToDuration(costTime)})"

        return OverviewInfo(
            projectName = projectName,
            generatedTime = generatedTime,
            reportPeriod = reportPeriod,
            age = age,
            totalFiles = files,
            totalLines = totalLines,
            addedLines = insertions,
            removedLines = deletions,
            totalCommits = commits.size,
            commitsPerActiveDay = commitsPerActiveDay,
            commitsPerAllDays = commitsPerAllDays,
            totalAuthors = totalAuthors,
            avgCommitsPerAuthor = avgCommitsPerAuthor
        )
    }


    private fun getAuthorCount(commits: List<CommitInfo>): Int {
        return commits.map { it.author.trim().lowercase() }.toSet().size
    }

    private fun getActiveDays(commits: List<CommitInfo>): Int {
        return commits.map { it.date.date }.toSet().size
    }


    private fun computeInsertionsDeletions(commits: List<CommitInfo>): Pair<Long, Long> {
        val totalInsertions = commits.sumOf { it.insertions }
        val totalDeletions = commits.sumOf { it.deletions }
        return totalInsertions to totalDeletions
    }


    private fun countFilesInDirectory(): Int {
        return processed(repoPath, GIT_LS_FILES).size
    }
}