package io.github.kmp.gitstats.shared.platform

import io.github.kmp.gitstats.shared.model.AnalysisResult

interface RepoStorage {
    fun load(): List<String>
    fun save(repos: List<String>)

    fun saveRepoStats(repo: String, stats: AnalysisResult)
    fun loadRepoStats(repo: String): AnalysisResult
    fun deleteRepoStats(repo: String)
}