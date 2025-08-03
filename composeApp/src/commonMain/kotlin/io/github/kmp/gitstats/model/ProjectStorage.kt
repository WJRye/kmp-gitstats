package io.github.kmp.gitstats.model

import io.github.kmp.gitstats.RepoStorageProvider
import kotlinx.coroutines.Dispatchers

class ProjectStorage private constructor() {

    companion object Companion {
        @JvmStatic
        fun newInstance() = ProjectStorage()
    }

    suspend fun save(repos: List<String>) {
        with(Dispatchers.IO) {
            RepoStorageProvider.instance.save(repos)
        }
    }

    suspend fun load(): List<String> {
        with(Dispatchers.IO) {
            return RepoStorageProvider.instance.load()
        }
    }

    suspend fun saveRepoStats(repo: String, stats: AnalysisResult) {
        with(Dispatchers.IO) {
            RepoStorageProvider.instance.saveRepoStats(repo, stats)
        }
    }

    suspend fun loadRepoStats(repo: String): AnalysisResult {
        return with(Dispatchers.IO) {
            RepoStorageProvider.instance.loadRepoStats(repo)
        }
    }

    suspend fun deleteRepoStats(repo: String) {
        with(Dispatchers.IO) {
            RepoStorageProvider.instance.deleteRepoStats(repo)
        }
    }
}