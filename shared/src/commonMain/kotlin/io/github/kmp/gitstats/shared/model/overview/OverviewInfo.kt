package io.github.kmp.gitstats.shared.model.overview

import kotlinx.serialization.Serializable

@Serializable
data class OverviewInfo(
    val projectName: String = "",
    val generatedTime: String = "",
    val reportPeriod: String = "",
    val age: String = "",
    val totalFiles: Int = 0,
    val totalLines: Long = 0,
    val addedLines: Long = 0,
    val removedLines: Long = 0,
    val totalCommits: Int = 0,
    val commitsPerActiveDay: String = "",
    val commitsPerAllDays: String = "",
    val totalAuthors: Int = 0,
    val avgCommitsPerAuthor: String = ""
) {
    fun getTable(): Map<String, String> {
        val data = LinkedHashMap<String, String>()
        data.put("Project name", projectName)
        data.put("Generated", generatedTime)
        data.put("Report Period", reportPeriod)
        data.put("Age", age)
        data.put("Total Files", totalFiles.toString())
        data.put(
            "Total Lines of Code",
            "$totalLines (${addedLines} added, ${removedLines} removed)"
        )
        data.put(
            "Total Commits",
            "$totalCommits (average $commitsPerActiveDay per active day, $commitsPerAllDays per all days)"
        )
        data.put("Authors", "$totalAuthors (average $avgCommitsPerAuthor commits per author)")
        return data
    }
}