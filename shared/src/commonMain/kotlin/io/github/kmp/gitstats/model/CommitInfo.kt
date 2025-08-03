package io.github.kmp.gitstats.model

import io.github.kmp.gitstats.DateUtil.Companion.localDateTime
import kotlinx.datetime.LocalDateTime


data class CommitInfo(
    var hash: String = "",
    var author: String = "",
    var email: String = "",
    var date: LocalDateTime = localDateTime(),
    var message: String = "",
    var insertions: Long = 0,
    var deletions: Long = 0,
    var fileInfo: MutableList<CommitFileInfo> = mutableListOf()
) {
    var hasDate: Boolean = false
        internal set
    var lines: Long = 0L
        internal set
}

data class CommitFileInfo(
    val path: String, val insertions: Long, val deletions: Long
)
