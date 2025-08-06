package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.UserConfig

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