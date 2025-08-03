package io.github.kmp.gitstats.viewmodel

import io.github.kmp.gitstats.DialogInfo
import io.github.kmp.gitstats.getDirectoryPicker
import io.github.kmp.gitstats.model.AnalysisResult
import io.github.kmp.gitstats.model.ProjectStorage
import io.github.kmp.gitstats.platform.AnalyzeProject
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
    private val _reposState = MutableStateFlow(emptyList<String>())
    val reposState: StateFlow<List<String>> = _reposState

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
        val repos = _reposState.value
        val newRepos = mutableListOf<String>()
        newRepos.addAll(repos)
        newRepos.remove(intent.repo)
        _reposState.value = newRepos
        if (intent.repo == _analysisResult.value.repoPath) {
            _analysisResult.value = AnalysisResult()
        }
        viewModelScope.launch {
            projectStorage.save(newRepos)
            projectStorage.deleteRepoStats(intent.repo)
        }
    }

    private fun handleLoadRepoIntent() {
        viewModelScope.launch {
            val lastRepos = projectStorage.load()
            _reposState.value = lastRepos
        }
    }

    private fun handleAddRepoIntent() {
        val repos = _reposState.value
        val newRepos = mutableListOf<String>()
        newRepos.addAll(repos)
        val selectedFiles = directoryPicker.chooseDirectory()
        for (file in selectedFiles) {
            if (!repos.contains(file)) {
                newRepos.add(file)
            }
        }
        _reposState.value = newRepos
        viewModelScope.launch {
            projectStorage.save(newRepos)
        }
    }

}