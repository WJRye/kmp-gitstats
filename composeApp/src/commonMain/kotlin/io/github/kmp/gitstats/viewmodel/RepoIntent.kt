package io.github.kmp.gitstats.viewmodel

import io.github.kmp.gitstats.DialogInfo

sealed class RepoIntent {
    class LoadIntent : RepoIntent()
    class AddIntent : RepoIntent()

    class PullIntent(val repo: String, val dialogInfo: DialogInfo) : RepoIntent()
    class AnalyzeIntent(val repo: String, val dialogInfo: DialogInfo) : RepoIntent()

    class DeleteIntent(val repo: String) : RepoIntent()

    class ClickIntent(val repo: String) : RepoIntent()
}