package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.UserConfig
import io.github.kmp.gitstats.shared.platform.GitAnalyzer
import io.github.kmp.gitstats.shared.platform.LocalLanguage
import io.github.kmp.gitstats.shared.platform.RepoStorage


expect fun provideGitAnalyzer(repoPath: String): GitAnalyzer
expect fun provideLocaleLanguage(): LocalLanguage


expect object RepoStorageProvider {
    val instance: RepoStorage
}

expect fun getUserConfig(path: String): UserConfig