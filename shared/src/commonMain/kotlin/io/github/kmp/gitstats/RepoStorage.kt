package io.github.kmp.gitstats

import io.github.kmp.gitstats.model.AnalysisResult

interface RepoStorage {
    fun load(): List<String>
    fun save(repos: List<String>)

    fun saveRepoStats(repo: String, stats: AnalysisResult)
    fun loadRepoStats(repo: String): AnalysisResult
    fun deleteRepoStats(repo: String)
}