package io.github.kmp.gitstats.shared.platform

import io.github.kmp.gitstats.shared.model.AnalysisResult

interface GitAnalyzer {

    fun analyze(): AnalysisResult

    fun pull(): Pair<Int, String>
}