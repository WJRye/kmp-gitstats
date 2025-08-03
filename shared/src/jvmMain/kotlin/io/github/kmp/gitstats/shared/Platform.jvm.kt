package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.UserConfig


class JvmPlatform : Platform {
    override val name: String = "Jvm"
}

actual fun getPlatform(): Platform = JvmPlatform()


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