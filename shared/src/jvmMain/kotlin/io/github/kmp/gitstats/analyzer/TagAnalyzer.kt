package io.github.kmp.gitstats.analyzer

import io.github.kmp.gitstats.DateUtil.Companion.isoDateToLocalDateTime
import io.github.kmp.gitstats.GIT_TAG
import io.github.kmp.gitstats.GIT_TAG_SHA
import io.github.kmp.gitstats.GIT_TAG_SHA_INFO
import io.github.kmp.gitstats.model.tag.TagInfo
import io.github.kmp.gitstats.model.tag.TagStat
import io.github.kmp.gitstats.processed

class TagAnalyzer(val repoPath: String) {


    fun getTagInfo(): TagInfo {
        val stats = computeTagStats()
        return TagInfo(stats)
    }

    companion object {
        private const val TYPE_TAG = "tag"
        private const val TYPE_COMMIT = "commit"
    }

    private fun computeTagStats(): List<TagStat> {
        return processed(repoPath, GIT_TAG).map { line ->
            val parts = line.split("\t")
            TagStat(
                name = parts.getValue(0),
                type = parts.getValue(1),
                sha = parts.getValue(2),
                tagger = parts.getValue(3),
                date = isoDateToLocalDateTime(parts.getValue(4)),
                message = parts.getValue(5),
            )
        }.map { tagStat ->
            if (tagStat.type == TYPE_COMMIT && tagStat.tagger.isEmpty()) {
                val sha = processed(repoPath, GIT_TAG_SHA + tagStat.name)[0]
                val commitInfo = processed(repoPath, GIT_TAG_SHA_INFO + sha)
                tagStat.copy(
                    sha = sha,
                    tagger = commitInfo.getValue(0),
                    date = isoDateToLocalDateTime(commitInfo.getValue(1)),
                    message = commitInfo.getValue(2),
                )
            } else tagStat
        }.sortedByDescending { it.date }
    }

    private fun List<String>.getValue(index: Int): String {
        return this.getOrElse(index, { "" })
    }
}