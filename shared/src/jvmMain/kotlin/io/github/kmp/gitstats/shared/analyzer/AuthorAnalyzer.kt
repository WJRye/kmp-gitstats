package io.github.kmp.gitstats.shared.analyzer

import io.github.kmp.gitstats.shared.model.CommitInfo
import io.github.kmp.gitstats.shared.model.author.AuthorContributionInfo
import io.github.kmp.gitstats.shared.model.author.AuthorDailyStat
import io.github.kmp.gitstats.shared.model.author.AuthorInfo
import io.github.kmp.gitstats.shared.model.author.AuthorStat
import io.github.kmp.gitstats.shared.model.author.DailyContribution
import io.github.kmp.gitstats.shared.model.author.DateRangeStat
import io.github.kmp.gitstats.shared.model.serializer.LocalDateRange
import io.github.kmp.gitstats.shared.provideLocaleLanguage
import kotlinx.datetime.LocalDate
import java.security.MessageDigest

class AuthorAnalyzer {

    companion object {
        private const val AVATAR = "https://www.gravatar.com/avatar/"

        private val IS_EN = provideLocaleLanguage().isEnglish()
    }

    fun getAuthorInfo(commits: List<CommitInfo>): AuthorInfo {
        val authorStats = computeAuthorStats(commits)
        val authorContributionInfo: AuthorContributionInfo = computeAuthorContribution(commits)
        return AuthorInfo(
            authorStats = authorStats, authorContributionInfo = authorContributionInfo
        )
    }

    private fun computeAuthorContribution(commits: List<CommitInfo>): AuthorContributionInfo {
        val commitsByAuthor = commits.groupBy { it.author }
        val start = commits.first().date.date
        val end = commits.last().date.date
        val yearlyRanges = splitDateRangeByYear(start, end)
        val dateRangeStats = yearlyRanges.map { dateRange ->
            DateRangeStat(
                dateRange = dateRange, getAuthorStatsByDateRange(commitsByAuthor, dateRange)
            )
        }
        return AuthorContributionInfo(LocalDateRange(start, end), yearlyRanges, dateRangeStats)
    }


    private fun getAuthorStatsByDateRange(
        commits: Map<String, List<CommitInfo>>, dateRange: LocalDateRange
    ): List<AuthorDailyStat> {
        return commits.asSequence().map { (author, authorCommits) ->
            val dailyList =
                authorCommits.groupingBy { it.date.date }.eachCount().asSequence()
                    .filter { it.key >= dateRange.first && it.key <= dateRange.last }
                    .map { (date, count) -> DailyContribution(date, count) }.sortedBy { it.date }
                    .toList()
            val email = authorCommits.first().email
            AuthorDailyStat(
                avatarUrl(email), author = author, dailyContributions = dailyList
            )
        }
            .sortedByDescending { it.dailyContributions.sumOf { dailyContributions -> dailyContributions.commitCount } }
            .toList()
    }

    private fun splitDateRangeByYear(start: LocalDate, end: LocalDate): List<LocalDateRange> {
        val yearlyRanges = mutableListOf<LocalDateRange>()
        var currentYear = start.year

        if (start.year == end.year) {
            yearlyRanges.add(LocalDateRange(start, end))
            return yearlyRanges.asReversed()
        }

        if (start.dayOfYear != 1) {
            val endOfFirstYear = LocalDate(currentYear, 12, 31)
            yearlyRanges.add(LocalDateRange(start, minOf(end, endOfFirstYear)))
            currentYear++ // Move to the next year for full year segments
        }

        while (currentYear < end.year) {
            val startOfCurrentYear = LocalDate(currentYear, 1, 1)
            val endOfCurrentYear = LocalDate(currentYear, 12, 31)
            yearlyRanges.add(LocalDateRange(startOfCurrentYear, endOfCurrentYear))
            currentYear++
        }
        val startOfLastYear = LocalDate(currentYear, 1, 1)
        yearlyRanges.add(LocalDateRange(maxOf(start, startOfLastYear), end))
        return yearlyRanges.asReversed()
    }

    private fun computeAuthorStats(commits: List<CommitInfo>): List<AuthorStat> {
        val allCommits = commits.size
        val allLines = commits.sumOf { it.insertions - it.deletions }
        val allFiles = commits.asSequence().flatMap { it.fileInfo.map { f -> f.path } }.toSet().size
        val authorStats = commits.groupBy { it.email }.map { (email, authorCommits) ->
            val author = authorCommits.first().author
            val avatar = avatarUrl(email)// gravatar URL by email
            val totalCommits = authorCommits.size
            val linesAdded = authorCommits.sumOf { it.insertions }
            val linesRemoved = authorCommits.sumOf { it.deletions }
            val linesChanged = linesAdded - linesRemoved
            val filesChanged =
                authorCommits.asSequence().flatMap { it.fileInfo.map { f -> f.path } }.toSet().size
            val firstCommitDate = authorCommits.minBy { it.date }.date.date
            val lastCommitDate = authorCommits.maxBy { it.date }.date.date
            val activeDays = authorCommits.groupBy { it.date.date }.size

            AuthorStat(
                author = author,
                email = email,
                avatarUrl = avatar,
                commitCount = totalCommits,
                linesChanged = linesChanged,
                linesAdded = linesAdded,
                linesRemoved = linesRemoved,
                filesChanged = filesChanged,
                activeDays = activeDays
            ).apply {
                activeTime = "$firstCommitDate ~ $lastCommitDate"
                commitsPercent = format(this.commitCount.toLong(), allCommits.toLong())
                linesPercent = format(this.linesChanged, allLines)
                filesPercent = format(this.filesChanged.toLong(), allFiles.toLong())
            }
        }.toList()
        return authorStats
    }

    /**
     * Generates the MD5 hash of the given URL string, typically used for services like Gravatar.
     *
     * @param url The input string to hash. For example: "https://www.gravatar.com/avatar/"
     * @return A lowercase hexadecimal MD5 hash string.
     */
    private fun md5(url: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(url.trim().lowercase().toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun avatarUrl(email: String): String {
        return if (IS_EN) "$AVATAR${md5(email)}" else ""
    }

    private fun format(divisor: Long, dividend: Long): String {
        return "(${(("%.2f".format((divisor.toFloat() / dividend) * 100)))}%)"
    }

}

