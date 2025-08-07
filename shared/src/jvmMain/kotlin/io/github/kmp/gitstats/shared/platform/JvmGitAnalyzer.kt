package io.github.kmp.gitstats.shared.platform


import io.github.kmp.gitstats.shared.DateUtil
import io.github.kmp.gitstats.shared.DateUtil.Companion.isoDateToLocalDateTime
import io.github.kmp.gitstats.shared.GIT_BRANCH
import io.github.kmp.gitstats.shared.GIT_LOG_COMMIT
import io.github.kmp.gitstats.shared.GIT_PULL
import io.github.kmp.gitstats.shared.analyzer.ActivityAnalyzer
import io.github.kmp.gitstats.shared.analyzer.AuthorAnalyzer
import io.github.kmp.gitstats.shared.analyzer.FileAnalyzer
import io.github.kmp.gitstats.shared.analyzer.LineAnalyzer
import io.github.kmp.gitstats.shared.analyzer.OverviewAnalyzer
import io.github.kmp.gitstats.shared.analyzer.TagAnalyzer
import io.github.kmp.gitstats.shared.execute
import io.github.kmp.gitstats.shared.model.AnalysisResult
import io.github.kmp.gitstats.shared.model.CommitFileInfo
import io.github.kmp.gitstats.shared.model.CommitInfo
import io.github.kmp.gitstats.shared.processed
import io.github.kmp.gitstats.shared.processing

/**
 * JVM implementation of the GitAnalyzer interface.
 *
 * This class performs various Git analysis operations (e.g., commit history, author statistics,
 * tags, file changes) on a repository located at the given path.
 * Designed to run in desktop JVM environments only.
 *
 * @property repoPath The local file system path to the Git repository (absolute).
 */
class JvmGitAnalyzer(val repoPath: String) : GitAnalyzer {
    private val overviewAnalyzer: OverviewAnalyzer = OverviewAnalyzer(repoPath)
    private val activityAnalyzer: ActivityAnalyzer = ActivityAnalyzer(repoPath)
    private val authorAnalyzer: AuthorAnalyzer = AuthorAnalyzer()
    private val tagAnalyzer: TagAnalyzer = TagAnalyzer(repoPath)

    private val fileAnalyzer: FileAnalyzer = FileAnalyzer(repoPath)
    private val lineAnalyzer: LineAnalyzer = LineAnalyzer()


    fun getAllCommitsByGitCommand(): List<CommitInfo> {
        val commits = arrayListOf<CommitInfo>()
        var currentCommit: CommitInfo? = null
        // Regular expression to match lines of the format produced by `--numstat`,
        // which looks like: "12	4	src/main/File.kt"
        // This line contains: [lines added] [tab] [lines deleted] [tab] [file path]
        val pattern = """^\d+\t\d+\t.+$"""
        processing(repoPath, GIT_LOG_COMMIT) { line ->
            when {
                line == "--" -> {
                    currentCommit?.let {
                        it.insertions = it.fileInfo.sumOf { f -> f.insertions }
                        it.deletions = it.fileInfo.sumOf { f -> f.deletions }
                        it.lines = it.insertions - it.deletions
                        commits.add(it)
                    }
                    currentCommit = CommitInfo()
                }

                currentCommit != null && currentCommit.hash.isEmpty() -> {
                    currentCommit.hash = line
                }

                currentCommit != null && currentCommit.author.isEmpty() -> {
                    currentCommit.author = line
                }

                currentCommit != null && currentCommit.email.isEmpty() -> {
                    currentCommit.email = line
                }

                currentCommit != null && !currentCommit.hasDate -> {
                    currentCommit.date = isoDateToLocalDateTime(line)
                    currentCommit.hasDate = true
                }

                currentCommit != null && currentCommit.message.isEmpty() -> {
                    currentCommit.message = line
                }

                currentCommit != null && Regex(pattern).matches(line) -> {
                    val parts = line.split("\t")
                    val insertions = parts.getOrElse(0, { "0" }).toLong()
                    val deletions = parts.getOrElse(1, { "0" }).toLong()
                    val file = parts.getOrElse(2, { "" })
                    currentCommit.fileInfo.add(CommitFileInfo(file, insertions, deletions))
                }

                else -> {

                }
            }
        }
        return commits.asReversed()
    }

    override fun analyze(): AnalysisResult {
        val startTime = DateUtil.Companion.currentMilliseconds()
        val commits = getAllCommitsByGitCommand()
        val activityInfo = activityAnalyzer.getActivityInfo(commits)
        val authorInfo = authorAnalyzer.getAuthorInfo(commits)
        val tagInfo = tagAnalyzer.getTagInfo()
        val lineInfo = lineAnalyzer.getLineInfo(commits)
        val fileInfo = fileAnalyzer.getFileInfo(commits)
        val overviewInfo = overviewAnalyzer.getOverviewInfo(startTime, commits)
        return AnalysisResult(
            repoPath, overviewInfo, activityInfo, authorInfo, fileInfo, lineInfo, tagInfo
        )
    }

    override fun pull(): Pair<Int, String> {
        val branchName = processed(repoPath, GIT_BRANCH).getOrElse(
            0, { "" })
        val args = arrayListOf<String>()
        args.addAll(GIT_PULL)
        if (branchName.isNotEmpty()) {
            args.addAll(arrayListOf("origin", branchName))
        }
        return execute(repoPath, args)
    }
}
