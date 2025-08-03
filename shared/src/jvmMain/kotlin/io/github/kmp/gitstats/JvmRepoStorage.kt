package io.github.kmp.gitstats

import io.github.kmp.gitstats.model.AnalysisResult
import kotlinx.serialization.json.Json
import java.io.File
import java.util.prefs.Preferences

class JvmRepoStorage : RepoStorage {
    private val prefs = Preferences.userRoot().node("gitstats")
    private val json = Json { prettyPrint = true }

    companion object {
        private const val KEY = "imported_paths"
        private const val SPLIT = "|"

        private const val GIT_STATS = "gitstats"

        private const val FILE_GIT_STATS_NAME = "$GIT_STATS.json"

        @JvmStatic
        private fun getGitStatsFile(repo: String): File? {
            val repoName = File(repo).name
            val baseDir = AppDirs.getAppDataDir()
            val gitStatsDir = File(baseDir, repoName)
            if (!gitStatsDir.exists()) {
                gitStatsDir.mkdirs()
            }
            return File(gitStatsDir, FILE_GIT_STATS_NAME)
        }
    }

    override fun load(): List<String> {
        val value = prefs.get(KEY, "")
        return value.split(SPLIT).filter { it.isNotBlank() }
    }

    override fun save(repos: List<String>) {
        prefs.put(KEY, repos.joinToString(SPLIT))
    }

    override fun saveRepoStats(
        repo: String, stats: AnalysisResult
    ) {
        val file = getGitStatsFile(repo)
        file?.writeText(json.encodeToString(stats))
    }

    override fun loadRepoStats(repo: String): AnalysisResult {
        val file = getGitStatsFile(repo)
        if (file == null || !file.exists()) return AnalysisResult()
        return try {
            json.decodeFromString<AnalysisResult>(file.readText())
        } catch (e: Exception) {
            AnalysisResult()
        }
    }

    override fun deleteRepoStats(repo: String) {
        getGitStatsFile(repo)?.parentFile?.deleteRecursively()
    }
}