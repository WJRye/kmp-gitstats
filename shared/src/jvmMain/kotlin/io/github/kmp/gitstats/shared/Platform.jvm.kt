package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.UserConfig
import io.github.kmp.gitstats.shared.platform.GitAnalyzer
import io.github.kmp.gitstats.shared.platform.JvmGitAnalyzer
import io.github.kmp.gitstats.shared.platform.JvmLocaleLanguage
import io.github.kmp.gitstats.shared.platform.JvmRepoStorage
import io.github.kmp.gitstats.shared.platform.JvmUserConfig
import io.github.kmp.gitstats.shared.platform.LocalLanguage
import io.github.kmp.gitstats.shared.platform.RepoStorage

actual fun provideGitAnalyzer(repoPath: String): GitAnalyzer {
    return JvmGitAnalyzer(repoPath)
}

actual fun provideLocaleLanguage(): LocalLanguage {
    return JvmLocaleLanguage()
}

actual object RepoStorageProvider {
    actual val instance: RepoStorage = JvmRepoStorage()
}

actual fun getUserConfig(path: String): UserConfig = JvmUserConfig.getUserConfig(path)