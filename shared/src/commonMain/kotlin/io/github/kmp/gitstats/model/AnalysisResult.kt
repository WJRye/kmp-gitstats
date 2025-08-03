package io.github.kmp.gitstats.model

import io.github.kmp.gitstats.model.activity.ActivityInfo
import io.github.kmp.gitstats.model.author.AuthorInfo
import io.github.kmp.gitstats.model.file.FileInfo
import io.github.kmp.gitstats.model.line.LineInfo
import io.github.kmp.gitstats.model.overview.OverviewInfo
import io.github.kmp.gitstats.model.tag.TagInfo

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisResult(
    val repoPath: String = "",
    val overViewInfo: OverviewInfo = OverviewInfo(),
    val activityInfo: ActivityInfo = ActivityInfo(),
    val authorInfo: AuthorInfo = AuthorInfo(),
    val fileInfo: FileInfo = FileInfo(),
    val lineInfo: LineInfo = LineInfo(),
    val tagInfo: TagInfo = TagInfo(),
)