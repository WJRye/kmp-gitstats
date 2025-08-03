package io.github.kmp.gitstats.shared.model

import io.github.kmp.gitstats.shared.model.activity.ActivityInfo
import io.github.kmp.gitstats.shared.model.author.AuthorInfo
import io.github.kmp.gitstats.shared.model.file.FileInfo
import io.github.kmp.gitstats.shared.model.line.LineInfo
import io.github.kmp.gitstats.shared.model.overview.OverviewInfo
import io.github.kmp.gitstats.shared.model.tag.TagInfo

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