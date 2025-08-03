package io.github.kmp.gitstats.platform

import io.github.kmp.gitstats.shared.model.AnalysisResult
import io.github.kmp.gitstats.shared.provideGitAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnalyzeProject {
    companion object {
        suspend fun analyze(repo: String): AnalysisResult {
            return withContext(Dispatchers.IO) {
                val analyzer = provideGitAnalyzer(repo)
                analyzer.analyze()
            }
        }

        suspend fun pull(repo: String): Pair<Int, String> {
            return withContext(Dispatchers.IO) {
                val analyzer = provideGitAnalyzer(repo)
                analyzer.pull()
            }
        }
    }
}