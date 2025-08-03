package io.github.kmp.gitstats.model.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorStat(
    val author: String,
    val email: String,
    val avatarUrl: String,
    val commitCount: Int,
    val linesChanged: Long,
    val linesAdded: Long,
    val linesRemoved: Long,
    val filesChanged: Int,
    val activeDays: Int
) {
    var activeTime: String = ""
        internal set(value) {
            field = value
        }

    var commitsPercent: String = ""
        internal set(value) {
            field = value
        }

    var linesPercent: String = ""
        internal set(value) {
            field = value
        }

    var filesPercent: String = ""
        internal set(value) {
            field = value
        }

    val commitReadable: String
        get() = "$commitCount $commitsPercent"
    val fileReadable: String
        get() = "$filesChanged $filesPercent"

    val lineReadable: String
        get() = "$linesChanged (+${linesAdded} / -${linesRemoved}) $linesPercent"
}