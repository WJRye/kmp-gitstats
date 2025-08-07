package io.github.kmp.gitstats.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.kmp.gitstats.DialogInfo
import io.github.kmp.gitstats.getDirectoryPicker
import io.github.kmp.gitstats.model.ProjectStorage
import io.github.kmp.gitstats.platform.AnalyzeProject
import io.github.kmp.gitstats.shared.model.AnalysisResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val projectStorage: ProjectStorage = ProjectStorage.newInstance()

    private val _loadingDialog = MutableStateFlow(DialogInfo())
    val loadingDialog: StateFlow<DialogInfo> = _loadingDialog
    private val _repos: SnapshotStateList<String> = mutableStateListOf()
    val repos: List<String> = _repos

    private val _analysisResult = MutableStateFlow(AnalysisResult())
    val analysisResult: StateFlow<AnalysisResult> = _analysisResult

    private val _pullResult = MutableStateFlow("")
    val pullResult: StateFlow<String> = _pullResult

    private val directoryPicker = getDirectoryPicker()


    fun sendRepoIntent(intent: RepoIntent) {
        when (intent) {
            is RepoIntent.LoadIntent -> {
                handleLoadRepoIntent()
            }

            is RepoIntent.AddIntent -> {
                handleAddRepoIntent()
            }

            is RepoIntent.DeleteIntent -> {
                handleDeleteRepoIntent(intent)
            }

            is RepoIntent.AnalyzeIntent -> {
                handleAnalyzeRepoIntent(intent)
            }

            is RepoIntent.PullIntent -> {
                handlePullRepoIntent(intent)
            }

            is RepoIntent.ClickIntent -> {
                handleClickRepoIntent(intent)
            }
        }
    }

    private fun handleClickRepoIntent(intent: RepoIntent.ClickIntent) {
        viewModelScope.launch {
            _analysisResult.value = projectStorage.loadRepoStats(intent.repo)
        }
    }


    private fun handlePullRepoIntent(intent: RepoIntent.PullIntent) {
        viewModelScope.launch {
            _loadingDialog.value = intent.dialogInfo
            _pullResult.value = ""
            val result = AnalyzeProject.pull(intent.repo)
            _pullResult.value = result.second
            _loadingDialog.value = DialogInfo()
        }
    }

    private fun handleAnalyzeRepoIntent(intent: RepoIntent.AnalyzeIntent) {
        viewModelScope.launch {
            _loadingDialog.value = intent.dialogInfo
            _analysisResult.value = AnalyzeProject.analyze(intent.repo)
            projectStorage.saveRepoStats(intent.repo, _analysisResult.value)
            _loadingDialog.value = DialogInfo()
        }
    }

    private fun handleDeleteRepoIntent(intent: RepoIntent.DeleteIntent) {
        _repos.remove(intent.repo)
        if (intent.repo == _analysisResult.value.repoPath) {
            _analysisResult.value = AnalysisResult()
        }
        viewModelScope.launch {
            projectStorage.save(_repos)
            projectStorage.deleteRepoStats(intent.repo)
        }
    }

    private fun handleLoadRepoIntent() {
        viewModelScope.launch {
            val lastRepos = projectStorage.load()
            _repos.addAll(lastRepos)
        }
    }

    private fun handleAddRepoIntent() {
        val selectedFiles = directoryPicker.chooseDirectory()
        _repos.addAll(selectedFiles)
        viewModelScope.launch {
            projectStorage.save(_repos)
        }
    }

}