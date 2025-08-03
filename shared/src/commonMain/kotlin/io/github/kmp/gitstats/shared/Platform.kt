package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.UserConfig

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun provideGitAnalyzer(repoPath: String): GitAnalyzer
expect fun provideLocaleLanguage(): LocalLanguage


expect object RepoStorageProvider {
    val instance: RepoStorage
}

expect fun getUserConfig(path: String): UserConfig