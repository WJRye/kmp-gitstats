package io.github.kmp.gitstats.analyzer

import io.github.kmp.gitstats.GIT_COMMIT_SHA
import io.github.kmp.gitstats.GIT_FILE_STAT
import io.github.kmp.gitstats.model.CommitInfo
import io.github.kmp.gitstats.model.file.FileInfo
import io.github.kmp.gitstats.model.file.FileTypeStat
import io.github.kmp.gitstats.model.file.FileYearSize
import io.github.kmp.gitstats.model.file.NO_EXT
import io.github.kmp.gitstats.model.file.formatSize
import io.github.kmp.gitstats.processed
import kotlinx.datetime.LocalDate

class FileAnalyzer(val repoPath: String) {

    companion object {
        /**
         * Git command to retrieve the recursive list of files in a Git tree along with their sizes.
         *
         * val GIT_FILE_STAT = arrayListOf("git", "ls-tree", "-r", "-l") + sha
         *
         * Explanation:
         * - "git ls-tree": Git command to list the contents of a tree object.
         * - "-r": Recursively lists all files in the directory tree.
         * - "-l": Includes the file size in bytes (only for blobs).
         * - sha: The commit SHA or tree SHA to inspect.
         *
         * Example output line:
         *   100644 blob f2c1a81c5f243318...  1234  path/to/file.txt
         *
         * To parse this output, the following regular expression is used:
         *
         * Regex("""^\S+\s+\S+\s+\S+\s+(\d+)\s+(.+)$""")
         *
         * Explanation of the regex:
         * - ^\S+           → Matches the file mode (e.g., 100644)
         * - \s+            → Whitespace
         * - \S+            → Matches the object type (e.g., blob)
         * - \s+            → Whitespace
         * - \S+            → Matches the object hash
         * - \s+            → Whitespace
         * - (\d+)          → Captures the file size in bytes (Group 1)
         * - \s+            → Whitespace
         * - (.+)           → Captures the file path (Group 2)
         * - $              → End of line
         *
         * The regex captures two useful components from each line:
         *   1. The file size (as an integer).
         *   2. The full relative path of the file in the repository.
         */
        private const val FILE_STAT_PATTERN = """^\S+\s+\S+\s+\S+\s+(\d+)\s+(.+)$"""

        private const val SHA_HEAD = "HEAD"
    }

    fun getFileInfo(commits: List<CommitInfo>): FileInfo {
        val fileYearSizeStats = computeFileYearSizeStats(commits)
        val fileTypeStats = computeFileTypeStats(SHA_HEAD)
        return FileInfo(
            fileYearSizeStats = fileYearSizeStats, fileTypeStats = fileTypeStats
        ).apply {
            this.totalSizeReadable = formatSize(fileTypeStats.sumOf { it.totalSizeInBytes })
        }
    }

    private fun computeFileYearSizeStats(commits: List<CommitInfo>): List<FileYearSize> {
        val start = commits.first().date
        val end = commits.last().date
        return commits.groupBy { it.date.date.year }.map { (year, _) ->
            val args = GIT_COMMIT_SHA + "--before=${year + 1}-01-01T00:00:00" + SHA_HEAD
            val sha = processed(repoPath, args).getOrElse(0, { "" })
            Pair(year, sha)
        }.map { pair ->
            val sizeInBytes = computeFileTypeStats(pair.second).sumOf { it.totalSizeInBytes }
            when (pair.first) {
                start.year -> {
                    FileYearSize(start.date, 0L)
                }

                end.year -> {
                    FileYearSize(end.date, sizeInBytes)
                }

                else -> {
                    FileYearSize(
                        LocalDate(pair.first, 12, 31), sizeInBytes
                    )
                }
            }
        }
    }


    private fun computeFileTypeStats(sha: String): List<FileTypeStat> {
        val lines = processed(repoPath, GIT_FILE_STAT + sha)

        val typeStats = mutableMapOf<String, Pair<Int, Long>>() // ext -> (count, sizeInBytes)

        lines.forEach { line ->
            // Match: mode type object size<TAB>path (path can contain spaces!)
            val regex = Regex(FILE_STAT_PATTERN)
            val match = regex.find(line) ?: return@forEach

            val size = match.groupValues[1].toLongOrNull() ?: return@forEach
            val path = match.groupValues[2]

            val ext = path.substringAfterLast('.', missingDelimiterValue = "").lowercase()
                .ifBlank { NO_EXT }

            val (count, total) = typeStats.getOrDefault(ext, 0 to 0L)
            typeStats[ext] = count + 1 to total + size
        }

        val count = typeStats.values.sumOf { it.first }
        val size = typeStats.values.sumOf { it.second }
        return typeStats.map { (ext, stat) ->
            FileTypeStat(
                extension = ext, count = stat.first, stat.second
            ).apply {
                this.countPercent = formatToPercent(this.count.toLong(), count.toLong())
                this.totalSizePercent = formatToPercent(this.totalSizeInBytes, size)
            }
        }
    }


    private fun formatToPercent(divisor: Long, dividend: Long): String {
        return "(${(("%.2f".format((divisor.toFloat() / dividend) * 100)))}%)"
    }
}